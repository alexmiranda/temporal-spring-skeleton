package io.github.alexmiranda.samples.temporal_poc.tasks;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Getter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private long caseId;

    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Setter
    @Column(name = "priority")
    private TaskPriority priority = TaskPriority.NORMAL;

    protected Task() {
    }

    public Task(long caseId, TaskType taskType) {
        this.caseId = caseId;
        this.taskType = taskType;
        this.createdAt = LocalDateTime.now();
    }

    public void markCompleted() {
        this.completedAt = LocalDateTime.now();
    }
}
