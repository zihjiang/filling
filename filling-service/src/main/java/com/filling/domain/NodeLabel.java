package com.filling.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A NodeLabel.
 */
@Entity
@Table(name = "node_label")
public class NodeLabel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "color")
  private String color;

  @ManyToOne
  @JsonIgnoreProperties(
    value = { "fillingEdgeJobs", "nodeLabels" },
    allowSetters = true
  )
  private FillingEdgeNodes fillingEdgeNodes;

  // jhipster-needle-entity-add-field - JHipster will add fields here
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public NodeLabel id(Long id) {
    this.id = id;
    return this;
  }

  public String getTitle() {
    return this.title;
  }

  public NodeLabel title(String title) {
    this.title = title;
    return this;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getColor() {
    return this.color;
  }

  public NodeLabel color(String color) {
    this.color = color;
    return this;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public FillingEdgeNodes getFillingEdgeNodes() {
    return this.fillingEdgeNodes;
  }

  public NodeLabel fillingEdgeNodes(FillingEdgeNodes fillingEdgeNodes) {
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
    if (!(o instanceof NodeLabel)) {
      return false;
    }
    return id != null && id.equals(((NodeLabel) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "NodeLabel{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", color='" + getColor() + "'" +
            "}";
    }
}
