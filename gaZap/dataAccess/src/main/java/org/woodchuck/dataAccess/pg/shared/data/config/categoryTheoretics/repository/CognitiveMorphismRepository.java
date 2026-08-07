package org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.repository;

import org.woodchuck.dataAccess.pg.shared.data.config.categoryTheoretics.entity.CognitiveMorphism;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CognitiveMorphismRepository extends JpaRepository<CognitiveMorphism, String> {
}
