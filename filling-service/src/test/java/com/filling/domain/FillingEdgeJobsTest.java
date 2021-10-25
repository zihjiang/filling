package com.filling.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.filling.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FillingEdgeJobsTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(FillingEdgeJobs.class);
    FillingEdgeJobs fillingEdgeJobs1 = new FillingEdgeJobs();
    fillingEdgeJobs1.setId(1L);
    FillingEdgeJobs fillingEdgeJobs2 = new FillingEdgeJobs();
    fillingEdgeJobs2.setId(fillingEdgeJobs1.getId());
    assertThat(fillingEdgeJobs1).isEqualTo(fillingEdgeJobs2);
    fillingEdgeJobs2.setId(2L);
    assertThat(fillingEdgeJobs1).isNotEqualTo(fillingEdgeJobs2);
    fillingEdgeJobs1.setId(null);
    assertThat(fillingEdgeJobs1).isNotEqualTo(fillingEdgeJobs2);
  }
}
