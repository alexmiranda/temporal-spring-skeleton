package io.github.alexmiranda.samples.temporal_poc.controllers;

import io.github.alexmiranda.samples.temporal_poc.messages.TaskCompleted;
import io.github.alexmiranda.samples.temporal_poc.onboarding.CustomerOnboardingWorkflow;
import io.temporal.client.WorkflowClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskCompletedListener {
    private final WorkflowClient workflowClient;

    @EventListener
    public void handleTaskCompleted(TaskCompleted event) {
        var workflow = workflowClient.newWorkflowStub(CustomerOnboardingWorkflow.class, event.getWorkflowId());
        switch (event.getTaskType()) {
            case EnrichAndVerifyRequest -> workflow.signalCaseVerified(Long.toString(event.getTaskId()));
        }
    }
}