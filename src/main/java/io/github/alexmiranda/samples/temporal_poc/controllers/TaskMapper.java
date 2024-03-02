package io.github.alexmiranda.samples.temporal_poc.controllers;

import io.github.alexmiranda.samples.temporal_poc.domain.OnboardingCase;
import io.github.alexmiranda.samples.temporal_poc.domain.Task;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskIn;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskOut;
import io.github.alexmiranda.samples.temporal_poc.messages.EnrichAndVerifyRequestIn;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toEntity(CreateTaskIn in);

    @Mapping(source = "id", target = "taskId")
    CreateTaskOut toCreateTaskOut(Task entity);

    @Mapping(target = "completed", ignore = true)
    EnrichAndVerifyRequestIn toEnrichAndVerifyRequestIn(OnboardingCase entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfBirth", dateFormat = "yyyy-MM-dd")
    void update(@MappingTarget OnboardingCase entity, EnrichAndVerifyRequestIn in);

    @Condition
    static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}
