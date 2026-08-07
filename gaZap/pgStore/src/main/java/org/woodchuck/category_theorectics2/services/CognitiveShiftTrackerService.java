package org.woodchuck.category_theorectics2.services;

import org.woodchuck.category_theorectics2.models.InteractionObject;
import org.woodchuck.category_theorectics2.models.InteractionMorphism;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CognitiveShiftTrackerService {

    private final Neo4jClient neo4jClient;

    public CognitiveShiftTrackerService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    /**
     * Registers a Natural Transformation Component showing an evolution in research perspective.
     * Maps the old functor target F(A) to the new functor target G(A).
     */
    @Transactional
    public void trackCognitiveEvolution(InteractionObject informalObj, 
                                        String oldSessionId, 
                                        String newSessionId,
                                        String explanation) {
        
        String transformationId = "alpha_" + UUID.randomUUID().toString().substring(0, 8);

        neo4jClient.query(
            "MATCH (i:InformalObject {id: $infId})-[f1:FUNCTOR_OBJECT_MAP {sessionId: $oldSession}]->(fOld:FormalPaper) " +
            "MATCH (i)-[f2:FUNCTOR_OBJECT_MAP {sessionId: $newSession}]->(fNew:FormalPaper) " +
            "MERGE (fOld)-[alpha:NATURAL_COMPONENT {id: $transId}]->(fNew) " +
            "SET alpha.informalContext = i.id, " +
            "    alpha.explanation = $explanation, " +
            "    alpha.timestamp = $now"
        )
        .bind(informalObj.id()).to("infId")
        .bind(oldSessionId).to("oldSession")
        .bind(newSessionId).to("newSession")
        .bind(transformationId).to("transId")
        .bind(explanation).to("explanation")
        .bind(Instant.now().toString()).to("now")
        .run();
    }
}
