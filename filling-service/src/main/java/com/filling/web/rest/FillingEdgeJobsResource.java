package com.filling.web.rest;

import com.filling.domain.FillingEdgeJobs;
import com.filling.repository.FillingEdgeJobsRepository;
import com.filling.service.FillingEdgeJobsService;
import com.filling.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.filling.domain.FillingEdgeJobs}.
 */
@RestController
@RequestMapping("/api")
public class FillingEdgeJobsResource {

  private final Logger log = LoggerFactory.getLogger(
    FillingEdgeJobsResource.class
  );

  private static final String ENTITY_NAME = "fillingEdgeJobs";

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  private final FillingEdgeJobsService fillingEdgeJobsService;

  private final FillingEdgeJobsRepository fillingEdgeJobsRepository;

  public FillingEdgeJobsResource(
    FillingEdgeJobsService fillingEdgeJobsService,
    FillingEdgeJobsRepository fillingEdgeJobsRepository
  ) {
    this.fillingEdgeJobsService = fillingEdgeJobsService;
    this.fillingEdgeJobsRepository = fillingEdgeJobsRepository;
  }

  /**
   * {@code POST  /filling-edge-jobs} : Create a new fillingEdgeJobs.
   *
   * @param fillingEdgeJobs the fillingEdgeJobs to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fillingEdgeJobs, or with status {@code 400 (Bad Request)} if the fillingEdgeJobs has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/filling-edge-jobs")
  public ResponseEntity<FillingEdgeJobs> createFillingEdgeJobs(
    @RequestBody FillingEdgeJobs fillingEdgeJobs
  ) throws URISyntaxException {
    log.debug("REST request to save FillingEdgeJobs : {}", fillingEdgeJobs);
    if (fillingEdgeJobs.getId() != null) {
      throw new BadRequestAlertException(
        "A new fillingEdgeJobs cannot already have an ID",
        ENTITY_NAME,
        "idexists"
      );
    }
    FillingEdgeJobs result = fillingEdgeJobsService.save(fillingEdgeJobs);
    return ResponseEntity
      .created(new URI("/api/filling-edge-jobs/" + result.getId()))
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
   * {@code PUT  /filling-edge-jobs/:id} : Updates an existing fillingEdgeJobs.
   *
   * @param id the id of the fillingEdgeJobs to save.
   * @param fillingEdgeJobs the fillingEdgeJobs to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fillingEdgeJobs,
   * or with status {@code 400 (Bad Request)} if the fillingEdgeJobs is not valid,
   * or with status {@code 500 (Internal Server Error)} if the fillingEdgeJobs couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/filling-edge-jobs/{id}")
  public ResponseEntity<FillingEdgeJobs> updateFillingEdgeJobs(
    @PathVariable(value = "id", required = false) final Long id,
    @RequestBody FillingEdgeJobs fillingEdgeJobs
  ) throws URISyntaxException {
    log.debug(
      "REST request to update FillingEdgeJobs : {}, {}",
      id,
      fillingEdgeJobs
    );
    if (fillingEdgeJobs.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, fillingEdgeJobs.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!fillingEdgeJobsRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    FillingEdgeJobs result = fillingEdgeJobsService.save(fillingEdgeJobs);
    return ResponseEntity
      .ok()
      .headers(
        HeaderUtil.createEntityUpdateAlert(
          applicationName,
          false,
          ENTITY_NAME,
          fillingEdgeJobs.getId().toString()
        )
      )
      .body(result);
  }

  /**
   * {@code PATCH  /filling-edge-jobs/:id} : Partial updates given fields of an existing fillingEdgeJobs, field will ignore if it is null
   *
   * @param id the id of the fillingEdgeJobs to save.
   * @param fillingEdgeJobs the fillingEdgeJobs to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fillingEdgeJobs,
   * or with status {@code 400 (Bad Request)} if the fillingEdgeJobs is not valid,
   * or with status {@code 404 (Not Found)} if the fillingEdgeJobs is not found,
   * or with status {@code 500 (Internal Server Error)} if the fillingEdgeJobs couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(
    value = "/filling-edge-jobs/{id}",
    consumes = "application/merge-patch+json"
  )
  public ResponseEntity<FillingEdgeJobs> partialUpdateFillingEdgeJobs(
    @PathVariable(value = "id", required = false) final Long id,
    @RequestBody FillingEdgeJobs fillingEdgeJobs
  ) throws URISyntaxException {
    log.debug(
      "REST request to partial update FillingEdgeJobs partially : {}, {}",
      id,
      fillingEdgeJobs
    );
    if (fillingEdgeJobs.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, fillingEdgeJobs.getId())) {
      throw new BadRequestAlertException(
        "Invalid ID",
        ENTITY_NAME,
        "idinvalid"
      );
    }

    if (!fillingEdgeJobsRepository.existsById(id)) {
      throw new BadRequestAlertException(
        "Entity not found",
        ENTITY_NAME,
        "idnotfound"
      );
    }

    Optional<FillingEdgeJobs> result = fillingEdgeJobsService.partialUpdate(
      fillingEdgeJobs
    );

    return ResponseUtil.wrapOrNotFound(
      result,
      HeaderUtil.createEntityUpdateAlert(
        applicationName,
        false,
        ENTITY_NAME,
        fillingEdgeJobs.getId().toString()
      )
    );
  }

  /**
   * {@code GET  /filling-edge-jobs} : get all the fillingEdgeJobs.
   *
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fillingEdgeJobs in body.
   */
  @GetMapping("/filling-edge-jobs")
  public ResponseEntity<List<FillingEdgeJobs>> getAllFillingEdgeJobs(
    Pageable pageable
  ) {
    log.debug("REST request to get a page of FillingEdgeJobs");
    Page<FillingEdgeJobs> page = fillingEdgeJobsService.findAll(pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
      ServletUriComponentsBuilder.fromCurrentRequest(),
      page
    );
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /filling-edge-jobs/:id} : get the "id" fillingEdgeJobs.
   *
   * @param id the id of the fillingEdgeJobs to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fillingEdgeJobs, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/filling-edge-jobs/{id}")
  public ResponseEntity<FillingEdgeJobs> getFillingEdgeJobs(
    @PathVariable Long id
  ) {
    log.debug("REST request to get FillingEdgeJobs : {}", id);
    Optional<FillingEdgeJobs> fillingEdgeJobs = fillingEdgeJobsService.findOne(
      id
    );
    return ResponseUtil.wrapOrNotFound(fillingEdgeJobs);
  }

  /**
   * {@code DELETE  /filling-edge-jobs/:id} : delete the "id" fillingEdgeJobs.
   *
   * @param id the id of the fillingEdgeJobs to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/filling-edge-jobs/{id}")
  public ResponseEntity<Void> deleteFillingEdgeJobs(@PathVariable Long id) {
    log.debug("REST request to delete FillingEdgeJobs : {}", id);
    fillingEdgeJobsService.delete(id);
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
