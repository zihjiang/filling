package com.filling.web.rest;

import com.filling.domain.FillingEdgeNodes;
import com.filling.repository.FillingEdgeNodesRepository;
import com.filling.service.FillingEdgeNodesService;
import com.filling.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.filling.web.rest.vm.ResultVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.filling.domain.FillingEdgeNodes}.
 */
@RestController
@RequestMapping("/api")
public class FillingEdgeNodesResource {

  private final Logger log = LoggerFactory.getLogger(
    FillingEdgeNodesResource.class
  );

  private static final String ENTITY_NAME = "fillingEdgeNodes";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final FillingEdgeNodesService fillingEdgeNodesService;

  private final FillingEdgeNodesRepository fillingEdgeNodesRepository;

  public FillingEdgeNodesResource(
    FillingEdgeNodesService fillingEdgeNodesService,
    FillingEdgeNodesRepository fillingEdgeNodesRepository
  ) {
    this.fillingEdgeNodesService = fillingEdgeNodesService;
    this.fillingEdgeNodesRepository = fillingEdgeNodesRepository;
  }

  /**
   * {@code POST  /filling-edge-nodes} : Create a new fillingEdgeNodes.
   *
   * @param fillingEdgeNodes the fillingEdgeNodes to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fillingEdgeNodes, or with status {@code 400 (Bad Request)} if the fillingEdgeNodes has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/filling-edge-nodes")
  public ResponseEntity<FillingEdgeNodes> createFillingEdgeNodes(
    @RequestBody FillingEdgeNodes fillingEdgeNodes
  ) throws URISyntaxException {
    log.debug("REST request to save FillingEdgeNodes : {}", fillingEdgeNodes);
    if (fillingEdgeNodes.getId() != null) {
      throw new BadRequestAlertException(
        "A new fillingEdgeNodes cannot already have an ID",
        ENTITY_NAME,
        "idexists"
      );
    }
    FillingEdgeNodes result = fillingEdgeNodesService.save(fillingEdgeNodes);
    return ResponseEntity
      .created(new URI("/api/filling-edge-nodes/" + result.getId()))
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
   * {@code PUT  /filling-edge-nodes/:id} : Updates an existing fillingEdgeNodes.
   *
   * @param id the id of the fillingEdgeNodes to save.
   * @param fillingEdgeNodes the fillingEdgeNodes to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fillingEdgeNodes,
   * or with status {@code 400 (Bad Request)} if the fillingEdgeNodes is not valid,
   * or with status {@code 500 (Internal Server Error)} if the fillingEdgeNodes couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/filling-edge-nodes/{id}")
  public ResponseEntity<FillingEdgeNodes> updateFillingEdgeNodes(
    @PathVariable(value = "id", required = false) final Long id,
    @RequestBody FillingEdgeNodes fillingEdgeNodes
  ) throws URISyntaxException {
    log.debug(
      "REST request to update FillingEdgeNodes : {}, {}",
      id,
      fillingEdgeNodes
    );
    if (fillingEdgeNodes.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, fillingEdgeNodes.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!fillingEdgeNodesRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    FillingEdgeNodes result = fillingEdgeNodesService.save(fillingEdgeNodes);
    return ResponseEntity
      .ok()
      .headers(
        HeaderUtil.createEntityUpdateAlert(
          applicationName,
          false,
          ENTITY_NAME,
          fillingEdgeNodes.getId().toString()
        )
      )
      .body(result);
  }

  /**
   * {@code PATCH  /filling-edge-nodes/:id} : Partial updates given fields of an existing fillingEdgeNodes, field will ignore if it is null
   *
   * @param id the id of the fillingEdgeNodes to save.
   * @param fillingEdgeNodes the fillingEdgeNodes to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fillingEdgeNodes,
   * or with status {@code 400 (Bad Request)} if the fillingEdgeNodes is not valid,
   * or with status {@code 404 (Not Found)} if the fillingEdgeNodes is not found,
   * or with status {@code 500 (Internal Server Error)} if the fillingEdgeNodes couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(
    value = "/filling-edge-nodes/{id}",
    consumes = "application/merge-patch+json"
  )
  public ResponseEntity<FillingEdgeNodes> partialUpdateFillingEdgeNodes(
    @PathVariable(value = "id", required = false) final Long id,
    @RequestBody FillingEdgeNodes fillingEdgeNodes
  ) throws URISyntaxException {
    log.debug(
      "REST request to partial update FillingEdgeNodes partially : {}, {}",
      id,
      fillingEdgeNodes
    );
    if (fillingEdgeNodes.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, fillingEdgeNodes.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!fillingEdgeNodesRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    Optional<FillingEdgeNodes> result = fillingEdgeNodesService.partialUpdate(
      fillingEdgeNodes
    );

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(
        applicationName,
        false,
        ENTITY_NAME,
        fillingEdgeNodes.getId().toString()
      )
    );
  }

  /**
   * {@code GET  /filling-edge-nodes} : get all the fillingEdgeNodes.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fillingEdgeNodes in body.
   */
  @GetMapping("/filling-edge-nodes")
  public ResponseEntity<ResultVM> getAllFillingEdgeNodes(
    Pageable pageable
  ) {
    log.debug("REST request to get a page of FillingEdgeNodes");
    Page<FillingEdgeNodes> page = fillingEdgeNodesService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
      ServletUriComponentsBuilder.fromCurrentRequest(),
      page
    );

      ResultVM resultVM = new ResultVM();
      resultVM.setData(page.getContent());
      resultVM.setCurrent(page.getNumber());
      resultVM.setPageSize(page.getSize());
      resultVM.setTotal(page.getTotalElements());
      resultVM.setSuccess(true);
      resultVM.setData(page.getContent());

      return ResponseEntity.ok().headers(headers).body(resultVM);
  }

  /**
   * {@code GET  /filling-edge-nodes/:id} : get the "id" fillingEdgeNodes.
   *
   * @param id the id of the fillingEdgeNodes to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fillingEdgeNodes, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/filling-edge-nodes/{id}")
  public ResponseEntity<FillingEdgeNodes> getFillingEdgeNodes(
    @PathVariable Long id
  ) {
    log.debug("REST request to get FillingEdgeNodes : {}", id);
    Optional<FillingEdgeNodes> fillingEdgeNodes = fillingEdgeNodesService.findOne(
      id
    );
    return ResponseUtil.wrapOrNotFound(fillingEdgeNodes);
  }

  /**
   * {@code DELETE  /filling-edge-nodes/:id} : delete the "id" fillingEdgeNodes.
   *
   * @param id the id of the fillingEdgeNodes to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/filling-edge-nodes/{id}")
  public ResponseEntity<Void> deleteFillingEdgeNodes(@PathVariable Long id) {
    log.debug("REST request to delete FillingEdgeNodes : {}", id);
    fillingEdgeNodesService.delete(id);
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
