package io.github.alexmiranda.samples.temporal_poc.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskPriorityIn {
    private String priority;
}
