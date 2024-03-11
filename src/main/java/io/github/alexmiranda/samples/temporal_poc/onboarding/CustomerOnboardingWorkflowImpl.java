package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.github.alexmiranda.samples.temporal_poc.screening.AdditionalScreeningWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.ChildWorkflowOptions;
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

    private final CustomerOnboardingActivities customerOnboardingActivities = Workflow.newActivityStub(CustomerOnboardingActivities.class, options);

    private final List<String> taskList = new ArrayList<>();

    private boolean caseVerified = false;
    private boolean caseReviewed = false;
    private boolean caseApproved = false;
    private boolean screeningRequired = false;
    private boolean screeningPassed = false;
    private boolean agreementFinalised = false;
    private boolean apologySent = false;

    @Override
    public void execute(String caseId) {
        log.info("New workflow started with caseId {}", caseId);
        do {
            customerOnboardingActivities.createTask(caseId, "EnrichAndVerifyRequest");
            Workflow.await(() -> this.caseVerified);
            customerOnboardingActivities.createTask(caseId, "ReviewAndAmendRequest");
            Workflow.await(() -> this.caseReviewed);
            if (this.caseReviewed && !this.caseApproved) {
                this.caseVerified = false;
                this.caseReviewed = false;
            }
        } while (!this.caseApproved);

        var caseToBeRejected = false;
        if (this.screeningRequired) {
            invokeAdditionalScreening(caseId);
            if (!this.screeningPassed) {
                caseToBeRejected = true;
            }
        }

        if (!caseToBeRejected) {
            customerOnboardingActivities.createTask(caseId, "FinaliseAgreementRequest");
            Workflow.await(() -> this.agreementFinalised);
        }

        if (caseToBeRejected) {
            customerOnboardingActivities.createTask(caseId, "ApologiseAndAdviseRequest");
            Workflow.await(() -> this.apologySent);
        }

        log.info("Workflow completed with caseId {}", caseId);
    }

    private void invokeAdditionalScreening(String caseId) {
        var childWorkflowId = Workflow.randomUUID().toString();
        var options = ChildWorkflowOptions.newBuilder()
            .setWorkflowId(childWorkflowId)
            .build();
        var additionalScreeningWorkflow = Workflow.newChildWorkflowStub(AdditionalScreeningWorkflow.class, options);
        this.screeningPassed = additionalScreeningWorkflow.performScreening(caseId);
    }

    @Override
    public void signalCaseVerified(String taskId) {
        log.info("Signal signalCaseVerified with taskId {} received", taskId);
        this.caseVerified = true;
        this.taskList.add(taskId);
        log.info("Signal signalCaseVerified with taskId {} completed", taskId);
    }

    @Override
    public void signalCaseReviewed(String taskId, boolean approved, boolean screeningRequired) {
        log.info("Signal signalCaseReviewed with taskId {} received", taskId);
        this.caseReviewed = true;
        this.caseApproved = approved;
        this.screeningRequired = screeningRequired;
        this.taskList.add(taskId);
        log.info("Signal signalCaseReviewed with taskId {} completed", taskId);
    }

    @Override
    public void signalAgreementFinalised(String taskId) {
        log.info("Signal signalAgreementFinalised with taskId {} received", taskId);
        this.agreementFinalised = true;
        this.taskList.add(taskId);
        log.info("Signal signalAgreementFinalised with taskId {} completed", taskId);
    }

    @Override
    public void signalApologySent(String taskId) {
        log.info("Signal signalApologySent with taskId {} received", taskId);
        this.apologySent = true;
        this.taskList.add(taskId);
        log.info("Signal signalApologySent with taskId {} completed", taskId);
    }
}
