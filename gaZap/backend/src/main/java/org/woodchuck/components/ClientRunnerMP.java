package org.woodchuck.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.woodchuck.temporal.workflows.BioWorkflowImpl;
import org.woodchuck.temporal.workflows.MPSpec;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import org.woodchuck.temporal.activities.BioActivitiesImpl;
import org.woodchuck.temporal.activities.MPActivitiesImpl;
import org.woodchuck.temporal.services.MPService;
import org.woodchuck.temporal.services.MPWorkflowImpl;

@Component
@Order(1)
@ConditionalOnProperty(name = "app.runner.enabled", havingValue = "true")
public class ClientRunnerMP implements CommandLineRunner {
    private static final String MP_QUEUE = "MP_QUEUE";

    private final WorkflowClient workflowClient;
    private final MPActivitiesImpl mpActivities;

    public ClientRunnerMP(MPService mpService, MPSpec mpSpec, 
                        WorkflowClient workflowClient, MPActivitiesImpl mpActivities) {
        this.workflowClient = workflowClient;
        this.mpActivities = mpActivities;
        var execution = mpService.createMPWorkflow(mpSpec);
        // var location = UriComponentsBuilder
        //                 .fromUriString("/mp/{mpExecutionId}")
        //                 .build(execution);
        System.out.println("Workflow started with execution ID: " + execution);
//        System.out.println("Workflow can be accessed at: " + location);               
        WorkerFactory workerFactory = WorkerFactory.newInstance(workflowClient);
        Worker worker = workerFactory.newWorker(MP_QUEUE);
        worker.registerWorkflowImplementationTypes(MPWorkflowImpl.class);
        worker.registerActivitiesImplementations(mpActivities);
        workerFactory.start(); 
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
