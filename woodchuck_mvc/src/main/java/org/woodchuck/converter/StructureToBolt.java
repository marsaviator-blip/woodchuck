package org.woodchuck.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.woodchuck.clients.Neo4jClient;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class StructureToBolt {

    private String cypher;
    private Map<String, Object> params;

    public void convert(String topName, String type, String element, 
        String mp_id, String jsonStringOfStructure) {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode moreDataNode = objectMapper.readTree(jsonStringOfStructure);
        JsonNode dataNodes    = moreDataNode.path("data"); // Get the named array        
        JsonNode latticeNode  = dataNodes.findValue("lattice");
        JsonNode aNode        = latticeNode.path("a");
        JsonNode bNode        = latticeNode.path("b");
        JsonNode cNode        = latticeNode.path("c");
        JsonNode siteNodes    = dataNodes.findValue("sites");

        String crystalName = element;
        String spaceGroup = "TBD";
        String sg = "TBD";  

        // 3. Create Cypher Query for the Bolt protocol
        cypher = "MERGE (tn:top {name: $topName}) " +
                "CREATE (t:type {name: $type}) " +
                "CREATE (c:Crystal {name: $name}) " +
                "CREATE (u:UnitCell {spaceGroup: $sg, a: $a, b: $b, c: $c}) " +
                "CREATE (tn)-[:HAS_TYPE]->(t) " +
                "WITH t " +
                "CREATE (t)-[:HAS_HUH]->(c) " +
                "WITH c " +
                "CREATE (c)-[:HAS_CELL]->(u) " +
                "WITH u " +
                "UNWIND $atoms AS atomData " +
                "CREATE (a:Atom {element: atomData.element, x: atomData.x, y: atomData.y, z: atomData.z}) " +
                "CREATE (u)-[:HAS_ATOM]->(a)";

        int cnt = 0;
        // 4. Structure atomic data for parameter mapping
        params = new HashMap<>();
        params.put("topName", topName);
        params.put("type", type);
        params.put("name", crystalName);
        params.put("sg", spaceGroup);
        params.put("a", aNode);
        params.put("b", bNode);
        params.put("c", cNode); 

        List<Map<String, Object>> atoms = new java.util.ArrayList<>();

        for (JsonNode sNode : siteNodes) {
            System.out.println(sNode.get("label").asString() +
                    "  x:" + sNode.get("xyz").get(0).asText() +
                    "  y:" + sNode.get("xyz").get(1).asText() +
                    "  z:" + sNode.get("xyz").get(2).asText());
            String elementToNeo = sNode.get("label").asString();
            Double x = sNode.get("xyz").get(0).asDouble();
            Double y = sNode.get("xyz").get(1).asDouble();
            Double z = sNode.get("xyz").get(2).asDouble();
            Double a = sNode.get("abc").get(0).asDouble();
            Double b = sNode.get("abc").get(1).asDouble();
            Double c = sNode.get("abc").get(2).asDouble();
//            params.put("atoms", java.util.List.of(
atoms.add(
                    Map.of("element", elementToNeo, "x", x, "y", y, "z", z)
  //                  Map.of("element", elementToNeo, "a", a, "b", b, "c", c)
);
//                ));
            cnt++;
        }
        params.put("atoms", atoms); 
        
        pushToNeo4j();
    }

    public void pushToNeo4j() {
        Neo4jClient.runCypher(cypher, params);
    }

    // public void closeDriver() {
    //     Neo4jClient.closeDriver();
    // }   
}