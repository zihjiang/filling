package com.filling.repository;

import com.filling.domain.FillingJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FillingJobs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FillingJobsRepository extends JpaRepository<FillingJobs, Long> {}
