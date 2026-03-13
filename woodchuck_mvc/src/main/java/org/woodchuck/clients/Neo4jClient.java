package org.woodchuck.clients;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class Neo4jClient {

    private static final String URI      = "bolt://localhost:7687";
    private static final String USERNAME = "neo4j";
    private static final String PASSWORD = "your_password";
    private static final Driver driver = GraphDatabase.driver(URI, AuthTokens.basic(USERNAME, PASSWORD));

    public static final void runCypher(String cypherQuery, java.util.Map<String, Object> params) {
        try (Session session = driver.session()) {
            session.run(cypherQuery, params);
        driver.close();
        }

    }
}
