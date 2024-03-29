package io.github.alexmiranda.samples.temporal_poc.screening;

import io.github.alexmiranda.samples.temporal_poc.messages.CustomerData;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AdditionalScreeningWorkflow {
    @WorkflowMethod
    boolean performScreening(CustomerData customerData);
}
