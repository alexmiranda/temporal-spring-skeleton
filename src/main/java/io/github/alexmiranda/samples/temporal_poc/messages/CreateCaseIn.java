package io.github.alexmiranda.samples.temporal_poc.messages;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class CreateCaseIn {
    private final String branchName;
    private final String requestType;
}
