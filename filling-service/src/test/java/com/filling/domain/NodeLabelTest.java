package com.filling.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.filling.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NodeLabelTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(NodeLabel.class);
    NodeLabel nodeLabel1 = new NodeLabel();
    nodeLabel1.setId(1L);
    NodeLabel nodeLabel2 = new NodeLabel();
    nodeLabel2.setId(nodeLabel1.getId());
    assertThat(nodeLabel1).isEqualTo(nodeLabel2);
    nodeLabel2.setId(2L);
    assertThat(nodeLabel1).isNotEqualTo(nodeLabel2);
    nodeLabel1.setId(null);
    assertThat(nodeLabel1).isNotEqualTo(nodeLabel2);
  }
}
