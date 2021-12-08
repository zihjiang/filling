package com.filling.service;

import com.alibaba.fastjson.JSONObject;
import com.filling.domain.FillingEdgeJobs;
import com.filling.repository.FillingEdgeJobsRepository;
import com.filling.utils.EdgeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
//          if (fillingEdgeJobs.getPipelineId() != null) {
//            existingFillingEdgeJobs.setPipelineId(
//              fillingEdgeJobs.getPipelineId()
//            );
//          }
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

          if (fillingEdgeJobs.getCreator() != null) {
            existingFillingEdgeJobs.setCreator(fillingEdgeJobs.getCreator());
          }
            existingFillingEdgeJobs.setLastModifier(Instant.now().toString());
            existingFillingEdgeJobs.setLastModified(Instant.now());

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

  public JSONObject saveAndPreview(FillingEdgeJobs fillingEdgeJobs) throws IOException {
      FillingEdgeJobs fillingEdgeJob = save(fillingEdgeJobs);
      EdgeUtils.save(fillingEdgeJob.getFillingEdgeNodes().getBaseHttpUrl(), fillingEdgeJob.getPipelineId(), fillingEdgeJob.getJobString());
      EdgeUtils.update(fillingEdgeJob.getFillingEdgeNodes().getBaseHttpUrl(), fillingEdgeJob.getPipelineId(), fillingEdgeJob.getJobString());

      return EdgeUtils.preview(fillingEdgeJob.getFillingEdgeNodes().getBaseHttpUrl(), fillingEdgeJob.getPipelineId());
  }

    public JSONObject start(Long id) throws IOException {
        JSONObject result = new JSONObject();
        Optional<FillingEdgeJobs> optionalFillingEdgeJobs = findOne(id);
        if(optionalFillingEdgeJobs.isPresent()) {
            FillingEdgeJobs fillingEdgeJob = optionalFillingEdgeJobs.get();
            EdgeUtils.save(fillingEdgeJob.getFillingEdgeNodes().getBaseHttpUrl(), fillingEdgeJob.getPipelineId(), fillingEdgeJob.getJobString());
            EdgeUtils.update(fillingEdgeJob.getFillingEdgeNodes().getBaseHttpUrl(), fillingEdgeJob.getPipelineId(), fillingEdgeJob.getJobString());

            result =  EdgeUtils.start(fillingEdgeJob.getFillingEdgeNodes().getBaseHttpUrl(), fillingEdgeJob.getPipelineId());

            fillingEdgeJob.setStatus(result.getString("status"));
            save(fillingEdgeJob);

        }
        return result;
    }

    public JSONObject stop(Long id) throws IOException {
        JSONObject result = new JSONObject();
        Optional<FillingEdgeJobs> optionalFillingEdgeJobs = findOne(id);
        if(optionalFillingEdgeJobs.isPresent()) {
            FillingEdgeJobs fillingEdgeJob = optionalFillingEdgeJobs.get();

            result =  EdgeUtils.stop(fillingEdgeJob.getFillingEdgeNodes().getBaseHttpUrl(), fillingEdgeJob.getPipelineId());

            fillingEdgeJob.setStatus(result.getString("status"));
            save(fillingEdgeJob);
        }
        return result;
    }

    /**
     * 根据nodeId
     * @param id
     * @return
     */
    public Optional<List<FillingEdgeJobs>> findByFillingEdgeNodesId(Long id) {
        log.debug("Request to findByFillingEdgeNodesId : {}", id);
        return fillingEdgeJobsRepository.findByFillingEdgeNodesId(id);
    }
}
