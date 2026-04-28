package org.woodchuck.temporal.services;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.woodchuck.converter.StructureToBolt;
import org.woodchuck.dtos.MaterialStructureParams;
import org.woodchuck.temporal.workflows.ActivityExecutionSettings;
import org.woodchuck.temporal.workflows.MPWorkflow;
import org.woodchuck.temporal.workflows.specs.MPSpec;
import org.woodchuck.temporal.activities.MPActivities;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@WorkflowImpl(taskQueues = "MP_QUEUE")
public class MPWorkflowImpl implements MPWorkflow {

    private boolean isStarted = false;  
    private boolean notProcessed = true;
    private boolean processed = false;
    private boolean completed = false;
    private String elementId;// = "CaHPO4";
    private MPActivities activities;

    public void resetFlags() {
        Workflow.await(() -> processed); 
        this.isStarted = false;
        this.notProcessed = true;
        this.processed = false;
        this.completed = false;
    }

    public void complete() {
        Workflow.await(() -> processed);
        this.isStarted = true;
        this.completed = true;
    }

    public void startUp(MPSpec spec) {
        Workflow.await(() -> notProcessed);
        activities = newActivities(spec.getSettings());
       
        isStarted = true;  
        Workflow.await(() -> processed); 
        System.out.println("MPWorkflowImpl started up.");
    }   

    public void processMP(MPSpec spec) {
        //MPActivities activities = WorkflowImpl.getActivityStub(MPActivities.class);
        while (!completed) {
         Workflow.await(() -> isStarted);
         System.out.println("isStarted: " + isStarted+", notProcessed: " + notProcessed + ", processed: " + processed + ", completed: " + completed);
         if(completed) {
            break;
         }
//        activities = newActivities(spec.getSettings());
        System.out.println("Run first activity");
        //Workflow.await(() -> Workflow.isEveryHandlerFinished());
        elementId = spec.getElementId();    
        String jsonString = activities.getChemicalElement(elementId); // Fetch and print chemical element data
        System.out.println("Tried fetching chemical element data for: " + elementId);
        if (jsonString != null && !jsonString.isEmpty()) {
            System.out.println("Data fetched successfully:");
//            System.out.println(jsonString);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode materialsNode = rootNode.path("data"); // Get the named array
            if(spec.isShouldCallNeo4j()) {  
                StructureToBolt structureToBolt = new StructureToBolt();
            }
            int index = 0;
            List<String> type = List.of("mono", "tri"); // get real stuff
            for (JsonNode node : materialsNode) {
//                System.out.println(node);
                String m_id = node.get("material_id").asString();

                // make endpoint calls to fetch more data about the material using the m_id, for example:
                MaterialStructureParams strucParams = new MaterialStructureParams(
                    m_id, "structure,symmetry,density,chemsys", false, 1000, 0, 
                    1000, "All");
                 String moreData = activities.getMaterialDetails(strucParams);
                 System.out.println("More data for material " + m_id + ": " + moreData);
                 System  .out.println("Structure length: " + moreData.length());


                MaterialStructureParams provParams = new MaterialStructureParams(
                    m_id, "structure,database_IDs,authors,references", false, 1000, 0, 
                    1000, "All");
                String provData = activities.getProvenance(provParams);
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

                // String someResult = citationService.getCitation(first);
                // System.out.println("Citation for ASTM standard " + first + ": " + someResult);

                if(spec.isShouldGetDOI()) {
                    MaterialStructureParams doiParams = new MaterialStructureParams(
                        m_id, "material_id,doi,bibtex", false, 1000, 0,
                        1000, "All");
                    String doiData = activities.getDOI(doiParams);
                    System.out.println("DOI data for material " + m_id + ": " + doiData);
                    System.out.println("DOI data length: " + doiData.length());
                }

                //structureToBolt.convert(element, type.get(index), m_id, moreData);

                index++;

                // String cif = mpService.getCIFfile(m_id);
                // System.out.println("CIF file for material " + m_id + ": " + cif);
            }
            //Workflow.await(() -> Workflow.isEveryHandlerFinished());
            notProcessed = false;
            processed = true;
        } else {
            System.out.println("No data found for element: " + elementId);
        }
        System.out.println("Finished fetching chemical element data for: " + elementId);
    }
    }

    private MPActivities newActivities(ActivityExecutionSettings settings) {
        return Workflow.newActivityStub(
            MPActivities.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(settings.getTimeoutSeconds()))
                .setRetryOptions(
                    RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(settings.getInitialIntervalSeconds()))
                        .setBackoffCoefficient(settings.getBackoffCoefficient())
                        .setMaximumInterval(Duration.ofSeconds(settings.getMaximumIntervalSeconds()))
                        .setMaximumAttempts(settings.getMaximumAttempts())
                        .build())
                .build());
    }

    // @Override
    // public void reserveOrderItems(Order order) {
    //     for (OrderItem item : order.items()) {
    //         inventoryService.reserveInventory(item.sku(), item.quantity());
    //     }
    // }
       // ... order initialization omitted

////        activities.reserveOrderItems(spec.order());

        // Create a payment request
//        Async.function(() -> payment = activities.createPaymentRequest(spec.order(), spec.billingInfo()));

        // Create a shipping request
//        shipping = activities.createShipping(spec.order());
    
       // ... workflow logic omitted


    // ... other methods omitted
}