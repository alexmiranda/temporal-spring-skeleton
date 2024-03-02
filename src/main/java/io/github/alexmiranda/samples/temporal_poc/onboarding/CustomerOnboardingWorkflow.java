package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CustomerOnboardingWorkflow {
    @WorkflowMethod
    void execute(String caseId);
}
