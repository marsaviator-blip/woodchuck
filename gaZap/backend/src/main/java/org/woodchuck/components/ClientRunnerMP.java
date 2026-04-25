package org.woodchuck.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.woodchuck.temporal.workflows.MPSpec;
import org.woodchuck.temporal.services.MPService;

@Component
@Order(1)
@ConditionalOnProperty(name = "app.runner.enabled", havingValue = "true")
public class ClientRunnerMP implements CommandLineRunner {

    //private final MPService mpService;

    public ClientRunnerMP(MPService mpService, MPSpec mpSpec) {
        var execution = mpService.createMPWorkflow(mpSpec);
        var location = UriComponentsBuilder
                        .fromUriString("/mp/{mpExecutionId}")
                        .build(execution);
        System.out.println("Workflow started with execution ID: " + execution);
        System.out.println("Workflow can be accessed at: " + location);               
    }

    // this method can be replaced with REST API calls to fetch data from the
    // Materials Project API using the MPService methods
    @Override
    public void run(String... args) {
        System.out.println("Starting ClientRunnerMP...");
    }

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}
