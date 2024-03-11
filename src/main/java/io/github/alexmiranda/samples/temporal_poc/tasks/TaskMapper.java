package io.github.alexmiranda.samples.temporal_poc.tasks;

import io.github.alexmiranda.samples.temporal_poc.messages.*;
import io.github.alexmiranda.samples.temporal_poc.onboarding.OnboardingCase;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "priority", ignore = true)
    Task toEntity(CreateTaskIn in);

    @Mapping(source = "id", target = "taskId")
    CreateTaskOut toCreateTaskOut(Task entity);

    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "action", ignore = true)
    OnboardingRequestIn toOnboardingRequestIn(OnboardingCase entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfBirth", dateFormat = "yyyy-MM-dd")
    void update(@MappingTarget OnboardingCase entity, OnboardingRequestIn in);

    void update(@MappingTarget Task entity, UpdateTaskPriorityIn in);

    @Mapping(target = "taskId", source = "id")
    UpdateTaskPriorityOut toUpdateTaskPriorityOut(Task entity);

    @AfterMapping
    default void updateFees(@MappingTarget OnboardingCase entity) {
        entity.recalculateFees();
    }

    CustomerData toCustomerData(OnboardingCase entity);

    @Condition
    static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

}
