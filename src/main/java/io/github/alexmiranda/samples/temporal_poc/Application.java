package io.github.alexmiranda.samples.temporal_poc;

import io.github.alexmiranda.samples.temporal_poc.onboarding.OnboardingCase;
import io.github.alexmiranda.samples.temporal_poc.onboarding.OnboardingCaseRepository;
import io.github.alexmiranda.samples.temporal_poc.onboarding.CustomerOnboardingWorkflow;
import io.github.alexmiranda.samples.temporal_poc.onboarding.CustomerOnboardingWorkflowImpl;
import io.github.alexmiranda.samples.temporal_poc.onboarding.CustomerOnboardingActivities;
import io.github.alexmiranda.samples.temporal_poc.screening.AdditionalScreeningWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

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

        @Autowired
        private ApplicationContext applicationContext;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            try {
                var serviceOptions = WorkflowServiceStubsOptions.newBuilder().setTarget(temporalAddress).build();
                var service = WorkflowServiceStubs.newConnectedServiceStubs(serviceOptions, Duration.ofSeconds(60));
                var clientOptions = WorkflowClientOptions.newBuilder().setNamespace(temporalNamespace).build();
                var client = WorkflowClient.newInstance(service, clientOptions);
                var factory = WorkerFactory.newInstance(client);
                var worker = factory.newWorker("CustomerOnboardingTaskQueue");
                worker.registerWorkflowImplementationTypes(CustomerOnboardingWorkflowImpl.class, AdditionalScreeningWorkflowImpl.class);
                worker.registerActivitiesImplementations(applicationContext.getBean(CustomerOnboardingActivities.class));
                factory.start();

                // create a few test data...
                var repo = applicationContext.getBean(OnboardingCaseRepository.class);
                createTestData(repo, client, "Branch-AMS", "NewAccount");
//                createTestData(repo, client, "Branch-FRA", "NewAccount");
//                createTestData(repo, client, "Branch-MAD", "NewAccount");
            } catch (Throwable e) {
                logger.error("Failed to create Temporal client", e);
            }
        }

        private void createTestData(OnboardingCaseRepository repo, WorkflowClient client, String branchName, String requestType) {
            var workflowId = UUID.randomUUID();
            var entity = repo.save(new OnboardingCase(workflowId, branchName, requestType));
            var workflow = client.newWorkflowStub(CustomerOnboardingWorkflow.class,
                WorkflowOptions.newBuilder()
                    .setWorkflowId(workflowId.toString())
                    .setWorkflowRunTimeout(Duration.ofDays(1))
                    .setTaskQueue("CustomerOnboardingTaskQueue")
                    .build());
            WorkflowClient.start(workflow::execute, Long.toString(entity.getId()));
        }
    }
}
