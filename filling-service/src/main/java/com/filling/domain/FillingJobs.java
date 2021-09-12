package com.filling.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.filling.enumeration.JobStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A FillingJobs.
 */
@Entity
@Table(name = "filling_jobs")
public class FillingJobs implements Serializable {

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

    @OneToMany(mappedBy = "fillingJobs")
    @JsonIgnoreProperties(value = {"fillingJobs"}, allowSetters = true)
    private Set<FillingJobsHistory> fillingJobsHistories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FillingJobs id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public FillingJobs name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public FillingJobs applicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getJobText() {
        return this.jobText;
    }

    public FillingJobs jobText(String jobText) {
        this.jobText = jobText;
        return this;
    }

    public void setJobText(String jobText) {
        this.jobText = jobText;
    }

    public String getType() {
        return this.type == null ? "DAG" : this.type;
    }

    public FillingJobs type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfProp() {
        return this.confProp;
    }

    public FillingJobs confProp(String confProp) {
        this.confProp = confProp;
        return this;
    }

    public void setConfProp(String confProp) {
        this.confProp = confProp;
    }

    public String getStatus() {
        return this.status;
    }

    public FillingJobs status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatetime() {
        return this.createtime;
    }

    public FillingJobs createtime(Instant createtime) {
        this.createtime = createtime;
        return this;
    }

    public void setCreatetime(Instant createtime) {
        this.createtime = createtime;
    }

    public Instant getUpdatetime() {
        return this.updatetime;
    }

    public FillingJobs updatetime(Instant updatetime) {
        this.updatetime = updatetime;
        return this;
    }

    public void setUpdatetime(Instant updatetime) {
        this.updatetime = updatetime;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public FillingJobs createdBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAddjar() {
        return this.addjar;
    }

    public FillingJobs addjar(String addjar) {
        this.addjar = addjar;
        return this;
    }

    public void setAddjar(String addjar) {
        this.addjar = addjar;
    }

    public String getDescription() {
        return this.description;
    }

    public FillingJobs description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<FillingJobsHistory> getFillingJobsHistories() {
        return this.fillingJobsHistories;
    }

    public FillingJobs fillingJobsHistories(Set<FillingJobsHistory> fillingJobsHistories) {
        this.setFillingJobsHistories(fillingJobsHistories);
        return this;
    }

    public FillingJobs addFillingJobsHistory(FillingJobsHistory fillingJobsHistory) {
        this.fillingJobsHistories.add(fillingJobsHistory);
        fillingJobsHistory.setFillingJobs(this);
        return this;
    }

    public FillingJobs removeFillingJobsHistory(FillingJobsHistory fillingJobsHistory) {
        this.fillingJobsHistories.remove(fillingJobsHistory);
        fillingJobsHistory.setFillingJobs(null);
        return this;
    }

    public void setFillingJobsHistories(Set<FillingJobsHistory> fillingJobsHistories) {
        if (this.fillingJobsHistories != null) {
            this.fillingJobsHistories.forEach(i -> i.setFillingJobs(null));
        }
        if (fillingJobsHistories != null) {
            fillingJobsHistories.forEach(i -> i.setFillingJobs(this));
        }
        this.fillingJobsHistories = fillingJobsHistories;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FillingJobs)) {
            return false;
        }
        return id != null && id.equals(((FillingJobs) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FillingJobs{" +
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

    public String toJobString() {
        // TODO
        switch (type) {
            case "DAG":


            case "SQL":
                break;
        }

        return getDAGString().toJSONString();
    }

    private JSONObject getDAGString() {
        JSONObject result = new JSONObject();
        JSONArray orgJobTextNodes = JSONObject.parseObject(jobText).getJSONArray("nodes");

        JSONArray source = new JSONArray();
        JSONArray transfrom = new JSONArray();
        JSONArray sink = new JSONArray();
        orgJobTextNodes.stream().forEach(d -> {
            JSONObject jsonObject = JSON.parseObject(d.toString());
            if("source".equals(jsonObject.getString("PluginType"))) {
                source.add(jsonObject.getJSONObject("data"));
            } else if("transform".equals(jsonObject.getString("PluginType"))) {
                transfrom.add(jsonObject.getJSONObject("data"));
            } else if("sink".equals(jsonObject.getString("PluginType"))) {
                sink.add(jsonObject.getJSONObject("data"));
            }
        });

        result.put("env", new JSONObject());
        result.put("source", source);
        result.put("transform", transfrom);
        result.put("sink", sink);
        return result;
    }
}
