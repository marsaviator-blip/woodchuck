package org.woodchuck.zChecker.repository;

import org.woodchuck.zChecker.model.Author;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Map;

public interface GraphRepository extends Neo4jRepository<Author, String> {

    // Native query to fetch system summary metrics
    @Query("CALL apoc.meta.stats() YIELD nodeCount, relCount, labels RETURN {nodeCount: nodeCount, relCount: relCount, labels: labels}")
    Map<String, Object> getMetaStats();

    // Autocomplete index search targeting author nodes matching keyword input
    @Query("MATCH (a:Author) WHERE a.name CONTAINS $searchString " +
           "OPTIONAL MATCH (a)-[r]->() " +
           "RETURN a.name as name, elementId(a) as id, count(r) as paperCount " +
           "LIMIT 10")
    List<Map<String, Object>> searchAuthorsAutocomplete(@Param("searchString") String searchString);
}

