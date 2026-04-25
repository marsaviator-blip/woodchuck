package org.woodchuck.components;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.woodchuck.dtos.SearchQueryParams;
import org.woodchuck.services.RcsbService;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.temporal.client.WorkflowFailedException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

@Component
@Order(3)
@ConditionalOnProperty(name = "app.runner.temporal-rcsb-enabled", havingValue = "true")
public class ClientRunnerRcsb implements CommandLineRunner {

    // private final ExecutorService startupExecutor = Executors.newSingleThreadExecutor(r -> {
    //     Thread t = new Thread(r, "startup-rcsb-runner");
    //     t.setDaemon(false);
    //     return t;
    // });

    private final RcsbService rcsbService;

    // Constructor injection
    public ClientRunnerRcsb(RcsbService rcsbService) {
        this.rcsbService = rcsbService;
        this.rcsbService.startWorkflow();
    }

    public void fetchRcsbData(String query) {
        System.out.println("Fetching RCSB data for query: " + query);
        SearchQueryParams params = new SearchQueryParams(query, "entry");
        List<String> results = rcsbService.search(params);
        System.out.println("RCSB search results for query '" + query + "': " + results);
        List<String> data = rcsbService.getData(results);
        System.out.println("RCSB data for query '" + query + "': " + data.size() + " entries");
    }

    @Override
    public void run(String... args) {

        System.out.println("ClientRunner scheduling startup Temporal demo call.");
    }

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}
