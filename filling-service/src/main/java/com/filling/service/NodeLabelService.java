package com.filling.service;

import com.filling.domain.NodeLabel;
import com.filling.repository.NodeLabelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link NodeLabel}.
 */
@Service
@Transactional
public class NodeLabelService {

  private final Logger log = LoggerFactory.getLogger(NodeLabelService.class);

  private final NodeLabelRepository nodeLabelRepository;

  public NodeLabelService(NodeLabelRepository nodeLabelRepository) {
    this.nodeLabelRepository = nodeLabelRepository;
  }

  /**
   * Save a nodeLabel.
   *
   * @param nodeLabel the entity to save.
   * @return the persisted entity.
   */
  public NodeLabel save(NodeLabel nodeLabel) {
    log.debug("Request to save NodeLabel : {}", nodeLabel);
    return nodeLabelRepository.save(nodeLabel);
  }

  /**
   * Partially update a nodeLabel.
   *
   * @param nodeLabel the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<NodeLabel> partialUpdate(NodeLabel nodeLabel) {
    log.debug("Request to partially update NodeLabel : {}", nodeLabel);

    return nodeLabelRepository
      .findById(nodeLabel.getId())
      .map(
        existingNodeLabel -> {
          if (nodeLabel.getTitle() != null) {
            existingNodeLabel.setTitle(nodeLabel.getTitle());
          }
          if (nodeLabel.getColor() != null) {
            existingNodeLabel.setColor(nodeLabel.getColor());
          }

          return existingNodeLabel;
        }
      )
      .map(nodeLabelRepository::save);
  }

  /**
   * Get all the nodeLabels.
   *
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public List<NodeLabel> findAll() {
    log.debug("Request to get all NodeLabels");
    return nodeLabelRepository.findAll();
  }

  /**
   * Get one nodeLabel by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<NodeLabel> findOne(Long id) {
    log.debug("Request to get NodeLabel : {}", id);
    return nodeLabelRepository.findById(id);
  }

  /**
   * Delete the nodeLabel by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete NodeLabel : {}", id);
    nodeLabelRepository.deleteById(id);
  }
}
