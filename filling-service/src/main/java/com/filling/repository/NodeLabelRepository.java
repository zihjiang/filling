package com.filling.repository;

import com.filling.domain.NodeLabel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the NodeLabel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NodeLabelRepository extends JpaRepository<NodeLabel, Long> {}
