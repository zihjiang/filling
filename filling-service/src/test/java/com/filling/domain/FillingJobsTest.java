package com.filling.domain;

import com.filling.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FillingJobsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FillingJobs.class);
        FillingJobs fillingJobs1 = new FillingJobs();
        fillingJobs1.setId(1L);
        FillingJobs fillingJobs2 = new FillingJobs();
        fillingJobs2.setId(fillingJobs1.getId());
        assertThat(fillingJobs1).isEqualTo(fillingJobs2);
        fillingJobs2.setId(2L);
        assertThat(fillingJobs1).isNotEqualTo(fillingJobs2);
        fillingJobs1.setId(null);
        assertThat(fillingJobs1).isNotEqualTo(fillingJobs2);
    }
}
