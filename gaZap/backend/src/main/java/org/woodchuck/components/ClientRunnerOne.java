package org.woodchuck.components;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.woodchuck.converter.StructureToBolt;
import org.woodchuck.dtos.MaterialStructureParams;
import org.woodchuck.dtos.SearchQueryParams;
import org.woodchuck.services.CitationService;
import org.woodchuck.services.MPServiceOne;
import org.woodchuck.services.RcsbService;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.temporal.client.WorkflowFailedException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

@Component
@ConditionalOnProperty(name = "app.runner.enabled", havingValue = "true")
public class ClientRunnerOne {//implements CommandLineRunner {

    private final boolean startupTemporalDemoEnabled;

    private final ExecutorService startupExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "startup-rcsb-runner");
        t.setDaemon(false);
        return t;
    });

    private final MPServiceOne mpService;
    private final CitationService citationService;
    private final RcsbService rcsbService;

    // Constructor injection
    public ClientRunnerOne(
            MPServiceOne mpService,
            CitationService citationService,
            RcsbService rcsbService,
            @Value("${app.runner.temporal-demo-enabled:true}") boolean startupTemporalDemoEnabled) {
        this.mpService = mpService;
        this.citationService = citationService;
        this.rcsbService = rcsbService;
        this.startupTemporalDemoEnabled = startupTemporalDemoEnabled;
    }

    public void fetchChemicalElement(String element) {
        System.out.println("Fetching chemical element data for: " + element);

        String jsonString = mpService.getChemicalElement(element); // Fetch and print chemical element data
        System.out.println("Tried fetching chemical element data for: " + element);
        if (jsonString != null && !jsonString.isEmpty()) {
            System.out.println("Data fetched successfully:");
//            System.out.println(jsonString);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode materialsNode = rootNode.path("data"); // Get the named array
            StructureToBolt structureToBolt = new StructureToBolt();
            int index = 0;
            List<String> type = List.of("mono", "tri"); // get real stuff
            
            for (JsonNode node : materialsNode) {
//                System.out.println(node);
                String m_id = node.get("material_id").asString();

                // make endpoint calls to fetch more data about the material using the m_id, for example:
                MaterialStructureParams params = new MaterialStructureParams(
                    m_id, "structure,symmetry,density,chemsys", false, 1000, 0, 
                    1000, "All");
                // String moreData = mpService.getMaterialDetails(params);
                // System.out.println("More data for material " + m_id + ": " + moreData);
                // System  .out.println("Structure length: " + moreData.length());


                MaterialStructureParams provParams = new MaterialStructureParams(
                    m_id, "structure,database_IDs,authors,references", false, 1000, 0, 
                    1000, "All");
                String provData = mpService.getProvenance(provParams);
                System.out.println("Provenance data for material " + m_id + ": " + provData);
                System.out.println("Provenance data length: " + provData.length());

                String[] articles = provData.split("@article");
                System.out.println("Articles for material " + m_id + ":"+articles.length);
                List<String> articleIdList = new java.util.ArrayList<>();
                List<String> astmIdList = new java.util.ArrayList<>();
                for (String article : articles) {
                    String[] parts = article.split(",");
                    System.out.println("Article parts : " + parts.length);
                    if(parts.length > 1) {          
                        System.out.println("Article part: " + parts[0].substring(1, parts[0].length())); 
                        astmIdList.add(parts[0].substring(1, parts[0].length()));
                    }
                    for (String part : parts) {
                         if(part.contains("ASTM")) {
                            String[] subparts = part.split("=");
                            System.out.println("Found ASTM standard: " + subparts[1].substring(3, 9)); 
                            astmIdList.add(subparts[1].substring(3, 9));
                        }
                    }
                }   // need a better way to parse tis data - what a mess

                List<String> uniqueAstmId= astmIdList.stream()
                                            .distinct()
                                            .collect(Collectors.toList()); 
                String first = uniqueAstmId.get(0);

                String someResult = citationService.getCitation(first);
                System.out.println("Citation for ASTM standard " + first + ": " + someResult);

                MaterialStructureParams doiParams = new MaterialStructureParams(
                    m_id, "material_id,doi,bibtex", false, 1000, 0,
                    1000, "All");
                String doiData = mpService.getDOI(doiParams);
                System.out.println("DOI data for material " + m_id + ": " + doiData);
                System.out.println("DOI data length: " + doiData.length());

                //structureToBolt.convert(element, type.get(index), m_id, moreData);

                index++;

                // String cif = mpService.getCIFfile(m_id);
                // System.out.println("CIF file for material " + m_id + ": " + cif);
//        System.exit(0);
            }

        } else {
            System.out.println("No data found for element: " + element);
        }
        System.out.println("Finished fetching chemical element data for: " + element);
    }

    public void fetchRcsbData(String query) {
        System.out.println("Fetching RCSB data for query: " + query);
        SearchQueryParams params = new SearchQueryParams(query, "entry");
        List<String> results = rcsbService.search(params);
        System.out.println("RCSB search results for query '" + query + "': " + results);
        List<String> data = rcsbService.getData(results);
        System.out.println("RCSB data for query '" + query + "': " + data.size() + " entries");
    }

    // this method can be replaced with REST API calls to fetch data from the
    // Materials Project API using the MPService methods
    // @Override
    // public void run(String... args) {
    //     if (!startupTemporalDemoEnabled) {
    //         System.out.println("ClientRunner startup Temporal demo disabled by app.runner.temporal-demo-enabled=false.");
    //         return;
    //     }

    //     System.out.println("ClientRunner scheduling startup Temporal demo call.");
    //     startupExecutor.submit(() -> {
    //         System.out.println("ClientRunner async task started.");
    //         try {
    //             fetchRcsbData("pyrophosphatase");
    //         } catch (WorkflowFailedException ex) {
    //             System.err.println("RCSB startup workflow failed: " + ex.getMessage());
    //         } catch (Exception ex) {
    //             System.err.println("Unexpected startup runner error: " + ex.getMessage());
    //         } finally {
    //             System.out.println("ClientRunner async task completed.");
    //         }
    //     });
    //     startupExecutor.shutdown();
    //     System.out.println("ClientRunner scheduled; application startup can continue.");
    //     // do interesting things with the service here, like fetching data or performing
    //     // operations

    //     // give nice names to the methods in MPService and use them here, for example:

    // }

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}
