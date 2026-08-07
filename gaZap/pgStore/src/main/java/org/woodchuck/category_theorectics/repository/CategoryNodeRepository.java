package org.woodchuck.category_theorectics.repository;

import org.woodchuck.category_theorectics.model.CategoryObjectNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface CategoryNodeRepository extends Neo4jRepository<CategoryObjectNode, String> {
    
    @Query("MATCH (a:CategoryObject {id: $sourceId}), (b:CategoryObject {id: $targetId}) " +
           "MERGE (a)-[r:MAPPED_BY_FUNCTOR {strength: $strength}]->(b)")
    void createFunctorMorphism(String sourceId, String targetId, String strength);
}
