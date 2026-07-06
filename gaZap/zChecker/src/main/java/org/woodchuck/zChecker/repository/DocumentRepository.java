package org.woodchuck.zChecker.repository;  

import org.woodchuck.zChecker.dtos.DocumentsInNeo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends Neo4jRepository<DocumentsInNeo4j, String> {
    // Built-in .findAll() will automatically retrieve all documents and mapped metadata
}
