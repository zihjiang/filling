package com.filling.repository;

import com.filling.domain.FillingEdgeJobs;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FillingEdgeJobs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FillingEdgeJobsRepository
  extends JpaRepository<FillingEdgeJobs, Long> {}
