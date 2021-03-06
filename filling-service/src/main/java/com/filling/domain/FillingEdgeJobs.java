package com.filling.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * A FillingEdgeJobs.
 */
@Entity
@Table(name = "filling_edge_jobs")
public class FillingEdgeJobs implements Serializable {

//    private final Logger log = LoggerFactory.getLogger(FillingEdgeJobs.class);

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "pipeline_id")
    private String pipelineId = UUID.randomUUID().toString();

    @Column(name = "title")
    private String title;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "valid")
    private Boolean valid;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "ctl_version")
    private String ctlVersion;

    @Column(name = "ctl_id")
    private String ctlId;

    @Column(name = "ui_info")
    private String uiInfo;

    @Column(name = "info")
    private String info;

    @Column(name = "job_text")
    private String jobText;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private Instant created;

    @Column(name = "last_modified")
    private Instant lastModified;

    @Column(name = "creator")
    private String creator;

    @Column(name = "last_modifier")
    private String lastModifier;

    @ManyToOne
    @JsonIgnoreProperties(value = {"fillingEdgeJobs", "nodeLabels"}, allowSetters = true)
    private FillingEdgeNodes fillingEdgeNodes;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FillingEdgeJobs id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public FillingEdgeJobs name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPipelineId() {
        return this.pipelineId;
    }

    public FillingEdgeJobs pipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
        return this;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getTitle() {
        return this.title;
    }

    public FillingEdgeJobs title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return this.uuid;
    }

    public FillingEdgeJobs uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getValid() {
        return this.valid;
    }

    public FillingEdgeJobs valid(Boolean valid) {
        this.valid = valid;
        return this;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public FillingEdgeJobs metadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getCtlVersion() {
        return this.ctlVersion;
    }

    public FillingEdgeJobs ctlVersion(String ctlVersion) {
        this.ctlVersion = ctlVersion;
        return this;
    }

    public void setCtlVersion(String ctlVersion) {
        this.ctlVersion = ctlVersion;
    }

    public String getCtlId() {
        return this.ctlId;
    }

    public FillingEdgeJobs ctlId(String ctlId) {
        this.ctlId = ctlId;
        return this;
    }

    public void setCtlId(String ctlId) {
        this.ctlId = ctlId;
    }

    public String getUiInfo() {
        return this.uiInfo;
    }

    public FillingEdgeJobs uiInfo(String uiInfo) {
        this.uiInfo = uiInfo;
        return this;
    }

    public void setUiInfo(String uiInfo) {
        this.uiInfo = uiInfo;
    }

    public String getInfo() {
        return this.info;
    }

    public FillingEdgeJobs info(String info) {
        this.info = info;
        return this;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getJobText() {
        return this.jobText;
    }

    public FillingEdgeJobs jobText(String jobText) {
        this.jobText = jobText;
        return this;
    }

    public void setJobText(String jobText) {
        this.jobText = jobText;
    }

    public String getStatus() {
        return this.status;
    }

    public FillingEdgeJobs status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public FillingEdgeJobs description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreated() {
        return this.created;
    }

    public FillingEdgeJobs created(Instant created) {
        this.created = created;
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getLastModified() {
        return this.lastModified;
    }

    public FillingEdgeJobs lastModified(Instant lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getCreator() {
        return this.creator;
    }

    public FillingEdgeJobs creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLastModifier() {
        return this.lastModifier;
    }

    public FillingEdgeJobs lastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
        return this;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public FillingEdgeNodes getFillingEdgeNodes() {
        return this.fillingEdgeNodes;
    }

    public FillingEdgeJobs fillingEdgeNodes(FillingEdgeNodes fillingEdgeNodes) {
        this.setFillingEdgeNodes(fillingEdgeNodes);
        return this;
    }

    public void setFillingEdgeNodes(FillingEdgeNodes fillingEdgeNodes) {
        this.fillingEdgeNodes = fillingEdgeNodes;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FillingEdgeJobs)) {
            return false;
        }
        return id != null && id.equals(((FillingEdgeJobs) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FillingEdgeJobs{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", pipelineId='" + getPipelineId() + "'" +
            ", title='" + getTitle() + "'" +
            ", uuid='" + getUuid() + "'" +
            ", valid='" + getValid() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", ctlVersion='" + getCtlVersion() + "'" +
            ", ctlId='" + getCtlId() + "'" +
            ", uiInfo='" + getUiInfo() + "'" +
            ", info='" + getInfo() + "'" +
            ", jobText='" + getJobText() + "'" +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", created='" + getCreated() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", creator='" + getCreator() + "'" +
            ", lastModifier='" + getLastModifier() + "'" +
            "}";
    }

    public String getJobString() {

        JSONObject result = new JSONObject();
        JSONArray stages = new JSONArray();

        JSONArray orgJobTextNodes = JSONObject.parseObject(jobText).getJSONArray("nodes");

        orgJobTextNodes.stream().forEach(d -> {
            JSONArray configuration = new JSONArray();
            JSONObject jsonObject = JSON.parseObject(d.toString());
            JSONObject node = new JSONObject();
            JSONObject data = jsonObject.getJSONObject("data");
            // stageType
            String stageType = jsonObject.getString("PluginType");

            // left
            Integer left = jsonObject.getInteger("left");
            // top
            Integer top = jsonObject.getInteger("top");

            data.keySet().forEach(_d -> {
                JSONObject _data = new JSONObject();
                _data.put("name", _d);
                _data.put("value", data.get(_d));
                configuration.add(_data);
            });

            node.put("instanceName", jsonObject.get("instanceName"));

            node.put("library", jsonObject.get("library"));
            node.put("stageName", jsonObject.get("stageName"));
            node.put("stageVersion", jsonObject.get("stageVersion"));
            node.put("inputLanes", jsonObject.get("inputLanes"));
            node.put("outputLanes", jsonObject.get("outputLanes"));
            node.put("configuration", configuration);


            node.put("uiInfo", getDefaultStageUiInfo(stageType, left, top));

            stages.add(node);
        });


        result.put("schemaVersion", 1);
        result.put("version", 1);
        result.put("title", title);
        result.put("description", description);
        result.put("uuid", uuid);
        result.put("uiInfo", JSONObject.parseObject(uiInfo));
        result.put("errorStage", getErrorStage());
        result.put("statsAggregatorStage", new JSONObject());
        result.put("previewable", true);
        result.put("info", new JSONObject());

        result.put("pipelineId", pipelineId);
        result.put("stages", stages);

//        log.debug("getJobString: {}", result.toJSONString());

        return result.toJSONString();
    }

    private JSONObject getErrorStage() {
        String str = "{\n" +
            "    \"instanceName\": \"errorStageStageInstance\",\n" +
            "    \"library\": \"streamsets-datacollector-basic-lib\",\n" +
            "    \"stageName\": \"com_streamsets_pipeline_stage_destination_devnull_ToErrorNullDTarget\",\n" +
            "    \"stageVersion\": \"1\",\n" +
            "    \"configuration\": [],\n" +
            "    \"services\": [],\n" +
            "    \"uiInfo\": {\n" +
            "      \"label\": \"Error -Discard\",\n" +
            "      \"stageType\": \"TARGET\"\n" +
            "    },\n" +
            "    \"inputLanes\": [],\n" +
            "    \"outputLanes\": [],\n" +
            "    \"eventLanes\": []\n" +
            "  }";

        return JSONObject.parseObject(str);
    }

    private JSONObject getDefaultStageUiInfo(String stageType, Integer xPos, Integer yPos) {

        JSONObject result = new JSONObject();
        result.put("description", "");
        result.put("displayMode", "BASIC");
        result.put("icon", "httpclient.png");
        result.put("label", "HTTP Client 1");
        result.put("stageType", stageType);
        result.put("description", "by filling");
        result.put("xPos", xPos);
        result.put("yPos", yPos);
        return result;
    }
}
