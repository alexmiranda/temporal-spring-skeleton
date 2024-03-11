package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.github.alexmiranda.samples.temporal_poc.screening.AdditionalScreeningWorkflow;
import io.github.alexmiranda.samples.temporal_poc.tasks.TaskType;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CustomerOnboardingWorkflowTests {
    private static final int TIMEOUT = 10;

    @RegisterExtension
    TestWorkflowExtension testWorkflowExtension = TestWorkflowExtension.newBuilder()
        .setWorkflowTypes(CustomerOnboardingWorkflowImpl.class)
        .setDoNotStart(true)
        .build();

    @Test
    public void testHappyPath(TestWorkflowEnvironment testEnv, Worker worker, CustomerOnboardingWorkflow workflow) throws Exception {
        var activity = mock(CustomerOnboardingActivities.class, withSettings().withoutAnnotations());
        doAnswer(i -> {
            var taskType = Enum.valueOf(TaskType.class, i.getArgument(1, String.class));
            switch (taskType) {
                case EnrichAndVerifyRequest -> workflow.signalCaseVerified("testTaskId1");
                case ReviewAndAmendRequest -> workflow.signalCaseReviewed("testTaskId2", true, false);
                case FinaliseAgreementRequest -> workflow.signalAgreementFinalised("testTaskId3");
            }
            return "";
        }).when(activity).createTask(anyString(), anyString());
        worker.registerActivitiesImplementations(activity);
        testEnv.start();

        WorkflowClient.start(workflow::execute, "testCaseId");
        var untyped = WorkflowStub.fromTyped(workflow);
        untyped.getResult(TIMEOUT, SECONDS, Void.class);

        var inOrder = inOrder(activity);
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("EnrichAndVerifyRequest"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("ReviewAndAmendRequest"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("FinaliseAgreementRequest"));
    }

    @Test
    public void testEnrichAndVerifyTimeout(TestWorkflowEnvironment testEnv, Worker worker, CustomerOnboardingWorkflow workflow) throws Exception {
        var activity = mock(CustomerOnboardingActivities.class, withSettings().withoutAnnotations());
        doReturn("testTaskId1").when(activity).createTask(anyString(), anyString());
        doNothing().when(activity).escalateTaskPriority(anyString());
        worker.registerActivitiesImplementations(activity);
        testEnv.start();

        WorkflowClient.start(workflow::execute, "testCaseId");
        testEnv.sleep(Duration.ofMinutes(61));

        var untyped = WorkflowStub.fromTyped(workflow);
        untyped.cancel();

        var inOrder = inOrder(activity);
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("EnrichAndVerifyRequest"));
        inOrder.verify(activity, times(1)).escalateTaskPriority(eq("testTaskId1"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAmendAndReviewTimeout(TestWorkflowEnvironment testEnv, Worker worker, CustomerOnboardingWorkflow workflow) throws Exception {
        var activity = mock(CustomerOnboardingActivities.class, withSettings().withoutAnnotations());
        var counter = new AtomicInteger(0);
        doAnswer(i -> {
            var taskType = Enum.valueOf(TaskType.class, i.getArgument(1, String.class));
            var taskId = String.format("testTaskId%d", counter.incrementAndGet());
            if (taskType == TaskType.EnrichAndVerifyRequest) {
                testEnv.registerDelayedCallback(Duration.ofMillis(500), () -> workflow.signalCaseVerified(taskId));
            }
            return taskId;
        }).when(activity).createTask(anyString(), anyString());
        doNothing().when(activity).escalateTaskPriority(anyString());
        worker.registerActivitiesImplementations(activity);
        testEnv.start();

        WorkflowClient.start(workflow::execute, "testCaseId");
        testEnv.sleep(Duration.ofDays(3));

        var untyped = WorkflowStub.fromTyped(workflow);
        untyped.cancel();

        var inOrder = inOrder(activity);
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("EnrichAndVerifyRequest"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("ReviewAndAmendRequest"));
        inOrder.verify(activity, times(1)).escalateTaskPriority(eq("testTaskId2"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testRejectCase(TestWorkflowEnvironment testEnv, Worker worker, CustomerOnboardingWorkflow workflow) throws Exception {
        var activity = mock(CustomerOnboardingActivities.class, withSettings().withoutAnnotations());
        var countDownLatch = new CountDownLatch(3);
        doAnswer(i -> {
            try {
                var taskType = Enum.valueOf(TaskType.class, i.getArgument(1, String.class));
                switch (taskType) {
                    case EnrichAndVerifyRequest -> workflow.signalCaseVerified("testTaskId1");
                    case ReviewAndAmendRequest -> workflow.signalCaseReviewed("testTaskId2", false, false);
                }
                return "";
            } finally {
                countDownLatch.countDown();
            }
        }).when(activity).createTask(anyString(), anyString());
        worker.registerActivitiesImplementations(activity);
        testEnv.start();

        WorkflowClient.start(workflow::execute, "testCaseId");

        if (!countDownLatch.await(TIMEOUT, SECONDS)) {
            fail("Failed to run workflow until expected pointed in time");
        }

        // because of the delay in sending back a signal, you have to await a little longer
        testEnv.sleep(Duration.ofMillis(501));
        var untyped = WorkflowStub.fromTyped(workflow);
        untyped.terminate("cancelled by test");

        var inOrder = inOrder(activity);
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("EnrichAndVerifyRequest"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("ReviewAndAmendRequest"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("EnrichAndVerifyRequest"));
    }

    @Test
    public void testAdditionalScreeningPassed(TestWorkflowEnvironment testEnv, Worker worker, CustomerOnboardingWorkflow workflow) throws Exception {
        var activity = mock(CustomerOnboardingActivities.class, withSettings().withoutAnnotations());
        doAnswer(i -> {
            var taskType = Enum.valueOf(TaskType.class, i.getArgument(1, String.class));
            switch (taskType) {
                case EnrichAndVerifyRequest -> testEnv.registerDelayedCallback(Duration.ofMillis(500), () -> workflow.signalCaseVerified("testTaskId1"));
                case ReviewAndAmendRequest -> testEnv.registerDelayedCallback(Duration.ofMillis(500), () -> workflow.signalCaseReviewed("testTaskId2", true, true));
                case FinaliseAgreementRequest -> testEnv.registerDelayedCallback(Duration.ofMillis(500), () -> workflow.signalAgreementFinalised("testTaskId3"));
            }
            return "";
        }).when(activity).createTask(anyString(), anyString());
        worker.registerActivitiesImplementations(activity);
        var additionalScreeningWorkflow = mock(AdditionalScreeningWorkflow.class, withSettings().withoutAnnotations());
        doReturn(true).when(additionalScreeningWorkflow).performScreening(anyString());
        worker.registerWorkflowImplementationFactory(AdditionalScreeningWorkflow.class, () -> additionalScreeningWorkflow);
        testEnv.start();

        WorkflowClient.start(workflow::execute, "testCaseId");
        var untyped = WorkflowStub.fromTyped(workflow);
        untyped.getResult(TIMEOUT, SECONDS, Void.class);

        var inOrder = inOrder(activity, additionalScreeningWorkflow);
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("EnrichAndVerifyRequest"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("ReviewAndAmendRequest"));
        inOrder.verify(additionalScreeningWorkflow, times(1)).performScreening(eq("testCaseId"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("FinaliseAgreementRequest"));
    }

    @Test
    public void testAdditionalScreeningFailed(TestWorkflowEnvironment testEnv, Worker worker, CustomerOnboardingWorkflow workflow) throws Exception {
        var activity = mock(CustomerOnboardingActivities.class, withSettings().withoutAnnotations());
        doAnswer(i -> {
            var taskType = Enum.valueOf(TaskType.class, i.getArgument(1, String.class));
            switch (taskType) {
                case EnrichAndVerifyRequest -> testEnv.registerDelayedCallback(Duration.ofMillis(500), () -> workflow.signalCaseVerified("testTaskId1"));
                case ReviewAndAmendRequest -> testEnv.registerDelayedCallback(Duration.ofMillis(500), () -> workflow.signalCaseReviewed("testTaskId2", true, true));
                case FinaliseAgreementRequest -> testEnv.registerDelayedCallback(Duration.ofMillis(500), () -> workflow.signalAgreementFinalised("testTaskId3"));
                case ApologiseAndAdviseRequest -> testEnv.registerDelayedCallback(Duration.ofMillis(500), () -> workflow.signalApologySent("testTaskId4"));
            }
            return "";
        }).when(activity).createTask(anyString(), anyString());
        worker.registerActivitiesImplementations(activity);
        var additionalScreeningWorkflow = mock(AdditionalScreeningWorkflow.class, withSettings().withoutAnnotations());
        doReturn(false).when(additionalScreeningWorkflow).performScreening(anyString());
        worker.registerWorkflowImplementationFactory(AdditionalScreeningWorkflow.class, () -> additionalScreeningWorkflow);
        testEnv.start();

        WorkflowClient.start(workflow::execute, "testCaseId");
        var untyped = WorkflowStub.fromTyped(workflow);
        untyped.getResult(TIMEOUT, SECONDS, Void.class);

        var inOrder = inOrder(activity, additionalScreeningWorkflow);
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("EnrichAndVerifyRequest"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("ReviewAndAmendRequest"));
        inOrder.verify(additionalScreeningWorkflow, times(1)).performScreening(eq("testCaseId"));
        inOrder.verify(activity, times(1)).createTask(eq("testCaseId"), eq("ApologiseAndAdviseRequest"));
        inOrder.verifyNoMoreInteractions();
    }
}
