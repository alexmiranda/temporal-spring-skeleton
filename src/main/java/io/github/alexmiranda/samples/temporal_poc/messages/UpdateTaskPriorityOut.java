package io.github.alexmiranda.samples.temporal_poc.messages;

import lombok.Data;

@Data
public class UpdateTaskPriorityOut {
    private String taskId;
    private String priority;
}
