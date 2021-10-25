package com.filling.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.filling.IntegrationTest;
import com.filling.domain.FillingEdgeNodes;
import com.filling.repository.FillingEdgeNodesRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FillingEdgeNodesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FillingEdgeNodesResourceIT {

  private static final String DEFAULT_NAME = "AAAAAAAAAA";
  private static final String UPDATED_NAME = "BBBBBBBBBB";

  private static final String DEFAULT_TITLE = "AAAAAAAAAA";
  private static final String UPDATED_TITLE = "BBBBBBBBBB";

  private static final String DEFAULT_UUID = "AAAAAAAAAA";
  private static final String UPDATED_UUID = "BBBBBBBBBB";

  private static final Boolean DEFAULT_VALID = false;
  private static final Boolean UPDATED_VALID = true;

  private static final String DEFAULT_METADATA = "AAAAAAAAAA";
  private static final String UPDATED_METADATA = "BBBBBBBBBB";

  private static final String DEFAULT_HOST = "AAAAAAAAAA";
  private static final String UPDATED_HOST = "BBBBBBBBBB";

  private static final String DEFAULT_URL = "AAAAAAAAAA";
  private static final String UPDATED_URL = "BBBBBBBBBB";

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

  private static final String ENTITY_API_URL = "/api/filling-edge-nodes";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong count = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );

  @Autowired
  private FillingEdgeNodesRepository fillingEdgeNodesRepository;

  @Autowired
  private EntityManager em;

  @Autowired
  private MockMvc restFillingEdgeNodesMockMvc;

  private FillingEdgeNodes fillingEdgeNodes;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static FillingEdgeNodes createEntity(EntityManager em) {
    FillingEdgeNodes fillingEdgeNodes = new FillingEdgeNodes()
      .name(DEFAULT_NAME)
      .title(DEFAULT_TITLE)
      .uuid(DEFAULT_UUID)
      .valid(DEFAULT_VALID)
      .metadata(DEFAULT_METADATA)
      .host(DEFAULT_HOST)
      .url(DEFAULT_URL)
      .description(DEFAULT_DESCRIPTION)
      .created(DEFAULT_CREATED)
      .lastModified(DEFAULT_LAST_MODIFIED)
      .creator(DEFAULT_CREATOR)
      .lastModifier(DEFAULT_LAST_MODIFIER);
    return fillingEdgeNodes;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static FillingEdgeNodes createUpdatedEntity(EntityManager em) {
    FillingEdgeNodes fillingEdgeNodes = new FillingEdgeNodes()
      .name(UPDATED_NAME)
      .title(UPDATED_TITLE)
      .uuid(UPDATED_UUID)
      .valid(UPDATED_VALID)
      .metadata(UPDATED_METADATA)
      .host(UPDATED_HOST)
      .url(UPDATED_URL)
      .description(UPDATED_DESCRIPTION)
      .created(UPDATED_CREATED)
      .lastModified(UPDATED_LAST_MODIFIED)
      .creator(UPDATED_CREATOR)
      .lastModifier(UPDATED_LAST_MODIFIER);
    return fillingEdgeNodes;
  }

  @BeforeEach
  public void initTest() {
    fillingEdgeNodes = createEntity(em);
  }

  @Test
  @Transactional
  void createFillingEdgeNodes() throws Exception {
    int databaseSizeBeforeCreate = fillingEdgeNodesRepository.findAll().size();
    // Create the FillingEdgeNodes
    restFillingEdgeNodesMockMvc
      .perform(
        post(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeNodes))
      )
      .andExpect(status().isCreated());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeCreate + 1);
    FillingEdgeNodes testFillingEdgeNodes = fillingEdgeNodesList.get(
      fillingEdgeNodesList.size() - 1
    );
    assertThat(testFillingEdgeNodes.getName()).isEqualTo(DEFAULT_NAME);
    assertThat(testFillingEdgeNodes.getTitle()).isEqualTo(DEFAULT_TITLE);
    assertThat(testFillingEdgeNodes.getUuid()).isEqualTo(DEFAULT_UUID);
    assertThat(testFillingEdgeNodes.getValid()).isEqualTo(DEFAULT_VALID);
    assertThat(testFillingEdgeNodes.getMetadata()).isEqualTo(DEFAULT_METADATA);
    assertThat(testFillingEdgeNodes.getHost()).isEqualTo(DEFAULT_HOST);
    assertThat(testFillingEdgeNodes.getUrl()).isEqualTo(DEFAULT_URL);
    assertThat(testFillingEdgeNodes.getDescription())
      .isEqualTo(DEFAULT_DESCRIPTION);
    assertThat(testFillingEdgeNodes.getCreated()).isEqualTo(DEFAULT_CREATED);
    assertThat(testFillingEdgeNodes.getLastModified())
      .isEqualTo(DEFAULT_LAST_MODIFIED);
    assertThat(testFillingEdgeNodes.getCreator()).isEqualTo(DEFAULT_CREATOR);
    assertThat(testFillingEdgeNodes.getLastModifier())
      .isEqualTo(DEFAULT_LAST_MODIFIER);
  }

  @Test
  @Transactional
  void createFillingEdgeNodesWithExistingId() throws Exception {
    // Create the FillingEdgeNodes with an existing ID
    fillingEdgeNodes.setId(1L);

    int databaseSizeBeforeCreate = fillingEdgeNodesRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must fail
    restFillingEdgeNodesMockMvc
      .perform(
        post(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeNodes))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void getAllFillingEdgeNodes() throws Exception {
    // Initialize the database
    fillingEdgeNodesRepository.saveAndFlush(fillingEdgeNodes);

    // Get all the fillingEdgeNodesList
    restFillingEdgeNodesMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(
        jsonPath("$.[*].id").value(hasItem(fillingEdgeNodes.getId().intValue()))
      )
      .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
      .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
      .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID)))
      .andExpect(
        jsonPath("$.[*].valid").value(hasItem(DEFAULT_VALID.booleanValue()))
      )
      .andExpect(jsonPath("$.[*].metadata").value(hasItem(DEFAULT_METADATA)))
      .andExpect(jsonPath("$.[*].host").value(hasItem(DEFAULT_HOST)))
      .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
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
  void getFillingEdgeNodes() throws Exception {
    // Initialize the database
    fillingEdgeNodesRepository.saveAndFlush(fillingEdgeNodes);

    // Get the fillingEdgeNodes
    restFillingEdgeNodesMockMvc
      .perform(get(ENTITY_API_URL_ID, fillingEdgeNodes.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(fillingEdgeNodes.getId().intValue()))
      .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
      .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
      .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID))
      .andExpect(jsonPath("$.valid").value(DEFAULT_VALID.booleanValue()))
      .andExpect(jsonPath("$.metadata").value(DEFAULT_METADATA))
      .andExpect(jsonPath("$.host").value(DEFAULT_HOST))
      .andExpect(jsonPath("$.url").value(DEFAULT_URL))
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
  void getNonExistingFillingEdgeNodes() throws Exception {
    // Get the fillingEdgeNodes
    restFillingEdgeNodesMockMvc
      .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
      .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putNewFillingEdgeNodes() throws Exception {
    // Initialize the database
    fillingEdgeNodesRepository.saveAndFlush(fillingEdgeNodes);

    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();

    // Update the fillingEdgeNodes
    FillingEdgeNodes updatedFillingEdgeNodes = fillingEdgeNodesRepository
      .findById(fillingEdgeNodes.getId())
      .get();
    // Disconnect from session so that the updates on updatedFillingEdgeNodes are not directly saved in db
    em.detach(updatedFillingEdgeNodes);
    updatedFillingEdgeNodes
      .name(UPDATED_NAME)
      .title(UPDATED_TITLE)
      .uuid(UPDATED_UUID)
      .valid(UPDATED_VALID)
      .metadata(UPDATED_METADATA)
      .host(UPDATED_HOST)
      .url(UPDATED_URL)
      .description(UPDATED_DESCRIPTION)
      .created(UPDATED_CREATED)
      .lastModified(UPDATED_LAST_MODIFIED)
      .creator(UPDATED_CREATOR)
      .lastModifier(UPDATED_LAST_MODIFIER);

    restFillingEdgeNodesMockMvc
      .perform(
        put(ENTITY_API_URL_ID, updatedFillingEdgeNodes.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(updatedFillingEdgeNodes))
      )
      .andExpect(status().isOk());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
    FillingEdgeNodes testFillingEdgeNodes = fillingEdgeNodesList.get(
      fillingEdgeNodesList.size() - 1
    );
    assertThat(testFillingEdgeNodes.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testFillingEdgeNodes.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testFillingEdgeNodes.getUuid()).isEqualTo(UPDATED_UUID);
    assertThat(testFillingEdgeNodes.getValid()).isEqualTo(UPDATED_VALID);
    assertThat(testFillingEdgeNodes.getMetadata()).isEqualTo(UPDATED_METADATA);
    assertThat(testFillingEdgeNodes.getHost()).isEqualTo(UPDATED_HOST);
    assertThat(testFillingEdgeNodes.getUrl()).isEqualTo(UPDATED_URL);
    assertThat(testFillingEdgeNodes.getDescription())
      .isEqualTo(UPDATED_DESCRIPTION);
    assertThat(testFillingEdgeNodes.getCreated()).isEqualTo(UPDATED_CREATED);
    assertThat(testFillingEdgeNodes.getLastModified())
      .isEqualTo(UPDATED_LAST_MODIFIED);
    assertThat(testFillingEdgeNodes.getCreator()).isEqualTo(UPDATED_CREATOR);
    assertThat(testFillingEdgeNodes.getLastModifier())
      .isEqualTo(UPDATED_LAST_MODIFIER);
  }

  @Test
  @Transactional
  void putNonExistingFillingEdgeNodes() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();
    fillingEdgeNodes.setId(count.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restFillingEdgeNodesMockMvc
      .perform(
        put(ENTITY_API_URL_ID, fillingEdgeNodes.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeNodes))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchFillingEdgeNodes() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();
    fillingEdgeNodes.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFillingEdgeNodesMockMvc
      .perform(
        put(ENTITY_API_URL_ID, count.incrementAndGet())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeNodes))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamFillingEdgeNodes() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();
    fillingEdgeNodes.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFillingEdgeNodesMockMvc
      .perform(
        put(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeNodes))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdateFillingEdgeNodesWithPatch() throws Exception {
    // Initialize the database
    fillingEdgeNodesRepository.saveAndFlush(fillingEdgeNodes);

    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();

    // Update the fillingEdgeNodes using partial update
    FillingEdgeNodes partialUpdatedFillingEdgeNodes = new FillingEdgeNodes();
    partialUpdatedFillingEdgeNodes.setId(fillingEdgeNodes.getId());

    partialUpdatedFillingEdgeNodes
      .title(UPDATED_TITLE)
      .valid(UPDATED_VALID)
      .url(UPDATED_URL)
      .description(UPDATED_DESCRIPTION)
      .creator(UPDATED_CREATOR);

    restFillingEdgeNodesMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedFillingEdgeNodes.getId())
          .contentType("application/merge-patch+json")
          .content(
            TestUtil.convertObjectToJsonBytes(partialUpdatedFillingEdgeNodes)
          )
      )
      .andExpect(status().isOk());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
    FillingEdgeNodes testFillingEdgeNodes = fillingEdgeNodesList.get(
      fillingEdgeNodesList.size() - 1
    );
    assertThat(testFillingEdgeNodes.getName()).isEqualTo(DEFAULT_NAME);
    assertThat(testFillingEdgeNodes.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testFillingEdgeNodes.getUuid()).isEqualTo(DEFAULT_UUID);
    assertThat(testFillingEdgeNodes.getValid()).isEqualTo(UPDATED_VALID);
    assertThat(testFillingEdgeNodes.getMetadata()).isEqualTo(DEFAULT_METADATA);
    assertThat(testFillingEdgeNodes.getHost()).isEqualTo(DEFAULT_HOST);
    assertThat(testFillingEdgeNodes.getUrl()).isEqualTo(UPDATED_URL);
    assertThat(testFillingEdgeNodes.getDescription())
      .isEqualTo(UPDATED_DESCRIPTION);
    assertThat(testFillingEdgeNodes.getCreated()).isEqualTo(DEFAULT_CREATED);
    assertThat(testFillingEdgeNodes.getLastModified())
      .isEqualTo(DEFAULT_LAST_MODIFIED);
    assertThat(testFillingEdgeNodes.getCreator()).isEqualTo(UPDATED_CREATOR);
    assertThat(testFillingEdgeNodes.getLastModifier())
      .isEqualTo(DEFAULT_LAST_MODIFIER);
  }

  @Test
  @Transactional
  void fullUpdateFillingEdgeNodesWithPatch() throws Exception {
    // Initialize the database
    fillingEdgeNodesRepository.saveAndFlush(fillingEdgeNodes);

    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();

    // Update the fillingEdgeNodes using partial update
    FillingEdgeNodes partialUpdatedFillingEdgeNodes = new FillingEdgeNodes();
    partialUpdatedFillingEdgeNodes.setId(fillingEdgeNodes.getId());

    partialUpdatedFillingEdgeNodes
      .name(UPDATED_NAME)
      .title(UPDATED_TITLE)
      .uuid(UPDATED_UUID)
      .valid(UPDATED_VALID)
      .metadata(UPDATED_METADATA)
      .host(UPDATED_HOST)
      .url(UPDATED_URL)
      .description(UPDATED_DESCRIPTION)
      .created(UPDATED_CREATED)
      .lastModified(UPDATED_LAST_MODIFIED)
      .creator(UPDATED_CREATOR)
      .lastModifier(UPDATED_LAST_MODIFIER);

    restFillingEdgeNodesMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedFillingEdgeNodes.getId())
          .contentType("application/merge-patch+json")
          .content(
            TestUtil.convertObjectToJsonBytes(partialUpdatedFillingEdgeNodes)
          )
      )
      .andExpect(status().isOk());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
    FillingEdgeNodes testFillingEdgeNodes = fillingEdgeNodesList.get(
      fillingEdgeNodesList.size() - 1
    );
    assertThat(testFillingEdgeNodes.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testFillingEdgeNodes.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testFillingEdgeNodes.getUuid()).isEqualTo(UPDATED_UUID);
    assertThat(testFillingEdgeNodes.getValid()).isEqualTo(UPDATED_VALID);
    assertThat(testFillingEdgeNodes.getMetadata()).isEqualTo(UPDATED_METADATA);
    assertThat(testFillingEdgeNodes.getHost()).isEqualTo(UPDATED_HOST);
    assertThat(testFillingEdgeNodes.getUrl()).isEqualTo(UPDATED_URL);
    assertThat(testFillingEdgeNodes.getDescription())
      .isEqualTo(UPDATED_DESCRIPTION);
    assertThat(testFillingEdgeNodes.getCreated()).isEqualTo(UPDATED_CREATED);
    assertThat(testFillingEdgeNodes.getLastModified())
      .isEqualTo(UPDATED_LAST_MODIFIED);
    assertThat(testFillingEdgeNodes.getCreator()).isEqualTo(UPDATED_CREATOR);
    assertThat(testFillingEdgeNodes.getLastModifier())
      .isEqualTo(UPDATED_LAST_MODIFIER);
  }

  @Test
  @Transactional
  void patchNonExistingFillingEdgeNodes() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();
    fillingEdgeNodes.setId(count.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restFillingEdgeNodesMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, fillingEdgeNodes.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeNodes))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchFillingEdgeNodes() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();
    fillingEdgeNodes.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFillingEdgeNodesMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, count.incrementAndGet())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeNodes))
      )
      .andExpect(status().isBadRequest());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamFillingEdgeNodes() throws Exception {
    int databaseSizeBeforeUpdate = fillingEdgeNodesRepository.findAll().size();
    fillingEdgeNodes.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restFillingEdgeNodesMockMvc
      .perform(
        patch(ENTITY_API_URL)
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(fillingEdgeNodes))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the FillingEdgeNodes in the database
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deleteFillingEdgeNodes() throws Exception {
    // Initialize the database
    fillingEdgeNodesRepository.saveAndFlush(fillingEdgeNodes);

    int databaseSizeBeforeDelete = fillingEdgeNodesRepository.findAll().size();

    // Delete the fillingEdgeNodes
    restFillingEdgeNodesMockMvc
      .perform(
        delete(ENTITY_API_URL_ID, fillingEdgeNodes.getId())
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<FillingEdgeNodes> fillingEdgeNodesList = fillingEdgeNodesRepository.findAll();
    assertThat(fillingEdgeNodesList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
