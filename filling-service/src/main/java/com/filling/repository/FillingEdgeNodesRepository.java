package com.filling.repository;

import com.filling.domain.FillingEdgeNodes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the FillingEdgeNodes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FillingEdgeNodesRepository extends JpaRepository<FillingEdgeNodes, Long> {
    @Query("select f from FillingEdgeNodes f where f.uuid = ?1")
    Optional<FillingEdgeNodes> findByUuid(String uuid);
}
