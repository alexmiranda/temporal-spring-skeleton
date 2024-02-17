package io.github.alexmiranda.samples.temporal_poc.hello;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GreetingWorkflow {
    @WorkflowMethod
    String greeting(String name);
}
