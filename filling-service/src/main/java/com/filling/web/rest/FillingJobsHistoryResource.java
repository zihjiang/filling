package com.filling.web.rest;

import com.filling.domain.FillingJobsHistory;
import com.filling.repository.FillingJobsHistoryRepository;
import com.filling.service.FillingJobsHistoryService;
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
 * REST controller for managing {@link com.filling.domain.FillingJobsHistory}.
 */
@RestController
@RequestMapping("/api")
public class FillingJobsHistoryResource {

    private final Logger log = LoggerFactory.getLogger(FillingJobsHistoryResource.class);

    private static final String ENTITY_NAME = "fillingJobsHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FillingJobsHistoryService fillingJobsHistoryService;

    private final FillingJobsHistoryRepository fillingJobsHistoryRepository;

    public FillingJobsHistoryResource(
        FillingJobsHistoryService fillingJobsHistoryService,
        FillingJobsHistoryRepository fillingJobsHistoryRepository
    ) {
        this.fillingJobsHistoryService = fillingJobsHistoryService;
        this.fillingJobsHistoryRepository = fillingJobsHistoryRepository;
    }

    /**
     * {@code POST  /filling-jobs-histories} : Create a new fillingJobsHistory.
     *
     * @param fillingJobsHistory the fillingJobsHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fillingJobsHistory, or with status {@code 400 (Bad Request)} if the fillingJobsHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/filling-jobs-histories")
    public ResponseEntity<FillingJobsHistory> createFillingJobsHistory(@RequestBody FillingJobsHistory fillingJobsHistory)
        throws URISyntaxException {
        log.debug("REST request to save FillingJobsHistory : {}", fillingJobsHistory);
        if (fillingJobsHistory.getId() != null) {
            throw new BadRequestAlertException("A new fillingJobsHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FillingJobsHistory result = fillingJobsHistoryService.save(fillingJobsHistory);
        return ResponseEntity
            .created(new URI("/api/filling-jobs-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /filling-jobs-histories/:id} : Updates an existing fillingJobsHistory.
     *
     * @param id the id of the fillingJobsHistory to save.
     * @param fillingJobsHistory the fillingJobsHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fillingJobsHistory,
     * or with status {@code 400 (Bad Request)} if the fillingJobsHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fillingJobsHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/filling-jobs-histories/{id}")
    public ResponseEntity<FillingJobsHistory> updateFillingJobsHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FillingJobsHistory fillingJobsHistory
    ) throws URISyntaxException {
        log.debug("REST request to update FillingJobsHistory : {}, {}", id, fillingJobsHistory);
        if (fillingJobsHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fillingJobsHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fillingJobsHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FillingJobsHistory result = fillingJobsHistoryService.save(fillingJobsHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fillingJobsHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /filling-jobs-histories/:id} : Partial updates given fields of an existing fillingJobsHistory, field will ignore if it is null
     *
     * @param id the id of the fillingJobsHistory to save.
     * @param fillingJobsHistory the fillingJobsHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fillingJobsHistory,
     * or with status {@code 400 (Bad Request)} if the fillingJobsHistory is not valid,
     * or with status {@code 404 (Not Found)} if the fillingJobsHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the fillingJobsHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/filling-jobs-histories/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<FillingJobsHistory> partialUpdateFillingJobsHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FillingJobsHistory fillingJobsHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update FillingJobsHistory partially : {}, {}", id, fillingJobsHistory);
        if (fillingJobsHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fillingJobsHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fillingJobsHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FillingJobsHistory> result = fillingJobsHistoryService.partialUpdate(fillingJobsHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fillingJobsHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /filling-jobs-histories} : get all the fillingJobsHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fillingJobsHistories in body.
     */
    @GetMapping("/filling-jobs-histories")
    public ResponseEntity<List<FillingJobsHistory>> getAllFillingJobsHistories(Pageable pageable) {
        log.debug("REST request to get a page of FillingJobsHistories");
        Page<FillingJobsHistory> page = fillingJobsHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /filling-jobs-histories/:id} : get the "id" fillingJobsHistory.
     *
     * @param id the id of the fillingJobsHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fillingJobsHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/filling-jobs-histories/{id}")
    public ResponseEntity<FillingJobsHistory> getFillingJobsHistory(@PathVariable Long id) {
        log.debug("REST request to get FillingJobsHistory : {}", id);
        Optional<FillingJobsHistory> fillingJobsHistory = fillingJobsHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fillingJobsHistory);
    }

    /**
     * {@code DELETE  /filling-jobs-histories/:id} : delete the "id" fillingJobsHistory.
     *
     * @param id the id of the fillingJobsHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/filling-jobs-histories/{id}")
    public ResponseEntity<Void> deleteFillingJobsHistory(@PathVariable Long id) {
        log.debug("REST request to delete FillingJobsHistory : {}", id);
        fillingJobsHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
