package com.filling.domain;

import com.filling.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FillingJobsHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FillingJobsHistory.class);
        FillingJobsHistory fillingJobsHistory1 = new FillingJobsHistory();
        fillingJobsHistory1.setId(1L);
        FillingJobsHistory fillingJobsHistory2 = new FillingJobsHistory();
        fillingJobsHistory2.setId(fillingJobsHistory1.getId());
        assertThat(fillingJobsHistory1).isEqualTo(fillingJobsHistory2);
        fillingJobsHistory2.setId(2L);
        assertThat(fillingJobsHistory1).isNotEqualTo(fillingJobsHistory2);
        fillingJobsHistory1.setId(null);
        assertThat(fillingJobsHistory1).isNotEqualTo(fillingJobsHistory2);
    }
}
