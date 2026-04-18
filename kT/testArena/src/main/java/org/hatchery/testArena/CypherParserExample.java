package org.hatchery.testArena;

import org.antlr.v4.runtime.*;
import org.neo4j.cypher.internal.parser.v6.*;

public class CypherParserExample {

    public static void main(String[] args) {
        String query = """
    MATCH (c:Person {name: 'Chuck Norris'})
    CREATE (f:Fact {description: 'Chuck Norris can unit test an entire application with a single print statement.'})
    CREATE (c)-[:KNOWS_FACT]->(f)
    """;
        try {
            CharStream charStream = CharStreams.fromString(query);
            Cypher6Lexer lexer = new Cypher6Lexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Cypher6Parser parser = new Cypher6Parser(tokens);
            parser.statements();
        } catch (Exception e) {
            System.err.println("Failed to parse Cypher query: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
}

// From project base run me like this:
// mvn exec:java -Dexec.mainClass="org.hatchery.testArena.C