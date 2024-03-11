package io.github.alexmiranda.samples.temporal_poc.tasks;

import io.github.alexmiranda.samples.temporal_poc.messages.UpdateTaskPriorityIn;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskIn;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskOut;
import io.github.alexmiranda.samples.temporal_poc.messages.UpdateTaskPriorityOut;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/rest/v1/task")
@RequiredArgsConstructor
public class TaskRestController {
    private final TaskRepository repository;
    private final TaskMapper mapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<CreateTaskOut> create(@RequestBody CreateTaskIn in) {
        var entity = mapper.toEntity(in);
        entity = repository.save(entity);
        var responseBody = mapper.toCreateTaskOut(entity);
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{taskId}").buildAndExpand(responseBody.getTaskId()).toUri();
        return ResponseEntity.created(location).body(responseBody);
    }

    @PutMapping(path = "/{taskId}/priority", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<UpdateTaskPriorityOut> changePriority(@PathVariable Long taskId, @RequestBody UpdateTaskPriorityIn in) {
        var entity = repository.findById(taskId).get();
        mapper.update(entity, in);
        entity = repository.save(entity);
        var responseBody = mapper.toUpdateTaskPriorityOut(entity);
        return ResponseEntity.ok(responseBody);
    }
}
