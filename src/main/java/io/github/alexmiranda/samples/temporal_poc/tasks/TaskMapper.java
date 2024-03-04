package io.github.alexmiranda.samples.temporal_poc.tasks;

import io.github.alexmiranda.samples.temporal_poc.onboarding.OnboardingCase;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskIn;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskOut;
import io.github.alexmiranda.samples.temporal_poc.messages.OnboardingRequestIn;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toEntity(CreateTaskIn in);

    @Mapping(source = "id", target = "taskId")
    CreateTaskOut toCreateTaskOut(Task entity);

    @Mapping(target = "completed", ignore = true)
    OnboardingRequestIn toOnboardingRequestIn(OnboardingCase entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfBirth", dateFormat = "yyyy-MM-dd")
    void update(@MappingTarget OnboardingCase entity, OnboardingRequestIn in);

    @AfterMapping
    default void updateFees(@MappingTarget OnboardingCase entity) {
        entity.recalculateFees();
    }

    @Condition
    static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}
