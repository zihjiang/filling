package com.filling.repository;

import com.filling.domain.FillingEdgeNodes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FillingEdgeNodes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FillingEdgeNodesRepository
  extends JpaRepository<FillingEdgeNodes, Long> {}
