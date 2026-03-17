package org.lim.services;

//import org.lim.components.GraphConfig;
import org.lim.converter.YamlToBolt;
import org.springframework.stereotype.Service;


@Service
public class GraphService {

    // private final GraphConfig graphConfig;

    // public GraphService(GraphConfig graphConfig) {
    //     this.graphConfig = graphConfig;
    // }

    public void createGraph() {
        // String self = graphConfig.person().name();

        // graphConfig.interests().forEach(interest -> {
        //     System.out.println("Creating relationship for person " + self + " with interest " + interest.title());
        //     // Here you would add the logic to create nodes and relationships in Neo4j
        // }); 
        // graphConfig.relationships().forEach(rel -> {
        //     System.out.println("Creating relationship for person " + rel.person() + " with interest " + 
        //     rel.interests().get(0));
        //     // Here you would add the logic to create nodes and relationships in Neo4j
        // });
        YamlToBolt converter = new YamlToBolt();
        converter.convert();
        // converter.convert(graphConfig.person(), graphConfig.interests(), graphConfig.());
    }

}