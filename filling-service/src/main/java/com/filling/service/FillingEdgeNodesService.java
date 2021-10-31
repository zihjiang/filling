package com.filling.service;

import com.filling.domain.FillingEdgeNodes;
import com.filling.repository.FillingEdgeNodesRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FillingEdgeNodes}.
 */
@Service
@Transactional
public class FillingEdgeNodesService {

  private final Logger log = LoggerFactory.getLogger(
    FillingEdgeNodesService.class
  );

  private final FillingEdgeNodesRepository fillingEdgeNodesRepository;

  public FillingEdgeNodesService(
    FillingEdgeNodesRepository fillingEdgeNodesRepository
  ) {
    this.fillingEdgeNodesRepository = fillingEdgeNodesRepository;
  }

  /**
   * Save a fillingEdgeNodes.
   *
   * @param fillingEdgeNodes the entity to save.
   * @return the persisted entity.
   */
  public FillingEdgeNodes save(FillingEdgeNodes fillingEdgeNodes) {
    log.debug("Request to save FillingEdgeNodes : {}", fillingEdgeNodes);
    return fillingEdgeNodesRepository.save(fillingEdgeNodes);
  }

  /**
   * Partially update a fillingEdgeNodes.
   *
   * @param fillingEdgeNodes the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<FillingEdgeNodes> partialUpdate(
    FillingEdgeNodes fillingEdgeNodes
  ) {
    log.debug(
      "Request to partially update FillingEdgeNodes : {}",
      fillingEdgeNodes
    );

    return fillingEdgeNodesRepository
      .findById(fillingEdgeNodes.getId())
      .map(
        existingFillingEdgeNodes -> {
          if (fillingEdgeNodes.getName() != null) {
            existingFillingEdgeNodes.setName(fillingEdgeNodes.getName());
          }
          if (fillingEdgeNodes.getTitle() != null) {
            existingFillingEdgeNodes.setTitle(fillingEdgeNodes.getTitle());
          }
          if (fillingEdgeNodes.getBaseHttpUrl() != null) {
            existingFillingEdgeNodes.setBaseHttpUrl(
              fillingEdgeNodes.getBaseHttpUrl()
            );
          }
          if (fillingEdgeNodes.getGoGoVersion() != null) {
            existingFillingEdgeNodes.setGoGoVersion(
              fillingEdgeNodes.getGoGoVersion()
            );
          }
          if (fillingEdgeNodes.getGoGoOS() != null) {
            existingFillingEdgeNodes.setGoGoOS(fillingEdgeNodes.getGoGoOS());
          }
          if (fillingEdgeNodes.getGoGoArch() != null) {
            existingFillingEdgeNodes.setGoGoArch(
              fillingEdgeNodes.getGoGoArch()
            );
          }
          if (fillingEdgeNodes.getGoBuildDate() != null) {
            existingFillingEdgeNodes.setGoBuildDate(
              fillingEdgeNodes.getGoBuildDate()
            );
          }
          if (fillingEdgeNodes.getGoRepoSha() != null) {
            existingFillingEdgeNodes.setGoRepoSha(
              fillingEdgeNodes.getGoRepoSha()
            );
          }
          if (fillingEdgeNodes.getDescription() != null) {
            existingFillingEdgeNodes.setDescription(
              fillingEdgeNodes.getDescription()
            );
          }
          if (fillingEdgeNodes.getCreated() != null) {
            existingFillingEdgeNodes.setCreated(fillingEdgeNodes.getCreated());
          }
          if (fillingEdgeNodes.getLastModified() != null) {
            existingFillingEdgeNodes.setLastModified(
              fillingEdgeNodes.getLastModified()
            );
          }
          if (fillingEdgeNodes.getCreator() != null) {
            existingFillingEdgeNodes.setCreator(fillingEdgeNodes.getCreator());
          }
          if (fillingEdgeNodes.getLastModifier() != null) {
            existingFillingEdgeNodes.setLastModifier(
              fillingEdgeNodes.getLastModifier()
            );
          }
          if (fillingEdgeNodes.getUuid() != null) {
            existingFillingEdgeNodes.setUuid(fillingEdgeNodes.getUuid());
          }

          return existingFillingEdgeNodes;
        }
      )
      .map(fillingEdgeNodesRepository::save);
  }

  /**
   * Get all the fillingEdgeNodes.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<FillingEdgeNodes> findAll(Pageable pageable) {
    log.debug("Request to get all FillingEdgeNodes");
    return fillingEdgeNodesRepository.findAll(pageable);
  }

  /**
   * Get one fillingEdgeNodes by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<FillingEdgeNodes> findOne(Long id) {
    log.debug("Request to get FillingEdgeNodes : {}", id);
    return fillingEdgeNodesRepository.findById(id);
  }

  /**
   * Delete the fillingEdgeNodes by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete FillingEdgeNodes : {}", id);
    fillingEdgeNodesRepository.deleteById(id);
  }

    /**
     * find fillingEdgeNodes by uuid
     * @param uuid
     */
  public Optional<FillingEdgeNodes> findByUuid(String uuid) {
      log.debug("find by uuid FillingEdgeNodes : {}", uuid);
      return fillingEdgeNodesRepository.findByUuid(uuid);
  }

    /**
     * save by uuid, if exist update, else insert
     * @param fillingEdgeNodes
     */
  public void saveByUuid(FillingEdgeNodes fillingEdgeNodes) {
      log.debug("save uuid FillingEdgeNodes : {}", fillingEdgeNodes.getUuid());
      Optional<FillingEdgeNodes> optionalFillingEdgeNodes = findByUuid(fillingEdgeNodes.getUuid());
      if(optionalFillingEdgeNodes.isPresent()) {
          log.debug("exist, update FillingEdgeNodes");
          fillingEdgeNodes.setId(optionalFillingEdgeNodes.get().getId());
      }
      save(fillingEdgeNodes);
  }
}
