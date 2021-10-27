package com.filling.service;

import com.filling.domain.FillingEdgeJobs;
import com.filling.repository.FillingEdgeJobsRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FillingEdgeJobs}.
 */
@Service
@Transactional
public class FillingEdgeJobsService {

  private final Logger log = LoggerFactory.getLogger(
    FillingEdgeJobsService.class
  );

  private final FillingEdgeJobsRepository fillingEdgeJobsRepository;

  public FillingEdgeJobsService(
    FillingEdgeJobsRepository fillingEdgeJobsRepository
  ) {
    this.fillingEdgeJobsRepository = fillingEdgeJobsRepository;
  }

  /**
   * Save a fillingEdgeJobs.
   *
   * @param fillingEdgeJobs the entity to save.
   * @return the persisted entity.
   */
  public FillingEdgeJobs save(FillingEdgeJobs fillingEdgeJobs) {
    log.debug("Request to save FillingEdgeJobs : {}", fillingEdgeJobs);
    return fillingEdgeJobsRepository.save(fillingEdgeJobs);
  }

  /**
   * Partially update a fillingEdgeJobs.
   *
   * @param fillingEdgeJobs the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<FillingEdgeJobs> partialUpdate(
    FillingEdgeJobs fillingEdgeJobs
  ) {
    log.debug(
      "Request to partially update FillingEdgeJobs : {}",
      fillingEdgeJobs
    );

    return fillingEdgeJobsRepository
      .findById(fillingEdgeJobs.getId())
      .map(
        existingFillingEdgeJobs -> {
          if (fillingEdgeJobs.getName() != null) {
            existingFillingEdgeJobs.setName(fillingEdgeJobs.getName());
          }
          if (fillingEdgeJobs.getPipelineId() != null) {
            existingFillingEdgeJobs.setPipelineId(
              fillingEdgeJobs.getPipelineId()
            );
          }
          if (fillingEdgeJobs.getTitle() != null) {
            existingFillingEdgeJobs.setTitle(fillingEdgeJobs.getTitle());
          }
          if (fillingEdgeJobs.getUuid() != null) {
            existingFillingEdgeJobs.setUuid(fillingEdgeJobs.getUuid());
          }
          if (fillingEdgeJobs.getValid() != null) {
            existingFillingEdgeJobs.setValid(fillingEdgeJobs.getValid());
          }
          if (fillingEdgeJobs.getMetadata() != null) {
            existingFillingEdgeJobs.setMetadata(fillingEdgeJobs.getMetadata());
          }
          if (fillingEdgeJobs.getCtlVersion() != null) {
            existingFillingEdgeJobs.setCtlVersion(
              fillingEdgeJobs.getCtlVersion()
            );
          }
          if (fillingEdgeJobs.getCtlId() != null) {
            existingFillingEdgeJobs.setCtlId(fillingEdgeJobs.getCtlId());
          }
          if (fillingEdgeJobs.getUiInfo() != null) {
            existingFillingEdgeJobs.setUiInfo(fillingEdgeJobs.getUiInfo());
          }
          if (fillingEdgeJobs.getInfo() != null) {
            existingFillingEdgeJobs.setInfo(fillingEdgeJobs.getInfo());
          }
          if (fillingEdgeJobs.getJobText() != null) {
            existingFillingEdgeJobs.setJobText(fillingEdgeJobs.getJobText());
          }
          if (fillingEdgeJobs.getStatus() != null) {
            existingFillingEdgeJobs.setStatus(fillingEdgeJobs.getStatus());
          }
          if (fillingEdgeJobs.getDescription() != null) {
            existingFillingEdgeJobs.setDescription(
              fillingEdgeJobs.getDescription()
            );
          }
          if (fillingEdgeJobs.getCreated() != null) {
            existingFillingEdgeJobs.setCreated(fillingEdgeJobs.getCreated());
          }
          if (fillingEdgeJobs.getLastModified() != null) {
            existingFillingEdgeJobs.setLastModified(
              fillingEdgeJobs.getLastModified()
            );
          }
          if (fillingEdgeJobs.getCreator() != null) {
            existingFillingEdgeJobs.setCreator(fillingEdgeJobs.getCreator());
          }
          if (fillingEdgeJobs.getLastModifier() != null) {
            existingFillingEdgeJobs.setLastModifier(
              fillingEdgeJobs.getLastModifier()
            );
          }

          return existingFillingEdgeJobs;
        }
      )
      .map(fillingEdgeJobsRepository::save);
  }

  /**
   * Get all the fillingEdgeJobs.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<FillingEdgeJobs> findAll(Pageable pageable) {
    log.debug("Request to get all FillingEdgeJobs");
    return fillingEdgeJobsRepository.findAll(pageable);
  }

  /**
   * Get one fillingEdgeJobs by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<FillingEdgeJobs> findOne(Long id) {
    log.debug("Request to get FillingEdgeJobs : {}", id);
    return fillingEdgeJobsRepository.findById(id);
  }

  /**
   * Delete the fillingEdgeJobs by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete FillingEdgeJobs : {}", id);
    fillingEdgeJobsRepository.deleteById(id);
  }
}
