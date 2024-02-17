package io.github.alexmiranda.samples.temporal_poc.hello;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface GreetingActivity {
    String greet(String name);
}
