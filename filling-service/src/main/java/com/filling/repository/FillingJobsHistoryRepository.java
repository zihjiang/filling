package com.filling.repository;

import com.filling.domain.FillingJobsHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FillingJobsHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FillingJobsHistoryRepository extends JpaRepository<FillingJobsHistory, Long> {}
