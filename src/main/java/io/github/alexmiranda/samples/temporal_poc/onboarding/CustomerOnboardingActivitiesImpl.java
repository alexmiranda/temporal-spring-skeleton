package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.github.alexmiranda.samples.temporal_poc.domain.TaskClient;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerOnboardingActivitiesImpl implements CustomerOnboardingActivities {
    private final TaskClient taskClient;

    @Override
    public String createTask(String caseId, String taskType) {
        try {
            log.info("Activity {} started", CustomerOnboardingActivitiesImpl.class);
            var payload = CreateTaskIn.builder().taskType(taskType).caseId(caseId).build();
            var response = taskClient.create(payload);
            if (response.getStatusCode().is2xxSuccessful()) {
                var body = Objects.requireNonNull(response.getBody());
                return body.getTaskId();
            }
            // FIXME: handle error appropriately
            return "";
        } catch (Throwable e) {
            log.error("Activity failed", e);
            return "";
        }
    }
}
