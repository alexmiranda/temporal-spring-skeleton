package io.github.alexmiranda.samples.temporal_poc.onboarding;

import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskIn;
import io.github.alexmiranda.samples.temporal_poc.messages.CreateTaskOut;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/api/rest/v1/task", accept = MediaType.APPLICATION_JSON_VALUE)
public interface TaskServiceClient {
    @PostExchange()
    ResponseEntity<CreateTaskOut> create(@RequestBody CreateTaskIn in);
}
