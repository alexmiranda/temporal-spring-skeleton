package io.github.alexmiranda.samples.temporal_poc.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "onboarding_case")
@Getter
public class OnboardingCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "workflow_id", nullable = false)
    UUID workflowId;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "request_type", nullable = false)
    private String requestType;

    protected OnboardingCase() {
    }

    public OnboardingCase(UUID workflowId, String branchName, String requestType) {
        this.workflowId = workflowId;
        this.branchName = branchName;
        this.requestType = requestType;
    }
}
