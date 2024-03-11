package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskIn;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskOut;
import io.github.alexmiranda.samples.temporal_poc.messages.UpdateTaskPriorityIn;
import io.github.alexmiranda.samples.temporal_poc.messages.UpdateTaskPriorityOut;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(url = "/api/rest/v1/task", accept = MediaType.APPLICATION_JSON_VALUE)
public interface TaskServiceClient {
    @PostExchange()
    ResponseEntity<CreateTaskOut> create(@RequestBody CreateTaskIn in);

    @PutExchange(url = "/{taskId}/priority")
    ResponseEntity<UpdateTaskPriorityOut> changePriority(@PathVariable("taskId") String taskId, @RequestBody UpdateTaskPriorityIn in);
}
