package org.woodchuck.zChecker.repository;

import org.woodchuck.zChecker.model.PaperNode;
import org.woodchuck.zChecker.dtos.SearchResultDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ScholarlyRepository extends Neo4jRepository<PaperNode, String> {

    // Matches Papers by title, maps authors and institutions, applies page rules
    @Query("MATCH (p:Paper) WHERE p.title =~ ('(?i).*' + $query + '.*') " +
           "OPTIONAL MATCH (p)<-[:AUTHORED]-(a:Author) " +
           "OPTIONAL MATCH (a)-[:AFFILIATED_WITH]->(i:Institution) " +
           "RETURN p.id AS id, 'paper' AS type, p.title AS title, " +
           "collect(distinct a.name) AS authors, " +
           "collect(distinct i.name) AS institutions, " +
           "[] AS papers " +
           "SKIP $pageable.offset LIMIT $pageable.pageSize")
    List<SearchResultDTO> searchPapers(@Param("query") String query, Pageable pageable);

    // Matches Authors, maps their institutions and papers, applies page rules
    @Query("MATCH (a:Author) WHERE a.name =~ ('(?i).*' + $query + '.*') " +
           "OPTIONAL MATCH (a)-[:AFFILIATED_WITH]->(i:Institution) " +
           "OPTIONAL MATCH (a)-[:AUTHORED]->(p:Paper) " +
           "RETURN a.id AS id, 'author' AS type, a.name AS title, " +
           "[] AS authors, " +
           "collect(distinct i.name) AS institutions, " +
           "collect(distinct p.title) AS papers " +
           "SKIP $pageable.offset LIMIT $pageable.pageSize")
    List<SearchResultDTO> searchAuthors(@Param("query") String query, Pageable pageable);
}

