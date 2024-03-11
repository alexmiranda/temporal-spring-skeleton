package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskIn;
import io.github.alexmiranda.samples.temporal_poc.messages.UpdateTaskPriorityIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerOnboardingActivitiesImpl implements CustomerOnboardingActivities {
    private final TaskServiceClient taskServiceClient;

    @Override
    public String createTask(String caseId, String taskType) {
        try {
            log.info("Activity {} started", CustomerOnboardingActivitiesImpl.class);
            var payload = CreateTaskIn.builder().taskType(taskType).caseId(caseId).build();
            var response = taskServiceClient.create(payload);
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

    @Override
    public void escalateTaskPriority(String taskId) {
        try {
            log.info("Activity {} started", "escalateTaskPriority");
            var payload = new UpdateTaskPriorityIn("HIGH");
            taskServiceClient.changePriority(taskId, payload);
        } catch (Throwable e) {
            log.error("Activity failed", e);
            throw e;
        }
    }
}
