package org.woodchuck.components;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.woodchuck.dtos.MaterialStructureParams;
import org.woodchuck.services.MPService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class ClientRunner implements CommandLineRunner {

    private final MPService mpService;

    // Constructor injection
    public ClientRunner(MPService mpService) {
        this.mpService = mpService;
    }

    public void fetchChemicalElement(String element) {
        System.out.println("Fetching chemical element data for: " + element);

        String jsonString = mpService.getChemicalElement(element); // Fetch and print chemical element data
        System.out.println("Tried fetching chemical element data for: " + element);
        if (jsonString != null && !jsonString.isEmpty()) {
            System.out.println("Data fetched successfully:");
            System.out.println(jsonString);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode materialsNode = rootNode.path("data"); // Get the named array
            for (JsonNode node : materialsNode) {
                System.out.println(node);
                String m_id = node.get("material_id").asString();

                // make endpoint calls to fetch more data about the material using the m_id, for example:
                MaterialStructureParams params = new MaterialStructureParams(
                    m_id, "structure, symmetry", false, 1000, 0, 
                    1000, "All");
                String moreData = mpService.getMaterialDetails(params);
                System.out.println("More data for material " + m_id + ": " + moreData);
                System  .out.println("Structure length: " + moreData.length());

                MaterialStructureParams params2 = new MaterialStructureParams(
                    m_id, "structure", false, 1000, 0, 
                    1000, "All");
                String moreData2 = mpService.getMaterialDetails(params2);
//                System.out.println("More data for material " + m_id + ": " + moreData);
                System  .out.println("Structure length: " + moreData2.length());
                JsonNode moreDataNode = objectMapper.readTree(moreData2);
                JsonNode nextNode = moreDataNode.path("data"); // Get the named array
                JsonNode structureNode = nextNode.get(0).path("structure");
                JsonNode latticeNode = structureNode.path("lattice");
                JsonNode sitesNode = latticeNode.path("sites");
                JsonNode jn = nextNode.findValue("sites");
                int cnt = 0;
                for (JsonNode sNode : jn) {
                    System.out.println(sNode.get("label").asString()+
                    "  x:"+sNode.get("xyz").get(0).asText()+
                    "  y:"+sNode.get("xyz").get(1).asText()+
                    "  z:"+sNode.get("xyz").get(2).asText());
                    cnt++;
                }
                // Iterator<Map.Entry<String, JsonNode>> fields=nextNode.getKey();
                // while(fields.hasNext()) {
                //     Map.Entry<String, JsonNode> entry = fields.next();
                //     System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                // }
                System.out.println("Number of nodes in sites " + m_id + ": " + cnt);

                if(moreData.equals(moreData2)) {
                    System.out.println("Data is the same for both requests");
                } else {
                    System.out.println("Data is different for both requests");
                }    

                // String cif = mpService.getCIFfile(m_id);
                // System.out.println("CIF file for material " + m_id + ": " + cif);
            }
//            ObjectMapper objectMapper2= new ObjectMapper();
// try {
//     // Parse to a Map
//     Map<String, String> jsonMap = objectMapper2.readValue(materialsNode, Map.class);
//     System.out.println("ID: " + jsonMap.get("materials_id"));

// } catch (Exception e) {
//     e.printStackTrace();
// }

            // try {
            // // Parse to a M
            // Map<String, Object> jsonMap = objectMapper.readValue(jsonString, Map.class);
            // System.out.println("ID: " + jsonMap.get("material_id"));

            // } catch (Exception e) {
            // e.printStackTrace();
            // }

        } else {
            System.out.println("No data found for element: " + element);
        }
        System.out.println("Finished fetching chemical element data for: " + element);
    }

    // this method can be replaced with REST API calls to fetch data from the
    // Materials Project API using the MPService methods
    @Override
    public void run(String... args) {
        System.out.println("Fetching  using RestClient:");
        String element = "CaHPO4"; // Example component ID, replace with actual ID as needed
        fetchChemicalElement(element); // Fetch and print chemical element data

        // do interesting things with the service here, like fetching data or performing
        // operations

        // give nice names to the methods in MPService and use them here, for example:

    }

    // public static void main(String[] args) {
    // This main method is not needed for Spring Boot applications, as the
    // application
    // will be started by Spring Boot's auto-configuration. However, you can use it
    // for testing purposes if needed.
    // }

}
