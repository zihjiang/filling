package com.filling.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.filling.config.ApplicationProperties;
import com.filling.domain.FillingJobs;
import com.filling.repository.FillingJobsRepository;
import com.filling.service.FillingJobsService;
import com.filling.web.rest.errors.BadRequestAlertException;
import com.filling.web.rest.vm.ResultVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.filling.domain.FillingJobs}.
 */
@RestController
@RequestMapping("/api")
public class FillingJobsResource {

    private final Logger log = LoggerFactory.getLogger(FillingJobsResource.class);

    private static final String ENTITY_NAME = "fillingJobs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;


    private final FillingJobsService fillingJobsService;

    private final FillingJobsRepository fillingJobsRepository;

    @Autowired
    ApplicationProperties flink;

    public FillingJobsResource(FillingJobsService fillingJobsService, FillingJobsRepository fillingJobsRepository) {
        this.fillingJobsService = fillingJobsService;
        this.fillingJobsRepository = fillingJobsRepository;
    }

    /**
     * {@code POST  /filling-jobs} : Create a new fillingJobs.
     *
     * @param fillingJobs the fillingJobs to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fillingJobs, or with status {@code 400 (Bad Request)} if the fillingJobs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/filling-jobs")
    public ResponseEntity<FillingJobs> createFillingJobs(@RequestBody FillingJobs fillingJobs) throws URISyntaxException {
        log.debug("REST request to save FillingJobs : {}", fillingJobs);
        if (fillingJobs.getId() != null) {
            throw new BadRequestAlertException("A new fillingJobs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FillingJobs result = fillingJobsService.save(fillingJobs);
        return ResponseEntity
            .created(new URI("/api/filling-jobs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /filling-jobs/:id} : Updates an existing fillingJobs.
     *
     * @param id the id of the fillingJobs to save.
     * @param fillingJobs the fillingJobs to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fillingJobs,
     * or with status {@code 400 (Bad Request)} if the fillingJobs is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fillingJobs couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/filling-jobs/{id}")
    public ResponseEntity<FillingJobs> updateFillingJobs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FillingJobs fillingJobs
    ) throws URISyntaxException {
        log.debug("REST request to update FillingJobs : {}, {}", id, fillingJobs);
        if (fillingJobs.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fillingJobs.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fillingJobsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FillingJobs result = fillingJobsService.save(fillingJobs);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fillingJobs.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /filling-jobs/:id} : Partial updates given fields of an existing fillingJobs, field will ignore if it is null
     *
     * @param id the id of the fillingJobs to save.
     * @param fillingJobs the fillingJobs to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fillingJobs,
     * or with status {@code 400 (Bad Request)} if the fillingJobs is not valid,
     * or with status {@code 404 (Not Found)} if the fillingJobs is not found,
     * or with status {@code 500 (Internal Server Error)} if the fillingJobs couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/filling-jobs/{id}")
    public ResponseEntity<FillingJobs> partialUpdateFillingJobs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FillingJobs fillingJobs
    ) throws URISyntaxException {
        log.debug("REST request to partial update FillingJobs partially : {}, {}", id, fillingJobs);
        if (fillingJobs.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fillingJobs.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fillingJobsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FillingJobs> result = fillingJobsService.partialUpdate(fillingJobs);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fillingJobs.getId().toString())
        );
    }

    /**
     * {@code GET  /filling-jobs} : get all the fillingJobs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fillingJobs in body.
     */
    @GetMapping("/filling-jobs")
    public ResponseEntity<ResultVM> getAllFillingJobs(Pageable pageable) {
        log.debug("REST request to get a page of FillingJobs");
        Page<FillingJobs> page = fillingJobsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

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
     * {@code GET  /filling-jobs/:id} : get the "id" fillingJobs.
     *
     * @param id the id of the fillingJobs to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fillingJobs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/filling-jobs/{id}")
    public ResponseEntity<FillingJobs> getFillingJobs(@PathVariable Long id) {
        log.debug("REST request to get FillingJobs : {}", id);
        Optional<FillingJobs> fillingJobs = fillingJobsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fillingJobs);
    }
    /**
     * {@code DELETE  /filling-jobs/:id} : delete the "id" fillingJobs.
     *
     * @param id the id of the fillingJobs to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/filling-jobs/{id}")
    public ResponseEntity<Void> deleteFillingJobs(@PathVariable Long id) {
        log.debug("REST request to delete FillingJobs : {}", id);
        fillingJobsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code start  /filling-jobs/:id/start} : start the "id" fillingJobs.
     * @param id
     * @return
     */
    @GetMapping("/filling-jobs/{id}/start")
    public ResponseEntity<FillingJobs> startFillingJobs(@PathVariable Long id) {
        log.debug("REST request to start FillingJobs : {}", id);
        Optional<FillingJobs> fillingJobs = fillingJobsService.findOne(id);
        if(fillingJobs.isPresent()) {
            fillingJobsService.start(fillingJobs.get());

        }
        return ResponseUtil.wrapOrNotFound(fillingJobsService.findOne(id));
    }

    @GetMapping("/filling-jobs/{id}/stop")
    public ResponseEntity<FillingJobs> stopFillingJobs(@PathVariable Long id) {
        log.debug("REST request to stop FillingJobs : {}", id);
        Optional<FillingJobs> fillingJobs = fillingJobsService.findOne(id);
        if(fillingJobs.isPresent()) {
            fillingJobsService.stop(fillingJobs.get());

        }
        return ResponseUtil.wrapOrNotFound(fillingJobsService.findOne(id));
    }

    @GetMapping("/filling-jobs/{id}/plan")
    public ResponseEntity<JSONObject> planFillingJobs(@PathVariable Long id) {
        log.debug("REST request to plan FillingJobs : {}", id);
        Optional<FillingJobs> fillingJobs = fillingJobsService.findOne(id);
        return ResponseEntity.ok(fillingJobsService.plan(fillingJobs.get()));
    }

    @GetMapping("/filling-jobs/overview/{id}")
    public void planFillingJobsOverview(@PathVariable Long id, HttpServletResponse response) throws IOException {
        log.debug("REST request to overview FillingJobs : {}", id);
        Optional<FillingJobs> fillingJobs = fillingJobsService.findOne(id);

        response.sendRedirect(flink.getUrl() + "/#/job/" + fillingJobs.get().getApplicationId() + "/overview");
    }
}
