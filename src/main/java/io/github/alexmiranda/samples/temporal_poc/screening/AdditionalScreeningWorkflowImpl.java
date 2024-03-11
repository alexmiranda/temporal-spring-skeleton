package io.github.alexmiranda.samples.temporal_poc.screening;

import io.github.alexmiranda.samples.temporal_poc.messages.CustomerData;

public class AdditionalScreeningWorkflowImpl implements AdditionalScreeningWorkflow {
    @Override
    public boolean performScreening(CustomerData customerData) {
        return true;
    }
}
