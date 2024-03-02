package io.github.alexmiranda.samples.temporal_poc.controllers;

import io.github.alexmiranda.samples.temporal_poc.domain.OnboardingCase;
import io.github.alexmiranda.samples.temporal_poc.domain.OnboardingCaseRepository;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateCaseIn;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateCaseOut;
import io.github.alexmiranda.samples.temporal_poc.onboarding.CustomerOnboardingWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/rest/v1/customer-onboarding")
@RequiredArgsConstructor
public class CustomerOnboardingController {
    private final WorkflowClient workflowClient;
    private final OnboardingCaseRepository repository;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateCaseOut> start(@RequestBody CreateCaseIn in) {
        var workflowId = UUID.randomUUID();
        var entity = new OnboardingCase(workflowId, in.getBranchName(), in.getBranchName());
        entity = repository.save(entity);
        var workflow = workflowClient.newWorkflowStub(CustomerOnboardingWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId.toString())
                .setWorkflowRunTimeout(Duration.ofSeconds(10))
                .setTaskQueue("CustomerOnboardingTaskQueue")
                .build());
        workflow.execute(entity.getId());
        var responseBody = CreateCaseOut.builder().caseId(Long.toString(entity.getId())).build();
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{caseId}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(location).body(responseBody);
    }
}
