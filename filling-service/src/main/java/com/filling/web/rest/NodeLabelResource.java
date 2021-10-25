package com.filling.web.rest;

import com.filling.domain.NodeLabel;
import com.filling.repository.NodeLabelRepository;
import com.filling.service.NodeLabelService;
import com.filling.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.filling.domain.NodeLabel}.
 */
@RestController
@RequestMapping("/api")
public class NodeLabelResource {

  private final Logger log = LoggerFactory.getLogger(NodeLabelResource.class);

  private static final String ENTITY_NAME = "nodeLabel";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final NodeLabelService nodeLabelService;

  private final NodeLabelRepository nodeLabelRepository;

  public NodeLabelResource(
    NodeLabelService nodeLabelService,
    NodeLabelRepository nodeLabelRepository
  ) {
    this.nodeLabelService = nodeLabelService;
    this.nodeLabelRepository = nodeLabelRepository;
  }

  /**
   * {@code POST  /node-labels} : Create a new nodeLabel.
   *
   * @param nodeLabel the nodeLabel to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new nodeLabel, or with status {@code 400 (Bad Request)} if the nodeLabel has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/node-labels")
  public ResponseEntity<NodeLabel> createNodeLabel(
    @RequestBody NodeLabel nodeLabel
  ) throws URISyntaxException {
    log.debug("REST request to save NodeLabel : {}", nodeLabel);
    if (nodeLabel.getId() != null) {
      throw new BadRequestAlertException(
        "A new nodeLabel cannot already have an ID",
        ENTITY_NAME,
        "idexists"
      );
    }
    NodeLabel result = nodeLabelService.save(nodeLabel);
    return ResponseEntity
      .created(new URI("/api/node-labels/" + result.getId()))
      .headers(
        HeaderUtil.createEntityCreationAlert(
          applicationName,
          false,
          ENTITY_NAME,
          result.getId().toString()
        )
      )
      .body(result);
  }

  /**
   * {@code PUT  /node-labels/:id} : Updates an existing nodeLabel.
   *
   * @param id the id of the nodeLabel to save.
   * @param nodeLabel the nodeLabel to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nodeLabel,
   * or with status {@code 400 (Bad Request)} if the nodeLabel is not valid,
   * or with status {@code 500 (Internal Server Error)} if the nodeLabel couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/node-labels/{id}")
  public ResponseEntity<NodeLabel> updateNodeLabel(
    @PathVariable(value = "id", required = false) final Long id,
    @RequestBody NodeLabel nodeLabel
  ) throws URISyntaxException {
    log.debug("REST request to update NodeLabel : {}, {}", id, nodeLabel);
    if (nodeLabel.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, nodeLabel.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!nodeLabelRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    NodeLabel result = nodeLabelService.save(nodeLabel);
    return ResponseEntity
      .ok()
      .headers(
        HeaderUtil.createEntityUpdateAlert(
          applicationName,
          false,
          ENTITY_NAME,
          nodeLabel.getId().toString()
        )
      )
      .body(result);
  }

  /**
   * {@code PATCH  /node-labels/:id} : Partial updates given fields of an existing nodeLabel, field will ignore if it is null
   *
   * @param id the id of the nodeLabel to save.
   * @param nodeLabel the nodeLabel to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nodeLabel,
   * or with status {@code 400 (Bad Request)} if the nodeLabel is not valid,
   * or with status {@code 404 (Not Found)} if the nodeLabel is not found,
   * or with status {@code 500 (Internal Server Error)} if the nodeLabel couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(
    value = "/node-labels/{id}",
    consumes = "application/merge-patch+json"
  )
  public ResponseEntity<NodeLabel> partialUpdateNodeLabel(
    @PathVariable(value = "id", required = false) final Long id,
    @RequestBody NodeLabel nodeLabel
  ) throws URISyntaxException {
    log.debug(
      "REST request to partial update NodeLabel partially : {}, {}",
      id,
      nodeLabel
    );
    if (nodeLabel.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, nodeLabel.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!nodeLabelRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    Optional<NodeLabel> result = nodeLabelService.partialUpdate(nodeLabel);

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(
        applicationName,
        false,
        ENTITY_NAME,
        nodeLabel.getId().toString()
      )
    );
  }

  /**
   * {@code GET  /node-labels} : get all the nodeLabels.
   *
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of nodeLabels in body.
   */
  @GetMapping("/node-labels")
  public List<NodeLabel> getAllNodeLabels() {
    log.debug("REST request to get all NodeLabels");
    return nodeLabelService.findAll();
  }

  /**
   * {@code GET  /node-labels/:id} : get the "id" nodeLabel.
   *
   * @param id the id of the nodeLabel to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the nodeLabel, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/node-labels/{id}")
  public ResponseEntity<NodeLabel> getNodeLabel(@PathVariable Long id) {
    log.debug("REST request to get NodeLabel : {}", id);
    Optional<NodeLabel> nodeLabel = nodeLabelService.findOne(id);
    return ResponseUtil.wrapOrNotFound(nodeLabel);
  }

  /**
   * {@code DELETE  /node-labels/:id} : delete the "id" nodeLabel.
   *
   * @param id the id of the nodeLabel to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/node-labels/{id}")
  public ResponseEntity<Void> deleteNodeLabel(@PathVariable Long id) {
    log.debug("REST request to delete NodeLabel : {}", id);
    nodeLabelService.delete(id);
    return ResponseEntity
      .noContent()
      .headers(
        HeaderUtil.createEntityDeletionAlert(
          applicationName,
          false,
          ENTITY_NAME,
          id.toString()
        )
      )
      .build();
  }
}
