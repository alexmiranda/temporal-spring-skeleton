package io.github.alexmiranda.samples.temporal_poc.hello;

public class GreetingActivityImpl implements GreetingActivity {
    @Override
    public String greet(String name) {
        return String.format("Hello %s!", name);
    }
}
