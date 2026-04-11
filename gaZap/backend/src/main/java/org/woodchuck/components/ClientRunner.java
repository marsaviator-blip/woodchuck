package org.woodchuck.components;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.woodchuck.converter.StructureToBolt;
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
                String moreData = mpService.getMaterialDetails(params);
                System.out.println("More data for material " + m_id + ": " + moreData);
                System  .out.println("Structure length: " + moreData.length());


                MaterialStructureParams provParams = new MaterialStructureParams(
                    m_id, "structure,database_IDs,authors,references", false, 1000, 0, 
                    1000, "All");
                String provData = mpService.getProvenance(provParams);
                System.out.println("Provenance data for material " + m_id + ": " + provData);
                System.out.println("Provenance data length: " + provData.length());

                MaterialStructureParams doiParams = new MaterialStructureParams(
                    m_id, "doi,bibtex", false, 1000, 0,
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
