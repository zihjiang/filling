package com.filling.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.filling.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FillingEdgeNodesTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(FillingEdgeNodes.class);
    FillingEdgeNodes fillingEdgeNodes1 = new FillingEdgeNodes();
    fillingEdgeNodes1.setId(1L);
    FillingEdgeNodes fillingEdgeNodes2 = new FillingEdgeNodes();
    fillingEdgeNodes2.setId(fillingEdgeNodes1.getId());
    assertThat(fillingEdgeNodes1).isEqualTo(fillingEdgeNodes2);
    fillingEdgeNodes2.setId(2L);
    assertThat(fillingEdgeNodes1).isNotEqualTo(fillingEdgeNodes2);
    fillingEdgeNodes1.setId(null);
    assertThat(fillingEdgeNodes1).isNotEqualTo(fillingEdgeNodes2);
  }
}
