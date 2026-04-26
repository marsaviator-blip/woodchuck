package org.woodchuck.components;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.woodchuck.converter.StructureToBolt;
import org.woodchuck.dtos.MaterialStructureParams;
import org.woodchuck.services.CitationService;
import org.woodchuck.services.MPServiceOne;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
// @Order(2)
// @ConditionalOnProperty(name = "app.runner.temporal-citation-enabled", havingValue = "true")
public class ClientRunnerOne {//implements CommandLineRunner {

    private final MPServiceOne mpService;
    private final CitationService citationService;

    // Constructor injection
    public ClientRunnerOne(MPServiceOne mpService, CitationService citationService) {
        this.mpService = mpService;
        this.citationService = citationService;
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
            int index = 0;
            List<String> type = List.of("mono", "tri"); // get real stuff
            
            for (JsonNode node : materialsNode) {
//                System.out.println(node);
                String m_id = node.get("material_id").asString();

                // make endpoint calls to fetch more data about the material using the m_id, for example:
                MaterialStructureParams strucParams = new MaterialStructureParams(
                    m_id, "structure,symmetry,density,chemsys", false, 1000, 0, 
                    1000, "All");
                // String moreData = mpService.getMaterialDetails(strucParams);
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

    // this method can be replaced with REST API calls to fetch data from the
    // Materials Project API using the MPService methods
    // @Override
    // public void run(String... args) {

    //     System.out.println("ClientRunnerOne scheduling startup Temporal demo call.");
    //     System.out.println("ClientRunnerOne scheduled; application startup can continue.");
//        fetchChemicalElement("CaHPO4"); // example element, can be replaced with any other element or parameter
        // do interesting things with the service here, like fetching data or performing
        // operations

        // give nice names to the methods in MPService and use them here, for example:

    //}

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}
