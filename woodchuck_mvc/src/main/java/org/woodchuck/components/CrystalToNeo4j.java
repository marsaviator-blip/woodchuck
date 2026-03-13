package org.woodchuck.components;

import org.neo4j.driver.*;
//import org.neo4j.driver.Values;

import java.util.HashMap;
import java.util.Map;

public class CrystalToNeo4j {

    public static void main(String[] args) {
        // 1. Bolt Protocol Driver Configuration
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", 
            AuthTokens.basic("neo4j", "your_password"));

        try (Session session = driver.session()) {
            // 2. Define Crystal Structure Data (e.g., from CIF parser)
            String crystalName = "Cu2O";
            String spaceGroup = "Pn-3m";
            
            // 3. Create Cypher Query for the Bolt protocol
            String cypher = 
                "MERGE (c:Crystal {name: $name}) " +
                "CREATE (u:UnitCell {spaceGroup: $sg, a: $a, b: $b, c: $c}) " +
                "CREATE (c)-[:HAS_CELL]->(u) " +
                "WITH u " +
                "UNWIND $atoms AS atomData " +
                "CREATE (a:Atom {element: atomData.element, x: atomData.x, y: atomData.y, z: atomData.z}) " +
                "CREATE (u)-[:HAS_ATOM]->(a)";

            // 4. Structure atomic data for parameter mapping
            Map<String, Object> params = new HashMap<>();
            params.put("name", crystalName);
            params.put("sg", spaceGroup);
            params.put("a", 4.26); params.put("b", 4.26); params.put("c", 4.26); // Unit cell vectors
            
            // Atom list example
            params.put("atoms", java.util.List.of(
                Map.of("element", "Cu", "x", 0.25, "y", 0.25, "z", 0.25),
                Map.of("element", "O", "x", 0.0, "y", 0.0, "z", 0.0)
            ));

            // 5. Execute via Bolt Protocol
            session.run(cypher, params);
            System.out.println("Crystal structure loaded successfully.");
        }
        driver.close();
    }
}