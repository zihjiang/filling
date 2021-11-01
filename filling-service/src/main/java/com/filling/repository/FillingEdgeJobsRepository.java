package com.filling.repository;

import com.filling.domain.FillingEdgeJobs;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the FillingEdgeJobs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FillingEdgeJobsRepository
  extends JpaRepository<FillingEdgeJobs, Long> {
    @Query("select f from FillingEdgeJobs f where f.fillingEdgeNodes.id = ?1")
    Optional<List<FillingEdgeJobs>> findByFillingEdgeNodesId(Long id);}
