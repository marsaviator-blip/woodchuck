package org.lim.clients;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class Neo4jClient {

    public static final void runCypher(String cypherQuery, java.util.Map<String, Object> params) {

        final String URI = "bolt://localhost:7687";
        final String USERNAME = "neo4j";
        final String PASSWORD = "your_password";
        final Driver driver = GraphDatabase.driver(URI, AuthTokens.basic(USERNAME, PASSWORD));

        try (Session session = driver.session()) {
            session.run(cypherQuery, params);
        }  
        driver.close();
    }
}
