package io.github.alexmiranda.samples.temporal_poc.messages;

import io.github.alexmiranda.samples.temporal_poc.domain.TaskType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TaskCompleted {
    private final Long taskId;
    private final String workflowId;
    private final TaskType taskType;
}
