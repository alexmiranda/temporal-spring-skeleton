package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CustomerOnboardingWorkflow {
    @WorkflowMethod
    void execute(String caseId);

    @SignalMethod
    void signalCaseVerified(String taskId);

    @SignalMethod
    void signalCaseReviewed(String taskId, boolean approved, boolean screeningRequired);

    @SignalMethod
    void signalAgreementFinalised(String taskId);
}
