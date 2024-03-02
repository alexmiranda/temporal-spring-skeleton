package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface CustomerOnboardingActivities {
    String createTask(String caseId, String taskType);
}
