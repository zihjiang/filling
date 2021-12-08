package com.filling.web.rest;

import com.filling.IntegrationTest;
import com.filling.domain.FillingJobsHistory;
import com.filling.repository.FillingJobsHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link FillingJobsHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FillingJobsHistoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_APPLICATION_ID = "AAAAAAAAAA";
    private static final String UPDATED_APPLICATION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_JOB_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_JOB_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CONF_PROP = "AAAAAAAAAA";
    private static final String UPDATED_CONF_PROP = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATETIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATETIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATETIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATETIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_ADDJAR = "AAAAAAAAAA";
    private static final String UPDATED_ADDJAR = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/filling-jobs-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FillingJobsHistoryRepository fillingJobsHistoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFillingJobsHistoryMockMvc;

    private FillingJobsHistory fillingJobsHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FillingJobsHistory createEntity(EntityManager em) {
        FillingJobsHistory fillingJobsHistory = new FillingJobsHistory()
            .name(DEFAULT_NAME)
            .applicationId(DEFAULT_APPLICATION_ID)
            .jobText(DEFAULT_JOB_TEXT)
            .type(DEFAULT_TYPE)
            .confProp(DEFAULT_CONF_PROP)
            .status(DEFAULT_STATUS)
            .createtime(DEFAULT_CREATETIME)
            .updatetime(DEFAULT_UPDATETIME)
            .createdBy(DEFAULT_CREATED_BY)
            .addjar(DEFAULT_ADDJAR)
            .description(DEFAULT_DESCRIPTION);
        return fillingJobsHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FillingJobsHistory createUpdatedEntity(EntityManager em) {
        FillingJobsHistory fillingJobsHistory = new FillingJobsHistory()
            .name(UPDATED_NAME)
            .applicationId(UPDATED_APPLICATION_ID)
            .jobText(UPDATED_JOB_TEXT)
            .type(UPDATED_TYPE)
            .confProp(UPDATED_CONF_PROP)
            .status(UPDATED_STATUS)
            .createtime(UPDATED_CREATETIME)
            .updatetime(UPDATED_UPDATETIME)
            .createdBy(UPDATED_CREATED_BY)
            .addjar(UPDATED_ADDJAR)
            .description(UPDATED_DESCRIPTION);
        return fillingJobsHistory;
    }

    @BeforeEach
    public void initTest() {
        fillingJobsHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createFillingJobsHistory() throws Exception {
        int databaseSizeBeforeCreate = fillingJobsHistoryRepository.findAll().size();
        // Create the FillingJobsHistory
        restFillingJobsHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fillingJobsHistory))
            )
            .andExpect(status().isCreated());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        FillingJobsHistory testFillingJobsHistory = fillingJobsHistoryList.get(fillingJobsHistoryList.size() - 1);
        assertThat(testFillingJobsHistory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFillingJobsHistory.getApplicationId()).isEqualTo(DEFAULT_APPLICATION_ID);
        assertThat(testFillingJobsHistory.getJobText()).isEqualTo(DEFAULT_JOB_TEXT);
        assertThat(testFillingJobsHistory.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testFillingJobsHistory.getConfProp()).isEqualTo(DEFAULT_CONF_PROP);
        assertThat(testFillingJobsHistory.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testFillingJobsHistory.getCreatetime()).isEqualTo(DEFAULT_CREATETIME);
        assertThat(testFillingJobsHistory.getUpdatetime()).isEqualTo(DEFAULT_UPDATETIME);
        assertThat(testFillingJobsHistory.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testFillingJobsHistory.getAddjar()).isEqualTo(DEFAULT_ADDJAR);
        assertThat(testFillingJobsHistory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createFillingJobsHistoryWithExistingId() throws Exception {
        // Create the FillingJobsHistory with an existing ID
        fillingJobsHistory.setId(1L);

        int databaseSizeBeforeCreate = fillingJobsHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFillingJobsHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fillingJobsHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFillingJobsHistories() throws Exception {
        // Initialize the database
        fillingJobsHistoryRepository.saveAndFlush(fillingJobsHistory);

        // Get all the fillingJobsHistoryList
        restFillingJobsHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fillingJobsHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].applicationId").value(hasItem(DEFAULT_APPLICATION_ID)))
            .andExpect(jsonPath("$.[*].jobText").value(hasItem(DEFAULT_JOB_TEXT)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].confProp").value(hasItem(DEFAULT_CONF_PROP)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createtime").value(hasItem(DEFAULT_CREATETIME.toString())))
            .andExpect(jsonPath("$.[*].updatetime").value(hasItem(DEFAULT_UPDATETIME.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].addjar").value(hasItem(DEFAULT_ADDJAR)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getFillingJobsHistory() throws Exception {
        // Initialize the database
        fillingJobsHistoryRepository.saveAndFlush(fillingJobsHistory);

        // Get the fillingJobsHistory
        restFillingJobsHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, fillingJobsHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fillingJobsHistory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.applicationId").value(DEFAULT_APPLICATION_ID))
            .andExpect(jsonPath("$.jobText").value(DEFAULT_JOB_TEXT))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.confProp").value(DEFAULT_CONF_PROP))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createtime").value(DEFAULT_CREATETIME.toString()))
            .andExpect(jsonPath("$.updatetime").value(DEFAULT_UPDATETIME.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.addjar").value(DEFAULT_ADDJAR))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingFillingJobsHistory() throws Exception {
        // Get the fillingJobsHistory
        restFillingJobsHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFillingJobsHistory() throws Exception {
        // Initialize the database
        fillingJobsHistoryRepository.saveAndFlush(fillingJobsHistory);

        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();

        // Update the fillingJobsHistory
        FillingJobsHistory updatedFillingJobsHistory = fillingJobsHistoryRepository.findById(fillingJobsHistory.getId()).get();
        // Disconnect from session so that the updates on updatedFillingJobsHistory are not directly saved in db
        em.detach(updatedFillingJobsHistory);
        updatedFillingJobsHistory
            .name(UPDATED_NAME)
            .applicationId(UPDATED_APPLICATION_ID)
            .jobText(UPDATED_JOB_TEXT)
            .type(UPDATED_TYPE)
            .confProp(UPDATED_CONF_PROP)
            .status(UPDATED_STATUS)
            .createtime(UPDATED_CREATETIME)
            .updatetime(UPDATED_UPDATETIME)
            .createdBy(UPDATED_CREATED_BY)
            .addjar(UPDATED_ADDJAR)
            .description(UPDATED_DESCRIPTION);

        restFillingJobsHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFillingJobsHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFillingJobsHistory))
            )
            .andExpect(status().isOk());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
        FillingJobsHistory testFillingJobsHistory = fillingJobsHistoryList.get(fillingJobsHistoryList.size() - 1);
        assertThat(testFillingJobsHistory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFillingJobsHistory.getApplicationId()).isEqualTo(UPDATED_APPLICATION_ID);
        assertThat(testFillingJobsHistory.getJobText()).isEqualTo(UPDATED_JOB_TEXT);
        assertThat(testFillingJobsHistory.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFillingJobsHistory.getConfProp()).isEqualTo(UPDATED_CONF_PROP);
        assertThat(testFillingJobsHistory.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFillingJobsHistory.getCreatetime()).isEqualTo(UPDATED_CREATETIME);
        assertThat(testFillingJobsHistory.getUpdatetime()).isEqualTo(UPDATED_UPDATETIME);
        assertThat(testFillingJobsHistory.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testFillingJobsHistory.getAddjar()).isEqualTo(UPDATED_ADDJAR);
        assertThat(testFillingJobsHistory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingFillingJobsHistory() throws Exception {
        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();
        fillingJobsHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFillingJobsHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fillingJobsHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fillingJobsHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFillingJobsHistory() throws Exception {
        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();
        fillingJobsHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFillingJobsHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fillingJobsHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFillingJobsHistory() throws Exception {
        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();
        fillingJobsHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFillingJobsHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fillingJobsHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFillingJobsHistoryWithPatch() throws Exception {
        // Initialize the database
        fillingJobsHistoryRepository.saveAndFlush(fillingJobsHistory);

        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();

        // Update the fillingJobsHistory using partial update
        FillingJobsHistory partialUpdatedFillingJobsHistory = new FillingJobsHistory();
        partialUpdatedFillingJobsHistory.setId(fillingJobsHistory.getId());

        partialUpdatedFillingJobsHistory
            .jobText(UPDATED_JOB_TEXT)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .updatetime(UPDATED_UPDATETIME)
            .createdBy(UPDATED_CREATED_BY);

        restFillingJobsHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFillingJobsHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFillingJobsHistory))
            )
            .andExpect(status().isOk());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
        FillingJobsHistory testFillingJobsHistory = fillingJobsHistoryList.get(fillingJobsHistoryList.size() - 1);
        assertThat(testFillingJobsHistory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFillingJobsHistory.getApplicationId()).isEqualTo(DEFAULT_APPLICATION_ID);
        assertThat(testFillingJobsHistory.getJobText()).isEqualTo(UPDATED_JOB_TEXT);
        assertThat(testFillingJobsHistory.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFillingJobsHistory.getConfProp()).isEqualTo(DEFAULT_CONF_PROP);
        assertThat(testFillingJobsHistory.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFillingJobsHistory.getCreatetime()).isEqualTo(DEFAULT_CREATETIME);
        assertThat(testFillingJobsHistory.getUpdatetime()).isEqualTo(UPDATED_UPDATETIME);
        assertThat(testFillingJobsHistory.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testFillingJobsHistory.getAddjar()).isEqualTo(DEFAULT_ADDJAR);
        assertThat(testFillingJobsHistory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateFillingJobsHistoryWithPatch() throws Exception {
        // Initialize the database
        fillingJobsHistoryRepository.saveAndFlush(fillingJobsHistory);

        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();

        // Update the fillingJobsHistory using partial update
        FillingJobsHistory partialUpdatedFillingJobsHistory = new FillingJobsHistory();
        partialUpdatedFillingJobsHistory.setId(fillingJobsHistory.getId());

        partialUpdatedFillingJobsHistory
            .name(UPDATED_NAME)
            .applicationId(UPDATED_APPLICATION_ID)
            .jobText(UPDATED_JOB_TEXT)
            .type(UPDATED_TYPE)
            .confProp(UPDATED_CONF_PROP)
            .status(UPDATED_STATUS)
            .createtime(UPDATED_CREATETIME)
            .updatetime(UPDATED_UPDATETIME)
            .createdBy(UPDATED_CREATED_BY)
            .addjar(UPDATED_ADDJAR)
            .description(UPDATED_DESCRIPTION);

        restFillingJobsHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFillingJobsHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFillingJobsHistory))
            )
            .andExpect(status().isOk());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
        FillingJobsHistory testFillingJobsHistory = fillingJobsHistoryList.get(fillingJobsHistoryList.size() - 1);
        assertThat(testFillingJobsHistory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFillingJobsHistory.getApplicationId()).isEqualTo(UPDATED_APPLICATION_ID);
        assertThat(testFillingJobsHistory.getJobText()).isEqualTo(UPDATED_JOB_TEXT);
        assertThat(testFillingJobsHistory.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testFillingJobsHistory.getConfProp()).isEqualTo(UPDATED_CONF_PROP);
        assertThat(testFillingJobsHistory.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFillingJobsHistory.getCreatetime()).isEqualTo(UPDATED_CREATETIME);
        assertThat(testFillingJobsHistory.getUpdatetime()).isEqualTo(UPDATED_UPDATETIME);
        assertThat(testFillingJobsHistory.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testFillingJobsHistory.getAddjar()).isEqualTo(UPDATED_ADDJAR);
        assertThat(testFillingJobsHistory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingFillingJobsHistory() throws Exception {
        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();
        fillingJobsHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFillingJobsHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fillingJobsHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fillingJobsHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFillingJobsHistory() throws Exception {
        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();
        fillingJobsHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFillingJobsHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fillingJobsHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFillingJobsHistory() throws Exception {
        int databaseSizeBeforeUpdate = fillingJobsHistoryRepository.findAll().size();
        fillingJobsHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFillingJobsHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fillingJobsHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FillingJobsHistory in the database
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFillingJobsHistory() throws Exception {
        // Initialize the database
        fillingJobsHistoryRepository.saveAndFlush(fillingJobsHistory);

        int databaseSizeBeforeDelete = fillingJobsHistoryRepository.findAll().size();

        // Delete the fillingJobsHistory
        restFillingJobsHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, fillingJobsHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FillingJobsHistory> fillingJobsHistoryList = fillingJobsHistoryRepository.findAll();
        assertThat(fillingJobsHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
