package org.woodchuck.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.woodchuck.entities.DocumentRelations;

public interface DocumentGraphRepository extends Neo4jRepository<DocumentRelations, String> {

}
