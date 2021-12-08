package com.filling.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A FillingJobsHistory.
 */
@Entity
@Table(name = "filling_jobs_history")
public class FillingJobsHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "application_id")
    private String applicationId;

    @Column(name = "job_text")
    private String jobText;

    @Column(name = "type")
    private String type;

    @Column(name = "conf_prop")
    private String confProp;

    @Column(name = "status")
    private String status;

    @Column(name = "createtime")
    private Instant createtime;

    @Column(name = "updatetime")
    private Instant updatetime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "addjar")
    private String addjar;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JsonIgnoreProperties(value = { "fillingJobsHistories" }, allowSetters = true)
    private FillingJobs fillingJobs;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FillingJobsHistory id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public FillingJobsHistory name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public FillingJobsHistory applicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getJobText() {
        return this.jobText;
    }

    public FillingJobsHistory jobText(String jobText) {
        this.jobText = jobText;
        return this;
    }

    public void setJobText(String jobText) {
        this.jobText = jobText;
    }

    public String getType() {
        return this.type;
    }

    public FillingJobsHistory type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfProp() {
        return this.confProp;
    }

    public FillingJobsHistory confProp(String confProp) {
        this.confProp = confProp;
        return this;
    }

    public void setConfProp(String confProp) {
        this.confProp = confProp;
    }

    public String getStatus() {
        return this.status;
    }

    public FillingJobsHistory status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatetime() {
        return this.createtime;
    }

    public FillingJobsHistory createtime(Instant createtime) {
        this.createtime = createtime;
        return this;
    }

    public void setCreatetime(Instant createtime) {
        this.createtime = createtime;
    }

    public Instant getUpdatetime() {
        return this.updatetime;
    }

    public FillingJobsHistory updatetime(Instant updatetime) {
        this.updatetime = updatetime;
        return this;
    }

    public void setUpdatetime(Instant updatetime) {
        this.updatetime = updatetime;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public FillingJobsHistory createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAddjar() {
        return this.addjar;
    }

    public FillingJobsHistory addjar(String addjar) {
        this.addjar = addjar;
        return this;
    }

    public void setAddjar(String addjar) {
        this.addjar = addjar;
    }

    public String getDescription() {
        return this.description;
    }

    public FillingJobsHistory description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FillingJobs getFillingJobs() {
        return this.fillingJobs;
    }

    public FillingJobsHistory fillingJobs(FillingJobs fillingJobs) {
        this.setFillingJobs(fillingJobs);
        return this;
    }

    public void setFillingJobs(FillingJobs fillingJobs) {
        this.fillingJobs = fillingJobs;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FillingJobsHistory)) {
            return false;
        }
        return id != null && id.equals(((FillingJobsHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FillingJobsHistory{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", applicationId='" + getApplicationId() + "'" +
            ", jobText='" + getJobText() + "'" +
            ", type='" + getType() + "'" +
            ", confProp='" + getConfProp() + "'" +
            ", status='" + getStatus() + "'" +
            ", createtime='" + getCreatetime() + "'" +
            ", updatetime='" + getUpdatetime() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", addjar='" + getAddjar() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
