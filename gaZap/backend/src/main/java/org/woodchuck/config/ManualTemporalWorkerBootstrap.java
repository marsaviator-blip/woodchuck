package org.woodchuck.config;

import jakarta.annotation.PreDestroy;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.woodchuck.temporal.activities.BioActivitiesImpl;
import org.woodchuck.temporal.workflows.BioWorkflowImpl;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

@Component
public class ManualTemporalWorkerBootstrap {
    private static final String BIO_TASK_QUEUE = "BioTaskQueue";

    private final WorkflowClient workflowClient;
    private final BioActivitiesImpl bioActivities;

    private WorkerFactory workerFactory;

    public ManualTemporalWorkerBootstrap(WorkflowClient workflowClient, BioActivitiesImpl bioActivities) {
        this.workflowClient = workflowClient;
        this.bioActivities = bioActivities;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startWorker() {
        if (workerFactory != null) {
            return;
        }

        workerFactory = WorkerFactory.newInstance(workflowClient);
        Worker worker = workerFactory.newWorker(BIO_TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(BioWorkflowImpl.class);
        worker.registerActivitiesImplementations(bioActivities);
        workerFactory.start();
        System.out.println("ManualTemporalWorkerBootstrap: workerFactory started for " + BIO_TASK_QUEUE);
    }

    @PreDestroy
    public void stopWorker() {
        if (workerFactory != null) {
            workerFactory.shutdown();
        }
    }
}
