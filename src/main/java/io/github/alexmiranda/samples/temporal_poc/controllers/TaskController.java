package io.github.alexmiranda.samples.temporal_poc.controllers;

import io.github.alexmiranda.samples.temporal_poc.domain.TaskRepository;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskIn;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskOut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/rest/v1/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository repository;
    private final TaskMapper mapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateTaskOut> create(@RequestBody CreateTaskIn in) {
        var entity = mapper.toEntity(in);
        entity = repository.save(entity);
        var responseBody = mapper.toCreateTaskOut(entity);
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{taskId}").buildAndExpand(responseBody.getTaskId()).toUri();
        return ResponseEntity.created(location).body(responseBody);
    }
}
