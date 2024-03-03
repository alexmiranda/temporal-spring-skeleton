package io.github.alexmiranda.samples.temporal_poc.screening;

public class AdditionalScreeningWorkflowImpl implements AdditionalScreeningWorkflow {
    @Override
    public boolean performScreening(String caseId) {
        // this is a dummy implementation of a child workflow that just blocks the parent workflow until it finishes
        return true;
    }
}
