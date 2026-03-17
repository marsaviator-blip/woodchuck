package org.lim.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lim.clients.Neo4jClient;
// import org.lim.components.GraphConfig.Interest;
// import org.lim.components.GraphConfig.Person;
// import org.lim.components.GraphConfig.Relationship;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class YamlToBolt {

    private String cypher;
    private Map<String, Object> params;

    // public void convert(Person self, List<Interest> interests , 
    //                 List<Relationship> relationships) {
    public void convert() {
        List<String> interests = List.of("Logging", 
        "Home Construction", "Aircraft Construction", "Aircraft Flying", "Pond Construction",
         "Tree Planting", "Software Engineering");
         List<String> relationships = List.of("rel1", "rel2", "rel3", "rel4", "rel5", "rel6", "rel7");

    //     ObjectMapper objectMapper = new ObjectMapper();
    //     JsonNode moreDataNode = objectMapper.readTree(jsonStringOfStructure);
    //     JsonNode dataNodes = moreDataNode.path("data"); // Get the named array
    //     JsonNode latticeNode = dataNodes.findValue("lattice");
    //     JsonNode aNode = latticeNode.path("a");
    //     JsonNode bNode = latticeNode.path("b");
    //     JsonNode cNode = latticeNode.path("c");
    //     JsonNode siteNodes = dataNodes.findValue("sites");

    //     String crystalName = mp_id;
    //     String spaceGroup = "TBD";
    //     String sg = "TBD";
        System.out.println(">>>>>>>  "+"junk"+"  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        String name = "Ray Martin";//self.name();
        System.out.println("Interests:");
//        String interest = "Logging";
    //     // 3. Create Cypher Query for the Bolt protocol
    int cnt = 1;
    String iStr = "";
        for (String interest : interests) { 
            iStr += "MERGE (i"+cnt+":interest {interest: \""+interest+"\"}) ";
            cnt++;
        };
        cnt = 0;
        String rStr = "";
        for (String rel : relationships) {
            rStr += "MERGE (s)-[:HAS_INTEREST]->(i"+cnt+") ";
            cnt++;
        };

        cypher = "MERGE (s:self {name: $name}) "+ iStr+""+rStr;// +""+rStr.substring(0, rStr.length()-9);
    //             "MERGE (i:interest {interest: $interest}) ";// +
    //             "CREATE (t:type {name: $type}) " +
    //             "CREATE (c:Crystal {name: $name}) " +
    //             "CREATE (u:UnitCell {spaceGroup: $sg, a: $a, b: $b, c: $c}) " +
    //             "CREATE (tn)-[:HAS_TYPE]->(t) " +
    //             "WITH t " +
    //             "CREATE (t)-[:HAS_HUH]->(c) " +
    //             "WITH c " +
    //             "CREATE (c)-[:HAS_CELL]->(u) " +
    //             "WITH u " +
    //             "UNWIND $atoms AS atomData " +
    //             "CREATE (a:Atom {element: atomData.element, x: atomData.x, y: atomData.y, z: atomData.z}) " +
    //             "CREATE (u)-[:HAS_ATOM]->(a)";

    //     int cnt = 0;
    //     // 4. Structure atomic data for parameter mapping
        params = new HashMap<>();
        params.put("name", name);
        interests.forEach(interest -> {
            params.put("interest", interest);
        });
        relationships.forEach(rel -> {
            params.put("relationship", rel);
        });
     //     params.put("type", type);
    //     params.put("name", crystalName); // mp_id for now
    //     params.put("sg", spaceGroup);
    //     params.put("a", aNode);
    //     params.put("b", bNode);
    //     params.put("c", cNode);

    //     List<Map<String, Object>> atoms = new java.util.ArrayList<>();

    //     for (JsonNode sNode : siteNodes) {
    //         // System.out.println(sNode.get("label").asString() +
    //         //         "  x:" + sNode.get("xyz").get(0).asText() +
    //         //         "  y:" + sNode.get("xyz").get(1).asText() +
    //         //         "  z:" + sNode.get("xyz").get(2).asText());
    //         String elementToNeo = sNode.get("label").asString();
    //         Double x = sNode.get("xyz").get(0).asDouble();
    //         Double y = sNode.get("xyz").get(1).asDouble();
    //         Double z = sNode.get("xyz").get(2).asDouble();
    //         Double a = sNode.get("abc").get(0).asDouble();
    //         Double b = sNode.get("abc").get(1).asDouble();
    //         Double c = sNode.get("abc").get(2).asDouble();
    //         // params.put("atoms", java.util.List.of(
    //         atoms.add(
    //                 Map.of("element", elementToNeo, "x", x, "y", y, "z", z)
    //         // Map.of("element", elementToNeo, "a", a, "b", b, "c", c)
    //         );
    //         // ));
    //         cnt++;
    //     }
    //     params.put("atoms", atoms);

         pushToNeo4j();
    }

    public void pushToNeo4j() {
        Neo4jClient.runCypher(cypher, params);
    }
}    