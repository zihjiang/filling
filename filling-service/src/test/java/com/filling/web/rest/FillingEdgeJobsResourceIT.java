package com.filling.web.rest;

import com.filling.IntegrationTest;
import com.filling.domain.FillingEdgeJobs;
import com.filling.repository.FillingEdgeJobsRepository;
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
 * Integration tests for the {@link FillingEdgeJobsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FillingEdgeJobsResourceIT {

  private static final String DEFAULT_NAME = "AAAAAAAAAA";
  private static final String UPDATED_NAME = "BBBBBBBBBB";

  private static final String DEFAULT_PIPELINE_ID = "AAAAAAAAAA";
  private static final String UPDATED_PIPELINE_ID = "BBBBBBBBBB";

  private static final String DEFAULT_TITLE = "AAAAAAAAAA";
  private static final String UPDATED_TITLE = "BBBBBBBBBB";

  private static final String DEFAULT_UUID = "AAAAAAAAAA";
  private static final String UPDATED_UUID = "BBBBBBBBBB";

  private static final Boolean DEFAULT_VALID = false;
  private static final Boolean UPDATED_VALID = true;

  private static final String DEFAULT_METADATA = "AAAAAAAAAA";
  private static final String UPDATED_METADATA = "BBBBBBBBBB";

  private static final String DEFAULT_CTL_VERSION = "AAAAAAAAAA";
  private static final String UPDATED_CTL_VERSION = "BBBBBBBBBB";

  private static final String DEFAULT_CTL_ID = "AAAAAAAAAA";
  private static final String UPDATED_CTL_ID = "BBBBBBBBBB";

  private static final String DEFAULT_UI_INFO = "AAAAAAAAAA";
  private static final String UPDATED_UI_INFO = "BBBBBBBBBB";

  private static final String DEFAULT_INFO = "AAAAAAAAAA";
  private static final String UPDATED_INFO = "BBBBBBBBBB";

  private static final String DEFAULT_JOB_TEXT = "AAAAAAAAAA";
  private static final String UPDATED_JOB_TEXT = "BBBBBBBBBB";

  private static final String DEFAULT_STATUS = "AAAAAAAAAA";
  private static final String UPDATED_STATUS = "BBBBBBBBBB";

  private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
  private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

  private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
  private static final Instant UPDATED_CREATED = Instant
    .now()
    .truncatedTo(ChronoUnit.MILLIS);

  private static final Instant DEFAULT_LAST_MODIFIED = Instant.ofEpochMilli(0L);
  private static final Instant UPDATED_LAST_MODIFIED = Instant
    .now()
    .truncatedTo(ChronoUnit.MILLIS);

  private static final String DEFAULT_CREATOR = "AAAAAAAAAA";
  private static final String UPDATED_CREATOR = "BBBBBBBBBB";

  private static final String DEFAULT_LAST_MODIFIER = "AAAAAAAAAA";
  private static final String UPDATED_LAST_MODIFIER = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/filling-edge-jobs";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong count = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );

  @Autowired
  private FillingEdgeJobsRepository fillingEdgeJobsRepository;

  @Autowired
  private EntityManager em;

  @Autowired
  private MockMvc restFillingEdgeJobsMockMvc;

  private FillingEdgeJobs fillingEdgeJobs;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static FillingEdgeJobs createEntity(EntityManager em) {
    FillingEdgeJobs fillingEdgeJobs = new FillingEdgeJobs()
      .name(DEFAULT_NAME)
      .pipelineId(DEFAULT_PIPELINE_ID)
      .title(DEFAULT_TITLE)
      .uuid(DEFAULT_UUID)
      .valid(DEFAULT_VALID)
      .metadata(DEFAULT_METADATA)
      .ctlVersion(DEFAULT_CTL_VERSION)
      .ctlId(DEFAULT_CTL_ID)
      .uiInfo(DEFAULT_UI_INFO)
      .info(DEFAULT_INFO)
      .jobText(DEFAULT_JOB_TEXT)
      .status(DEFAULT_STATUS)
      .description(DEFAULT_DESCRIPTION)
      .created(DEFAULT_CREATED)
      .lastModified(DEFAULT_LAST_MODIFIED)
      .creator(DEFAULT_CREATOR)
      .lastModifier(DEFAULT_LAST_MODIFIER);
    return fillingEdgeJobs;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static FillingEdgeJobs createUpdatedEntity(EntityManager em) {
    FillingEdgeJobs fillingEdgeJobs = new FillingEdgeJobs()
      .name(UPDATED_NAME)
      .pipelineId(UPDATED_PIPELINE_ID)
      .title(UPDATED_TITLE)
      .uuid(UPDATED_UUID)
      .valid(UPDATED_VALID)
      .metadata(UPDATED_METADATA)
      .ctlVersion(UPDATED_CTL_VERSION)
      .ctlId(UPDATED_CTL_ID)
      .uiInfo(UPDATED_UI_INFO)
      .info(UPDATED_INFO)
      .jobText(UPDATED_JOB_TEXT)
      .status(UPDATED_STATUS)
      .description(UPDATED_DESCRIPTION)
      .created(UPDATED_CREATED)
      .lastModified(UPDATED_LAST_MODIFIED)
      .creator(UPDATED_CREATOR)
      .lastModifier(UPDATED_LAST_MODIFIER);
    return fillingEdgeJobs;
  }

  @BeforeEach
  public void initTest() {
    fillingEdgeJobs = createEntity(em);
  }

  @Test
  @Transactional
  void createFillingEdgeJobs() throws Exception {
    int databaseSizeBeforeCreate = fillingEdgeJobsRepository.findAll().size();
    // Create the FillingEdgeJobs
    restFillingEdgeJobsMockMvc
      .perform(
        post(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeJobs))
      )
      .andExpect(status().isCreated());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeCreate + 1);
    FillingEdgeJobs testFillingEdgeJobs = fillingEdgeJobsList.get(
      fillingEdgeJobsList.size() - 1
    );
    assertThat(testFillingEdgeJobs.getName()).isEqualTo(DEFAULT_NAME);
    assertThat(testFillingEdgeJobs.getPipelineId())
      .isEqualTo(DEFAULT_PIPELINE_ID);
    assertThat(testFillingEdgeJobs.getTitle()).isEqualTo(DEFAULT_TITLE);
    assertThat(testFillingEdgeJobs.getUuid()).isEqualTo(DEFAULT_UUID);
    assertThat(testFillingEdgeJobs.getValid()).isEqualTo(DEFAULT_VALID);
    assertThat(testFillingEdgeJobs.getMetadata()).isEqualTo(DEFAULT_METADATA);
    assertThat(testFillingEdgeJobs.getCtlVersion())
      .isEqualTo(DEFAULT_CTL_VERSION);
    assertThat(testFillingEdgeJobs.getCtlId()).isEqualTo(DEFAULT_CTL_ID);
    assertThat(testFillingEdgeJobs.getUiInfo()).isEqualTo(DEFAULT_UI_INFO);
    assertThat(testFillingEdgeJobs.getInfo()).isEqualTo(DEFAULT_INFO);
    assertThat(testFillingEdgeJobs.getJobText()).isEqualTo(DEFAULT_JOB_TEXT);
    assertThat(testFillingEdgeJobs.getStatus()).isEqualTo(DEFAULT_STATUS);
    assertThat(testFillingEdgeJobs.getDescription())
      .isEqualTo(DEFAULT_DESCRIPTION);
    assertThat(testFillingEdgeJobs.getCreated()).isEqualTo(DEFAULT_CREATED);
    assertThat(testFillingEdgeJobs.getLastModified())
      .isEqualTo(DEFAULT_LAST_MODIFIED);
    assertThat(testFillingEdgeJobs.getCreator()).isEqualTo(DEFAULT_CREATOR);
    assertThat(testFillingEdgeJobs.getLastModifier())
      .isEqualTo(DEFAULT_LAST_MODIFIER);
  }

  @Test
  @Transactional
  void createFillingEdgeJobsWithExistingId() throws Exception {
    // Create the FillingEdgeJobs with an existing ID
    fillingEdgeJobs.setId(1L);

    int databaseSizeBeforeCreate = fillingEdgeJobsRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must fail
    restFillingEdgeJobsMockMvc
      .perform(
        post(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeJobs))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void getAllFillingEdgeJobs() throws Exception {
    // Initialize the database
    fillingEdgeJobsRepository.saveAndFlush(fillingEdgeJobs);

    // Get all the fillingEdgeJobsList
    restFillingEdgeJobsMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(
        jsonPath("$.[*].id").value(hasItem(fillingEdgeJobs.getId().intValue()))
      )
      .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
      .andExpect(
        jsonPath("$.[*].pipelineId").value(hasItem(DEFAULT_PIPELINE_ID))
      )
      .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
      .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID)))
      .andExpect(
        jsonPath("$.[*].valid").value(hasItem(DEFAULT_VALID.booleanValue()))
      )
      .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)))
      .andExpect(
        jsonPath("$.[*].ctlVersion").value(hasItem(DEFAULT_CTL_VERSION))
      )
      .andExpect(jsonPath("$.[*].ctlId").value(hasItem(DEFAULT_CTL_ID)))
      .andExpect(jsonPath("$.[*].uiInfo").value(hasItem(DEFAULT_UI_INFO)))
      .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO)))
      .andExpect(jsonPath("$.[*].jobText").value(hasItem(DEFAULT_JOB_TEXT)))
      .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
      .andExpect(
        jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION))
      )
      .andExpect(
        jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString()))
      )
      .andExpect(
        jsonPath("$.[*].lastModified")
          .value(hasItem(DEFAULT_LAST_MODIFIED.toString()))
      )
      .andExpect(jsonPath("$.[*].creator").value(hasItem(DEFAULT_CREATOR)))
      .andExpect(
        jsonPath("$.[*].lastModifier").value(hasItem(DEFAULT_LAST_MODIFIER))
      );
  }

  @Test
  @Transactional
  void getFillingEdgeJobs() throws Exception {
    // Initialize the database
    fillingEdgeJobsRepository.saveAndFlush(fillingEdgeJobs);

    // Get the fillingEdgeJobs
    restFillingEdgeJobsMockMvc
      .perform(get(ENTITY_API_URL_ID, fillingEdgeJobs.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(fillingEdgeJobs.getId().intValue()))
      .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
      .andExpect(jsonPath("$.pipelineId").value(DEFAULT_PIPELINE_ID))
      .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
      .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID))
      .andExpect(jsonPath("$.valid").value(DEFAULT_VALID.booleanValue()))
      .andExpect(jsonPath("$.metadata").value(DEFAULT_METADATA))
      .andExpect(jsonPath("$.ctlVersion").value(DEFAULT_CTL_VERSION))
      .andExpect(jsonPath("$.ctlId").value(DEFAULT_CTL_ID))
      .andExpect(jsonPath("$.uiInfo").value(DEFAULT_UI_INFO))
      .andExpect(jsonPath("$.info").value(DEFAULT_INFO))
      .andExpect(jsonPath("$.jobText").value(DEFAULT_JOB_TEXT))
      .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
      .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
      .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
      .andExpect(
        jsonPath("$.lastModified").value(DEFAULT_LAST_MODIFIED.toString())
      )
      .andExpect(jsonPath("$.creator").value(DEFAULT_CREATOR))
      .andExpect(jsonPath("$.lastModifier").value(DEFAULT_LAST_MODIFIER));
  }

  @Test
  @Transactional
  void getNonExistingFillingEdgeJobs() throws Exception {
    // Get the fillingEdgeJobs
    restFillingEdgeJobsMockMvc
      .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
      .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putNewFillingEdgeJobs() throws Exception {
    // Initialize the database
    fillingEdgeJobsRepository.saveAndFlush(fillingEdgeJobs);

    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();

    // Update the fillingEdgeJobs
    FillingEdgeJobs updatedFillingEdgeJobs = fillingEdgeJobsRepository
      .findById(fillingEdgeJobs.getId())
      .get();
    // Disconnect from session so that the updates on updatedFillingEdgeJobs are not directly saved in db
    em.detach(updatedFillingEdgeJobs);
    updatedFillingEdgeJobs
      .name(UPDATED_NAME)
      .pipelineId(UPDATED_PIPELINE_ID)
      .title(UPDATED_TITLE)
      .uuid(UPDATED_UUID)
      .valid(UPDATED_VALID)
      .metadata(UPDATED_METADATA)
      .ctlVersion(UPDATED_CTL_VERSION)
      .ctlId(UPDATED_CTL_ID)
      .uiInfo(UPDATED_UI_INFO)
      .info(UPDATED_INFO)
      .jobText(UPDATED_JOB_TEXT)
      .status(UPDATED_STATUS)
      .description(UPDATED_DESCRIPTION)
      .created(UPDATED_CREATED)
      .lastModified(UPDATED_LAST_MODIFIED)
      .creator(UPDATED_CREATOR)
      .lastModifier(UPDATED_LAST_MODIFIER);

    restFillingEdgeJobsMockMvc
      .perform(
        put(ENTITY_API_URL_ID, updatedFillingEdgeJobs.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(updatedFillingEdgeJobs))
      )
      .andExpect(status().isOk());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
    FillingEdgeJobs testFillingEdgeJobs = fillingEdgeJobsList.get(
      fillingEdgeJobsList.size() - 1
    );
    assertThat(testFillingEdgeJobs.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testFillingEdgeJobs.getPipelineId())
      .isEqualTo(UPDATED_PIPELINE_ID);
    assertThat(testFillingEdgeJobs.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testFillingEdgeJobs.getUuid()).isEqualTo(UPDATED_UUID);
    assertThat(testFillingEdgeJobs.getValid()).isEqualTo(UPDATED_VALID);
    assertThat(testFillingEdgeJobs.getMetadata()).isEqualTo(UPDATED_METADATA);
    assertThat(testFillingEdgeJobs.getCtlVersion())
      .isEqualTo(UPDATED_CTL_VERSION);
    assertThat(testFillingEdgeJobs.getCtlId()).isEqualTo(UPDATED_CTL_ID);
    assertThat(testFillingEdgeJobs.getUiInfo()).isEqualTo(UPDATED_UI_INFO);
    assertThat(testFillingEdgeJobs.getInfo()).isEqualTo(UPDATED_INFO);
    assertThat(testFillingEdgeJobs.getJobText()).isEqualTo(UPDATED_JOB_TEXT);
    assertThat(testFillingEdgeJobs.getStatus()).isEqualTo(UPDATED_STATUS);
    assertThat(testFillingEdgeJobs.getDescription())
      .isEqualTo(UPDATED_DESCRIPTION);
    assertThat(testFillingEdgeJobs.getCreated()).isEqualTo(UPDATED_CREATED);
    assertThat(testFillingEdgeJobs.getLastModified())
      .isEqualTo(UPDATED_LAST_MODIFIED);
    assertThat(testFillingEdgeJobs.getCreator()).isEqualTo(UPDATED_CREATOR);
    assertThat(testFillingEdgeJobs.getLastModifier())
      .isEqualTo(UPDATED_LAST_MODIFIER);
  }

  @Test
  @Transactional
  void putNonExistingFillingEdgeJobs() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();
    fillingEdgeJobs.setId(count.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restFillingEdgeJobsMockMvc
      .perform(
        put(ENTITY_API_URL_ID, fillingEdgeJobs.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeJobs))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchFillingEdgeJobs() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();
    fillingEdgeJobs.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFillingEdgeJobsMockMvc
      .perform(
        put(ENTITY_API_URL_ID, count.incrementAndGet())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeJobs))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamFillingEdgeJobs() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();
    fillingEdgeJobs.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFillingEdgeJobsMockMvc
      .perform(
        put(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeJobs))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdateFillingEdgeJobsWithPatch() throws Exception {
    // Initialize the database
    fillingEdgeJobsRepository.saveAndFlush(fillingEdgeJobs);

    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();

    // Update the fillingEdgeJobs using partial update
    FillingEdgeJobs partialUpdatedFillingEdgeJobs = new FillingEdgeJobs();
    partialUpdatedFillingEdgeJobs.setId(fillingEdgeJobs.getId());

    partialUpdatedFillingEdgeJobs
      .name(UPDATED_NAME)
      .pipelineId(UPDATED_PIPELINE_ID)
      .title(UPDATED_TITLE)
      .metadata(UPDATED_METADATA)
      .uiInfo(UPDATED_UI_INFO)
      .info(UPDATED_INFO)
      .jobText(UPDATED_JOB_TEXT)
      .status(UPDATED_STATUS)
      .lastModified(UPDATED_LAST_MODIFIED)
      .creator(UPDATED_CREATOR)
      .lastModifier(UPDATED_LAST_MODIFIER);

    restFillingEdgeJobsMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedFillingEdgeJobs.getId())
          .contentType("application/merge-patch+json")
          .content(
            TestUtil.convertObjectToJsonBytes(partialUpdatedFillingEdgeJobs)
          )
      )
      .andExpect(status().isOk());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
    FillingEdgeJobs testFillingEdgeJobs = fillingEdgeJobsList.get(
      fillingEdgeJobsList.size() - 1
    );
    assertThat(testFillingEdgeJobs.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testFillingEdgeJobs.getPipelineId())
      .isEqualTo(UPDATED_PIPELINE_ID);
    assertThat(testFillingEdgeJobs.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testFillingEdgeJobs.getUuid()).isEqualTo(DEFAULT_UUID);
    assertThat(testFillingEdgeJobs.getValid()).isEqualTo(DEFAULT_VALID);
    assertThat(testFillingEdgeJobs.getMetadata()).isEqualTo(UPDATED_METADATA);
    assertThat(testFillingEdgeJobs.getCtlVersion())
      .isEqualTo(DEFAULT_CTL_VERSION);
    assertThat(testFillingEdgeJobs.getCtlId()).isEqualTo(DEFAULT_CTL_ID);
    assertThat(testFillingEdgeJobs.getUiInfo()).isEqualTo(UPDATED_UI_INFO);
    assertThat(testFillingEdgeJobs.getInfo()).isEqualTo(UPDATED_INFO);
    assertThat(testFillingEdgeJobs.getJobText()).isEqualTo(UPDATED_JOB_TEXT);
    assertThat(testFillingEdgeJobs.getStatus()).isEqualTo(UPDATED_STATUS);
    assertThat(testFillingEdgeJobs.getDescription())
      .isEqualTo(DEFAULT_DESCRIPTION);
    assertThat(testFillingEdgeJobs.getCreated()).isEqualTo(DEFAULT_CREATED);
    assertThat(testFillingEdgeJobs.getLastModified())
      .isEqualTo(UPDATED_LAST_MODIFIED);
    assertThat(testFillingEdgeJobs.getCreator()).isEqualTo(UPDATED_CREATOR);
    assertThat(testFillingEdgeJobs.getLastModifier())
      .isEqualTo(UPDATED_LAST_MODIFIER);
  }

  @Test
  @Transactional
  void fullUpdateFillingEdgeJobsWithPatch() throws Exception {
    // Initialize the database
    fillingEdgeJobsRepository.saveAndFlush(fillingEdgeJobs);

    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();

    // Update the fillingEdgeJobs using partial update
    FillingEdgeJobs partialUpdatedFillingEdgeJobs = new FillingEdgeJobs();
    partialUpdatedFillingEdgeJobs.setId(fillingEdgeJobs.getId());

    partialUpdatedFillingEdgeJobs
      .name(UPDATED_NAME)
      .pipelineId(UPDATED_PIPELINE_ID)
      .title(UPDATED_TITLE)
      .uuid(UPDATED_UUID)
      .valid(UPDATED_VALID)
      .metadata(UPDATED_METADATA)
      .ctlVersion(UPDATED_CTL_VERSION)
      .ctlId(UPDATED_CTL_ID)
      .uiInfo(UPDATED_UI_INFO)
      .info(UPDATED_INFO)
      .jobText(UPDATED_JOB_TEXT)
      .status(UPDATED_STATUS)
      .description(UPDATED_DESCRIPTION)
      .created(UPDATED_CREATED)
      .lastModified(UPDATED_LAST_MODIFIED)
      .creator(UPDATED_CREATOR)
      .lastModifier(UPDATED_LAST_MODIFIER);

    restFillingEdgeJobsMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedFillingEdgeJobs.getId())
          .contentType("application/merge-patch+json")
          .content(
            TestUtil.convertObjectToJsonBytes(partialUpdatedFillingEdgeJobs)
          )
      )
      .andExpect(status().isOk());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
    FillingEdgeJobs testFillingEdgeJobs = fillingEdgeJobsList.get(
      fillingEdgeJobsList.size() - 1
    );
    assertThat(testFillingEdgeJobs.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testFillingEdgeJobs.getPipelineId())
      .isEqualTo(UPDATED_PIPELINE_ID);
    assertThat(testFillingEdgeJobs.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testFillingEdgeJobs.getUuid()).isEqualTo(UPDATED_UUID);
    assertThat(testFillingEdgeJobs.getValid()).isEqualTo(UPDATED_VALID);
    assertThat(testFillingEdgeJobs.getMetadata()).isEqualTo(UPDATED_METADATA);
    assertThat(testFillingEdgeJobs.getCtlVersion())
      .isEqualTo(UPDATED_CTL_VERSION);
    assertThat(testFillingEdgeJobs.getCtlId()).isEqualTo(UPDATED_CTL_ID);
    assertThat(testFillingEdgeJobs.getUiInfo()).isEqualTo(UPDATED_UI_INFO);
    assertThat(testFillingEdgeJobs.getInfo()).isEqualTo(UPDATED_INFO);
    assertThat(testFillingEdgeJobs.getJobText()).isEqualTo(UPDATED_JOB_TEXT);
    assertThat(testFillingEdgeJobs.getStatus()).isEqualTo(UPDATED_STATUS);
    assertThat(testFillingEdgeJobs.getDescription())
      .isEqualTo(UPDATED_DESCRIPTION);
    assertThat(testFillingEdgeJobs.getCreated()).isEqualTo(UPDATED_CREATED);
    assertThat(testFillingEdgeJobs.getLastModified())
      .isEqualTo(UPDATED_LAST_MODIFIED);
    assertThat(testFillingEdgeJobs.getCreator()).isEqualTo(UPDATED_CREATOR);
    assertThat(testFillingEdgeJobs.getLastModifier())
      .isEqualTo(UPDATED_LAST_MODIFIER);
  }

  @Test
  @Transactional
  void patchNonExistingFillingEdgeJobs() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();
    fillingEdgeJobs.setId(count.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restFillingEdgeJobsMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, fillingEdgeJobs.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeJobs))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchFillingEdgeJobs() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();
    fillingEdgeJobs.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFillingEdgeJobsMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, count.incrementAndGet())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeJobs))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamFillingEdgeJobs() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeJobsRepository.findAll().size();
    fillingEdgeJobs.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFillingEdgeJobsMockMvc
      .perform(
        patch(ENTITY_API_URL)
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeJobs))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the FillingEdgeJobs in the database
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deleteFillingEdgeJobs() throws Exception {
    // Initialize the database
    fillingEdgeJobsRepository.saveAndFlush(fillingEdgeJobs);

    int databaseSizeBeforeDelete = fillingEdgeJobsRepository.findAll().size();

    // Delete the fillingEdgeJobs
    restFillingEdgeJobsMockMvc
      .perform(
        delete(ENTITY_API_URL_ID, fillingEdgeJobs.getId())
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<FillingEdgeJobs> fillingEdgeJobsList = fillingEdgeJobsRepository.findAll();
    assertThat(fillingEdgeJobsList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
