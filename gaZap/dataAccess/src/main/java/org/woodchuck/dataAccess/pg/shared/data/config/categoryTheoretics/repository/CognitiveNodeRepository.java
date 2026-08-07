package org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.repository;

import org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.entity.CognitiveNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CognitiveNodeRepository extends JpaRepository<CognitiveNode, String> {
    
    // Custom query method used by the CategoricalMorphismEngine to fetch historical matrix profiles
    List<CognitiveNode> findByCategoryAndSessionIdNotIn(String category, List<String> sessionIds);
    
    List<CognitiveNode> findBySessionId(String sessionId);
}
