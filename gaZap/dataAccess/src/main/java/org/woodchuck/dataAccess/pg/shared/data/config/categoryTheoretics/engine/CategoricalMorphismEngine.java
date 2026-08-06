package org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.engine;

import org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.entity.CognitiveNode;
import org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.entity.CognitiveMorphism;
import org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.repository.CognitiveNodeRepository;
import org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.repository.CognitiveMorphismRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.System.Logger.Level; // Native Java 9+ Logger Levels
import java.util.*;

@Service // Automatically managed across all layers importing this JAR
public class CategoricalMorphismEngine {

    private static final System.Logger log = System.getLogger(CategoricalMorphismEngine.class.getName());
    private final CognitiveNodeRepository nodeRepository;
    private final CognitiveMorphismRepository morphismRepository;

    @Value("${gazap.category.threshold:0.40}")
    private double morphismThreshold;

    public CategoricalMorphismEngine(CognitiveNodeRepository nodeRepository, CognitiveMorphismRepository morphismRepository) {
        this.nodeRepository = nodeRepository;
        this.morphismRepository = morphismRepository;
    }
    
    /**
     * Evaluates a collection of newly staged or updated nodes against the 
     * historical vault category context to discover and write cross-session arrows.
     */
    @Transactional
    public void computeAndForgeCrossSessionMorphisms(List<CognitiveNode> incomingNodes, String targetCategory) {
        log.log(Level.INFO, "Executing Category Matrix Engine over category: {0}", targetCategory);

        // Fetch historical context using repositories enclosed in the same JAR
        List<CognitiveNode> historicalNodes = nodeRepository.findByCategoryAndSessionIdNotIn(
            targetCategory, 
            incomingNodes.stream().map(CognitiveNode::getSessionId).distinct().toList()
        );

        if (historicalNodes.isEmpty()) {
            log.log(Level.INFO, "No historical matrix vector profiles found for context: {}", targetCategory);
            return;
        }

        List<CognitiveMorphism> forgedMorphisms = new ArrayList<>();

        for (CognitiveNode incoming : incomingNodes) {
            for (CognitiveNode history : historicalNodes) {
                
                // Calculate Jaccard similarity index across the localized topic sets
                Set<String> intersection = new HashSet<>(incoming.getTopics());
                intersection.retainAll(history.getTopics());

                if (intersection.isEmpty()) continue;

                Set<String> union = new HashSet<>(incoming.getTopics());
                union.addAll(history.getTopics());

                double topicSimilarity = (double) intersection.size() / union.size();

                // Apply context alignments (subject metadata acceleration)
                double contextBoost = incoming.getSubject().equalsIgnoreCase(history.getSubject()) ? 1.35 : 1.0;
                double categoricalWeight = Math.min(topicSimilarity * contextBoost, 1.0);

                // If the intersection strength crosses the threshold, forge the morphism
                if (categoricalWeight >= morphismThreshold) {
                    CognitiveMorphism morphism = new CognitiveMorphism(
                        UUID.randomUUID().toString(), // id
                        history.getId(),              // sourceNodeId
                        incoming.getId(),             // targetNodeId
                        "CROSS_SESSION_ADJACENCY",    // transitionType
                        categoricalWeight             // algebraicWeight
                    );
                    forgedMorphisms.add(morphism);
                    log.log(Level.INFO, "Forged morphism: {0} -> {1} with weight {2}", history.getId(), incoming.getId(), categoricalWeight);     
                } else {
                    log.log(Level.INFO, "No morphism forged between {0} and {1}; weight {2} below threshold {3}", history.getId(), incoming.getId(), categoricalWeight, morphismThreshold);
                }
            }
        }

        if (!forgedMorphisms.isEmpty()) {
            morphismRepository.saveAll(forgedMorphisms);
            log.log(Level.INFO, "Successfully forged and saved {} new structural category arrows.", forgedMorphisms.size());
        }
    }
}
