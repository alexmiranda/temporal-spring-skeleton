package io.github.alexmiranda.samples.temporal_poc.domain;

import jakarta.persistence.*;
import lombok.Getter;

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

    protected Task() {
    }

    public Task(long caseId, TaskType taskType) {
        this.caseId = caseId;
        this.taskType = taskType;
        this.createdAt = LocalDateTime.now();
    }
}
