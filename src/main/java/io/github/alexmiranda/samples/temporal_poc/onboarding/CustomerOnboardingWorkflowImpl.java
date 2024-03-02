package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class CustomerOnboardingWorkflowImpl implements CustomerOnboardingWorkflow {
    private final ActivityOptions options = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofSeconds(60))
        .build();

    private final EnrichAndVerifyRequestActivity enrichAndVerifyRequestActivity = Workflow.newActivityStub(EnrichAndVerifyRequestActivity.class, options);

    @Override
    public void execute(String caseId) {
        log.info("New workflow started with caseId {}", caseId);
        enrichAndVerifyRequestActivity.createTask(caseId);
        log.info("Workflow completed with caseId {}", caseId);
    }
}
