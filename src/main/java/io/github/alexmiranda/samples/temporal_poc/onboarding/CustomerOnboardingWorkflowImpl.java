package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CustomerOnboardingWorkflowImpl implements CustomerOnboardingWorkflow {
    private final ActivityOptions options = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(60))
        .build();

    private final EnrichAndVerifyRequestActivity enrichAndVerifyRequestActivity = Workflow.newActivityStub(EnrichAndVerifyRequestActivity.class, options);

    private boolean caseVerified = false;
    private List<String> taskList = new ArrayList<>();

    @Override
    public void execute(String caseId) {
        log.info("New workflow started with caseId {}", caseId);
        enrichAndVerifyRequestActivity.createTask(caseId);
        Workflow.await(() -> caseVerified);
        log.info("Workflow completed with caseId {}", caseId);
    }

    @Override
    public void signalCaseVerified(String taskId) {
        log.info("Signal signalCaseVerified with taskId {} received", taskId);
        this.caseVerified = true;
        this.taskList.add(taskId);
        log.info("Signal signalCaseVerified with taskId {} completed", taskId);
    }
}
