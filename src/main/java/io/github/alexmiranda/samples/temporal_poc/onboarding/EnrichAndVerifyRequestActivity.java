package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface EnrichAndVerifyRequestActivity {
    String createTask(String caseId);
}
