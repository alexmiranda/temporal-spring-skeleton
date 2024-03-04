package io.github.alexmiranda.samples.temporal_poc;

import io.github.alexmiranda.samples.temporal_poc.onboarding.TaskServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {
    @Bean
    TaskServiceClient taskClient(@Value("${server.port}") String serverPort) {
        var client = WebClient.builder().baseUrl("http://localhost:" + serverPort).build();
        var factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(TaskServiceClient.class);
    }
}
