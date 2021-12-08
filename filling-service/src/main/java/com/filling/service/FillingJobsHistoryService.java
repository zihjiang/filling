package com.filling.service;

import com.filling.domain.FillingJobsHistory;
import com.filling.repository.FillingJobsHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link FillingJobsHistory}.
 */
@Service
@Transactional
public class FillingJobsHistoryService {

    private final Logger log = LoggerFactory.getLogger(FillingJobsHistoryService.class);

    private final FillingJobsHistoryRepository fillingJobsHistoryRepository;

    public FillingJobsHistoryService(FillingJobsHistoryRepository fillingJobsHistoryRepository) {
        this.fillingJobsHistoryRepository = fillingJobsHistoryRepository;
    }

    /**
     * Save a fillingJobsHistory.
     *
     * @param fillingJobsHistory the entity to save.
     * @return the persisted entity.
     */
    public FillingJobsHistory save(FillingJobsHistory fillingJobsHistory) {
        log.debug("Request to save FillingJobsHistory : {}", fillingJobsHistory);
        return fillingJobsHistoryRepository.save(fillingJobsHistory);
    }

    /**
     * Partially update a fillingJobsHistory.
     *
     * @param fillingJobsHistory the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FillingJobsHistory> partialUpdate(FillingJobsHistory fillingJobsHistory) {
        log.debug("Request to partially update FillingJobsHistory : {}", fillingJobsHistory);

        return fillingJobsHistoryRepository
            .findById(fillingJobsHistory.getId())
            .map(
                existingFillingJobsHistory -> {
                    if (fillingJobsHistory.getName() != null) {
                        existingFillingJobsHistory.setName(fillingJobsHistory.getName());
                    }
                    if (fillingJobsHistory.getApplicationId() != null) {
                        existingFillingJobsHistory.setApplicationId(fillingJobsHistory.getApplicationId());
                    }
                    if (fillingJobsHistory.getJobText() != null) {
                        existingFillingJobsHistory.setJobText(fillingJobsHistory.getJobText());
                    }
                    if (fillingJobsHistory.getType() != null) {
                        existingFillingJobsHistory.setType(fillingJobsHistory.getType());
                    }
                    if (fillingJobsHistory.getConfProp() != null) {
                        existingFillingJobsHistory.setConfProp(fillingJobsHistory.getConfProp());
                    }
                    if (fillingJobsHistory.getStatus() != null) {
                        existingFillingJobsHistory.setStatus(fillingJobsHistory.getStatus());
                    }
                    if (fillingJobsHistory.getCreatetime() != null) {
                        existingFillingJobsHistory.setCreatetime(fillingJobsHistory.getCreatetime());
                    }
                    if (fillingJobsHistory.getUpdatetime() != null) {
                        existingFillingJobsHistory.setUpdatetime(fillingJobsHistory.getUpdatetime());
                    }
                    if (fillingJobsHistory.getCreatedBy() != null) {
                        existingFillingJobsHistory.setCreatedBy(fillingJobsHistory.getCreatedBy());
                    }
                    if (fillingJobsHistory.getAddjar() != null) {
                        existingFillingJobsHistory.setAddjar(fillingJobsHistory.getAddjar());
                    }
                    if (fillingJobsHistory.getDescription() != null) {
                        existingFillingJobsHistory.setDescription(fillingJobsHistory.getDescription());
                    }

                    return existingFillingJobsHistory;
                }
            )
            .map(fillingJobsHistoryRepository::save);
    }

    /**
     * Get all the fillingJobsHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FillingJobsHistory> findAll(Pageable pageable) {
        log.debug("Request to get all FillingJobsHistories");
        return fillingJobsHistoryRepository.findAll(pageable);
    }

    /**
     * Get one fillingJobsHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FillingJobsHistory> findOne(Long id) {
        log.debug("Request to get FillingJobsHistory : {}", id);
        return fillingJobsHistoryRepository.findById(id);
    }

    /**
     * Delete the fillingJobsHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FillingJobsHistory : {}", id);
        fillingJobsHistoryRepository.deleteById(id);
    }
}
