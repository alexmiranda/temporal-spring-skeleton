package io.github.alexmiranda.samples.temporal_poc.hello;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class GreetingWorkflowImpl implements GreetingWorkflow {
    private final ActivityOptions options = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(60))
        .build();
    private final GreetingActivity activity = Workflow.newActivityStub(GreetingActivity.class, options);

    @Override
    public String greeting(String name) {
        return activity.greet(name);
    }
}
