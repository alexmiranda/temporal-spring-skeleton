package io.github.alexmiranda.samples.temporal_poc.hello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class GreetingWorkflowTests {
    @RegisterExtension
    TestWorkflowExtension testWorkflowExtension = TestWorkflowExtension.newBuilder()
        .setWorkflowTypes(GreetingWorkflowImpl.class)
        .setDoNotStart(true)
        .build();

    @Test
    public void testGreeting(TestWorkflowEnvironment testEnv, Worker worker, GreetingWorkflow workflow) {
        worker.registerActivitiesImplementations(new GreetingActivityImpl());
        testEnv.start();

        var greeting = workflow.greeting("World");
        assertEquals("Hello World!", greeting);
    }

    @Test
    public void testMockedActivity(TestWorkflowEnvironment testEnv, Worker worker, GreetingWorkflow workflow) {
        var activity = mock(GreetingActivity.class, withSettings().withoutAnnotations());
        doReturn("Howdy!").when(activity).greet(anyString());
        worker.registerActivitiesImplementations(activity);
        testEnv.start();

        var greeting = workflow.greeting("World");
        assertEquals("Howdy!", greeting);
    }
}
