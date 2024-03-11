package io.github.alexmiranda.samples.temporal_poc.messages;

import io.github.alexmiranda.samples.temporal_poc.tasks.TaskType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TaskCompleted {
    private final Long taskId;
    private final String workflowId;
    private final TaskType taskType;
    private final boolean approved;
    private final boolean screeningRequired;
    private final CustomerData customerData;
}
