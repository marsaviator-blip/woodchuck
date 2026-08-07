package org.woodchuck.category_theorectics2.services;

import org.woodchuck.category_theorectics2.models.InteractionObject;
import org.woodchuck.category_theorectics2.models.InteractionMorphism;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FunctorMappingService {

    private final EmbeddingModel embeddingModel;
    private final JdbcTemplate jdbcTemplate; // For pgvector
    private final Neo4jClient neo4jClient;   // For Category Topology

    public FunctorMappingService(EmbeddingModel embeddingModel, 
                                 JdbcTemplate jdbcTemplate, 
                                 Neo4jClient neo4jClient) {
        this.embeddingModel = embeddingModel;
        this.jdbcTemplate = jdbcTemplate;
        this.neo4jClient = neo4jClient;
    }

    /**
     * Maps an Informal Object to a Formal Academic Object (Object Mapping Component of Functor F)
     */
    @Transactional
    public void executeFunctorObjectMapping(InteractionObject informalObj, String targetScholarlyPaperId) {
        // 1. Vectorize via Spring AI
        float[] vectorArray = embeddingModel.embed(informalObj.content());
        // float[] vectorArray = new float[embedding.size()];
        // for (int i = 0; i < embedding.size(); i++) {
        //     vectorArray[i] = embedding.get(i).floatValue();
        // }

        // 2. Persist Object Vector to pgvector for similarity calculations
        String pgSql = "INSERT INTO category_i_objects (id, type, content, embedding) VALUES (?, ?, ?, ?::vector) " +
                       "ON CONFLICT (id) DO UPDATE SET content = EXCLUDED.content";
        jdbcTemplate.update(pgSql, informalObj.id(), informalObj.type().name(), informalObj.content(), vectorArray);

        // 3. Persist Functorial Object Map F(A) -> B directly inside Neo4j Graph Topology
        neo4jClient.query(
            "MERGE (i:InformalObject {id: $infId}) " +
            "SET i.type = $infType, i.content = $infContent " +
            "MERGE (f:FormalPaper {id: $paperId}) " +
            "MERGE (i)-[r:FUNCTOR_OBJECT_MAP]->(f)"
        )
        .bind(informalObj.id()).to("infId")
        .bind(informalObj.type().name()).to("infType")
        .bind(informalObj.content()).to("infContent")
        .bind(targetScholarlyPaperId).to("paperId")
        .run();
    }

    /**
     * Maps an Informal Morphism Relation to a Formal Structural Morphism (Morphism Mapping Component of Functor F)
     */
    @Transactional
    public void executeFunctorMorphismMapping(InteractionMorphism informalMorphism, String formalMorphismLabel) {
        // Enforces morphism structural preservation F(f: A -> B) = F(f): F(A) -> F(B)
        neo4jClient.query(
            "MATCH (iDomain:InformalObject {id: $infDomainId})-[r1:FUNCTOR_OBJECT_MAP]->(fDomain:FormalPaper) " +
            "MATCH (iCodomain:InformalObject {id: $infCodomainId})-[r2:FUNCTOR_OBJECT_MAP]->(fCodomain:FormalPaper) " +
            "MERGE (iDomain)-[:INFORMAL_MORPHISM {label: $infLabel}]->(iCodomain) " +
            "MERGE (fDomain)-[fMorph:FORMAL_MORPHISM {label: $formalLabel}]->(fCodomain)"
        )
        .bind(informalMorphism.domain().id()).to("infDomainId")
        .bind(informalMorphism.codomain().id()).to("infCodomainId")
        .bind(informalMorphism.label()).to("infLabel")
        .bind(formalMorphismLabel).to("formalLabel")
        .run();
    }
}
