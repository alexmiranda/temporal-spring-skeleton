package io.github.alexmiranda.samples.temporal_poc.tasks;

import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
}
