package com.filling.web.rest;

import com.filling.IntegrationTest;
import com.filling.domain.NodeLabel;
import com.filling.repository.NodeLabelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link NodeLabelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NodeLabelResourceIT {

  private static final String DEFAULT_TITLE = "AAAAAAAAAA";
  private static final String UPDATED_TITLE = "BBBBBBBBBB";

  private static final String DEFAULT_COLOR = "AAAAAAAAAA";
  private static final String UPDATED_COLOR = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/node-labels";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong count = new AtomicLong(
    random.nextInt() + (2 * Integer.MAX_VALUE)
  );

  @Autowired
  private NodeLabelRepository nodeLabelRepository;

  @Autowired
  private EntityManager em;

  @Autowired
  private MockMvc restNodeLabelMockMvc;

  private NodeLabel nodeLabel;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static NodeLabel createEntity(EntityManager em) {
    NodeLabel nodeLabel = new NodeLabel()
      .title(DEFAULT_TITLE)
      .color(DEFAULT_COLOR);
    return nodeLabel;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static NodeLabel createUpdatedEntity(EntityManager em) {
    NodeLabel nodeLabel = new NodeLabel()
      .title(UPDATED_TITLE)
      .color(UPDATED_COLOR);
    return nodeLabel;
  }

  @BeforeEach
  public void initTest() {
    nodeLabel = createEntity(em);
  }

  @Test
  @Transactional
  void createNodeLabel() throws Exception {
    int databaseSizeBeforeCreate = nodeLabelRepository.findAll().size();
    // Create the NodeLabel
    restNodeLabelMockMvc
      .perform(
        post(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(nodeLabel))
      )
      .andExpect(status().isCreated());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeCreate + 1);
    NodeLabel testNodeLabel = nodeLabelList.get(nodeLabelList.size() - 1);
    assertThat(testNodeLabel.getTitle()).isEqualTo(DEFAULT_TITLE);
    assertThat(testNodeLabel.getColor()).isEqualTo(DEFAULT_COLOR);
  }

  @Test
  @Transactional
  void createNodeLabelWithExistingId() throws Exception {
    // Create the NodeLabel with an existing ID
    nodeLabel.setId(1L);

    int databaseSizeBeforeCreate = nodeLabelRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must fail
    restNodeLabelMockMvc
      .perform(
        post(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(nodeLabel))
      )
      .andExpect(status().isBadRequest());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void getAllNodeLabels() throws Exception {
    // Initialize the database
    nodeLabelRepository.saveAndFlush(nodeLabel);

    // Get all the nodeLabelList
    restNodeLabelMockMvc
      .perform(get(ENTITY_API_URL + "?sort=id,desc"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(
        jsonPath("$.[*].id").value(hasItem(nodeLabel.getId().intValue()))
      )
      .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
      .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)));
  }

  @Test
  @Transactional
  void getNodeLabel() throws Exception {
    // Initialize the database
    nodeLabelRepository.saveAndFlush(nodeLabel);

    // Get the nodeLabel
    restNodeLabelMockMvc
      .perform(get(ENTITY_API_URL_ID, nodeLabel.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").value(nodeLabel.getId().intValue()))
      .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
      .andExpect(jsonPath("$.color").value(DEFAULT_COLOR));
  }

  @Test
  @Transactional
  void getNonExistingNodeLabel() throws Exception {
    // Get the nodeLabel
    restNodeLabelMockMvc
      .perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
      .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putNewNodeLabel() throws Exception {
    // Initialize the database
    nodeLabelRepository.saveAndFlush(nodeLabel);

    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();

    // Update the nodeLabel
    NodeLabel updatedNodeLabel = nodeLabelRepository
      .findById(nodeLabel.getId())
      .get();
    // Disconnect from session so that the updates on updatedNodeLabel are not directly saved in db
    em.detach(updatedNodeLabel);
    updatedNodeLabel.title(UPDATED_TITLE).color(UPDATED_COLOR);

    restNodeLabelMockMvc
      .perform(
        put(ENTITY_API_URL_ID, updatedNodeLabel.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(updatedNodeLabel))
      )
      .andExpect(status().isOk());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
    NodeLabel testNodeLabel = nodeLabelList.get(nodeLabelList.size() - 1);
    assertThat(testNodeLabel.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testNodeLabel.getColor()).isEqualTo(UPDATED_COLOR);
  }

  @Test
  @Transactional
  void putNonExistingNodeLabel() throws Exception {
    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();
    nodeLabel.setId(count.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restNodeLabelMockMvc
      .perform(
        put(ENTITY_API_URL_ID, nodeLabel.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(nodeLabel))
      )
      .andExpect(status().isBadRequest());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchNodeLabel() throws Exception {
    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();
    nodeLabel.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restNodeLabelMockMvc
      .perform(
        put(ENTITY_API_URL_ID, count.incrementAndGet())
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(nodeLabel))
      )
      .andExpect(status().isBadRequest());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamNodeLabel() throws Exception {
    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();
    nodeLabel.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restNodeLabelMockMvc
      .perform(
        put(ENTITY_API_URL)
          .contentType(MediaType.APPLICATION_JSON)
          .content(TestUtil.convertObjectToJsonBytes(nodeLabel))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdateNodeLabelWithPatch() throws Exception {
    // Initialize the database
    nodeLabelRepository.saveAndFlush(nodeLabel);

    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();

    // Update the nodeLabel using partial update
    NodeLabel partialUpdatedNodeLabel = new NodeLabel();
    partialUpdatedNodeLabel.setId(nodeLabel.getId());

    partialUpdatedNodeLabel.color(UPDATED_COLOR);

    restNodeLabelMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedNodeLabel.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNodeLabel))
      )
      .andExpect(status().isOk());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
    NodeLabel testNodeLabel = nodeLabelList.get(nodeLabelList.size() - 1);
    assertThat(testNodeLabel.getTitle()).isEqualTo(DEFAULT_TITLE);
    assertThat(testNodeLabel.getColor()).isEqualTo(UPDATED_COLOR);
  }

  @Test
  @Transactional
  void fullUpdateNodeLabelWithPatch() throws Exception {
    // Initialize the database
    nodeLabelRepository.saveAndFlush(nodeLabel);

    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();

    // Update the nodeLabel using partial update
    NodeLabel partialUpdatedNodeLabel = new NodeLabel();
    partialUpdatedNodeLabel.setId(nodeLabel.getId());

    partialUpdatedNodeLabel.title(UPDATED_TITLE).color(UPDATED_COLOR);

    restNodeLabelMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, partialUpdatedNodeLabel.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNodeLabel))
      )
      .andExpect(status().isOk());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
    NodeLabel testNodeLabel = nodeLabelList.get(nodeLabelList.size() - 1);
    assertThat(testNodeLabel.getTitle()).isEqualTo(UPDATED_TITLE);
    assertThat(testNodeLabel.getColor()).isEqualTo(UPDATED_COLOR);
  }

  @Test
  @Transactional
  void patchNonExistingNodeLabel() throws Exception {
    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();
    nodeLabel.setId(count.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restNodeLabelMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, nodeLabel.getId())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(nodeLabel))
      )
      .andExpect(status().isBadRequest());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchNodeLabel() throws Exception {
    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();
    nodeLabel.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restNodeLabelMockMvc
      .perform(
        patch(ENTITY_API_URL_ID, count.incrementAndGet())
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(nodeLabel))
      )
      .andExpect(status().isBadRequest());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamNodeLabel() throws Exception {
    int databaseSizeBeforeUpdate = nodeLabelRepository.findAll().size();
    nodeLabel.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restNodeLabelMockMvc
      .perform(
        patch(ENTITY_API_URL)
          .contentType("application/merge-patch+json")
          .content(TestUtil.convertObjectToJsonBytes(nodeLabel))
      )
      .andExpect(status().isMethodNotAllowed());

    // Validate the NodeLabel in the database
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deleteNodeLabel() throws Exception {
    // Initialize the database
    nodeLabelRepository.saveAndFlush(nodeLabel);

    int databaseSizeBeforeDelete = nodeLabelRepository.findAll().size();

    // Delete the nodeLabel
    restNodeLabelMockMvc
      .perform(
        delete(ENTITY_API_URL_ID, nodeLabel.getId())
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<NodeLabel> nodeLabelList = nodeLabelRepository.findAll();
    assertThat(nodeLabelList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
