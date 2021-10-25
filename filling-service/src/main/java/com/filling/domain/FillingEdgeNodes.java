package com.filling.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A FillingEdgeNodes.
 */
@Entity
@Table(name = "filling_edge_nodes")
public class FillingEdgeNodes implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "title")
  private String title;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "valid")
  private Boolean valid;

  @Column(name = "metadata")
  private String metadata;

  @Column(name = "host")
  private String host;

  @Column(name = "url")
  private String url;

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

  @OneToMany(mappedBy = "fillingEdgeNodes")
  @JsonIgnoreProperties(value = { "fillingEdgeNodes" }, allowSetters = true)
  private Set<FillingEdgeJobs> fillingEdgeJobs = new HashSet<>();

  @OneToMany(mappedBy = "fillingEdgeNodes")
  @JsonIgnoreProperties(value = { "fillingEdgeNodes" }, allowSetters = true)
  private Set<NodeLabel> nodeLabels = new HashSet<>();

  // jhipster-needle-entity-add-field - JHipster will add fields here
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public FillingEdgeNodes id(Long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public FillingEdgeNodes name(String name) {
    this.name = name;
    return this;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return this.title;
  }

  public FillingEdgeNodes title(String title) {
    this.title = title;
    return this;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUuid() {
    return this.uuid;
  }

  public FillingEdgeNodes uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Boolean getValid() {
    return this.valid;
  }

  public FillingEdgeNodes valid(Boolean valid) {
    this.valid = valid;
    return this;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  public String getMetadata() {
    return this.metadata;
  }

  public FillingEdgeNodes metadata(String metadata) {
    this.metadata = metadata;
    return this;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public String getHost() {
    return this.host;
  }

  public FillingEdgeNodes host(String host) {
    this.host = host;
    return this;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUrl() {
    return this.url;
  }

  public FillingEdgeNodes url(String url) {
    this.url = url;
    return this;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDescription() {
    return this.description;
  }

  public FillingEdgeNodes description(String description) {
    this.description = description;
    return this;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Instant getCreated() {
    return this.created;
  }

  public FillingEdgeNodes created(Instant created) {
    this.created = created;
    return this;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getLastModified() {
    return this.lastModified;
  }

  public FillingEdgeNodes lastModified(Instant lastModified) {
    this.lastModified = lastModified;
    return this;
  }

  public void setLastModified(Instant lastModified) {
    this.lastModified = lastModified;
  }

  public String getCreator() {
    return this.creator;
  }

  public FillingEdgeNodes creator(String creator) {
    this.creator = creator;
    return this;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getLastModifier() {
    return this.lastModifier;
  }

  public FillingEdgeNodes lastModifier(String lastModifier) {
    this.lastModifier = lastModifier;
    return this;
  }

  public void setLastModifier(String lastModifier) {
    this.lastModifier = lastModifier;
  }

  public Set<FillingEdgeJobs> getFillingEdgeJobs() {
    return this.fillingEdgeJobs;
  }

  public FillingEdgeNodes fillingEdgeJobs(
    Set<FillingEdgeJobs> fillingEdgeJobs
  ) {
    this.setFillingEdgeJobs(fillingEdgeJobs);
    return this;
  }

  public FillingEdgeNodes addFillingEdgeJobs(FillingEdgeJobs fillingEdgeJobs) {
    this.fillingEdgeJobs.add(fillingEdgeJobs);
    fillingEdgeJobs.setFillingEdgeNodes(this);
    return this;
  }

  public FillingEdgeNodes removeFillingEdgeJobs(
    FillingEdgeJobs fillingEdgeJobs
  ) {
    this.fillingEdgeJobs.remove(fillingEdgeJobs);
    fillingEdgeJobs.setFillingEdgeNodes(null);
    return this;
  }

  public void setFillingEdgeJobs(Set<FillingEdgeJobs> fillingEdgeJobs) {
    if (this.fillingEdgeJobs != null) {
      this.fillingEdgeJobs.forEach(i -> i.setFillingEdgeNodes(null));
    }
    if (fillingEdgeJobs != null) {
      fillingEdgeJobs.forEach(i -> i.setFillingEdgeNodes(this));
    }
    this.fillingEdgeJobs = fillingEdgeJobs;
  }

  public Set<NodeLabel> getNodeLabels() {
    return this.nodeLabels;
  }

  public FillingEdgeNodes nodeLabels(Set<NodeLabel> nodeLabels) {
    this.setNodeLabels(nodeLabels);
    return this;
  }

  public FillingEdgeNodes addNodeLabel(NodeLabel nodeLabel) {
    this.nodeLabels.add(nodeLabel);
    nodeLabel.setFillingEdgeNodes(this);
    return this;
  }

  public FillingEdgeNodes removeNodeLabel(NodeLabel nodeLabel) {
    this.nodeLabels.remove(nodeLabel);
    nodeLabel.setFillingEdgeNodes(null);
    return this;
  }

  public void setNodeLabels(Set<NodeLabel> nodeLabels) {
    if (this.nodeLabels != null) {
      this.nodeLabels.forEach(i -> i.setFillingEdgeNodes(null));
    }
    if (nodeLabels != null) {
      nodeLabels.forEach(i -> i.setFillingEdgeNodes(this));
    }
    this.nodeLabels = nodeLabels;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FillingEdgeNodes)) {
      return false;
    }
    return id != null && id.equals(((FillingEdgeNodes) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "FillingEdgeNodes{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", title='" + getTitle() + "'" +
            ", uuid='" + getUuid() + "'" +
            ", valid='" + getValid() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", host='" + getHost() + "'" +
            ", url='" + getUrl() + "'" +
            ", description='" + getDescription() + "'" +
            ", created='" + getCreated() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", creator='" + getCreator() + "'" +
            ", lastModifier='" + getLastModifier() + "'" +
            "}";
    }
}
