package io.github.alexmiranda.samples.temporal_poc;

import io.github.alexmiranda.samples.temporal_poc.hello.GreetingActivityImpl;
import io.github.alexmiranda.samples.temporal_poc.hello.GreetingWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    @Profile("!test")
    static class TemporalApplicationRunner implements ApplicationRunner {
        private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

        @Value("${TEMPORAL_ADDRESS}")
        private String temporalAddress;

        @Value("${TEMPORAL_NAMESPACE}")
        private String temporalNamespace;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            try {
                var serviceOptions = WorkflowServiceStubsOptions.newBuilder().setTarget(temporalAddress).build();
                var service = WorkflowServiceStubs.newConnectedServiceStubs(serviceOptions, Duration.ofSeconds(60));
                var clientOptions = WorkflowClientOptions.newBuilder().setNamespace(temporalNamespace).build();
                var client = WorkflowClient.newInstance(service, clientOptions);
                var factory = WorkerFactory.newInstance(client);
                var worker = factory.newWorker("HelloWorldTaskQueue");
                worker.registerWorkflowImplementationTypes(GreetingWorkflowImpl.class);
                worker.registerActivitiesImplementations(new GreetingActivityImpl());
                factory.start();
            } catch (Throwable e) {
                logger.error("Failed to create Temporal client", e);
            }
        }
    }
}
