package org.woodchuck.components;

import org.springframework.stereotype.Component;
import org.woodchuck.services.WorkflowHandlerService;
import org.woodchuck.temporal.services.ParentWorkflowServiceImpl;
import org.woodchuck.temporal.workflows.specs.BioWorkflowRequest;
import org.woodchuck.temporal.workflows.specs.MPSpec;
import org.springframework.boot.CommandLineRunner;

@Component
public class ClientRunnerParentWorkflow implements CommandLineRunner {

    private final WorkflowHandlerService wfhService;

    //Constructor injection
    public ClientRunnerParentWorkflow(WorkflowHandlerService wfhService) {
        this.wfhService = wfhService;
    }

    public void handleWorkflow() {
    }

    // public void fetchRcsbData(String query) {
    //     System.out.println("Fetching RCSB data for query: " + query);
    //     SearchQueryParams params = new SearchQueryParams(query, "entry");
    //     List<String> results = rcsbService.search(params);
    //     System.out.println("RCSB search results for query '" + query + "': " + results);
    //     List<String> data = rcsbService.getData(results);
    //     System.out.println("RCSB data for query '" + query + "': " + data.size() + " entries");
    // }

    @Override
    public void run(String... args) {

        System.out.println("ClientRunner scheduling startup Temporal workflow.");
    }

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}
