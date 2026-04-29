package org.woodchuck.components;

import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TemporalShutdownListener {

    private final WorkerFactory workerFactory;
    private final WorkflowServiceStubs serviceStubs;

    public TemporalShutdownListener(WorkerFactory workerFactory, WorkflowServiceStubs serviceStubs) {
        this.workerFactory = workerFactory;
        this.serviceStubs = serviceStubs;
    }

    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        System.out.println("Spring context closing: Shutting down Temporal Workers...");
        workerFactory.shutdown();
        serviceStubs.shutdown();
    }
}
