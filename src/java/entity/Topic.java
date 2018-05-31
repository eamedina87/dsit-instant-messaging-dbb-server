/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author upcnet
 */
@Entity
@Table(name = "topic")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Topic.findAll", query = "SELECT t FROM Topic t"),
  @NamedQuery(name = "Topic.findById", query = "SELECT t FROM Topic t WHERE t.id = :id"),
  @NamedQuery(name = "Topic.findByName", query = "SELECT t FROM Topic t WHERE t.name = :name")})
public class Topic implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Size(max = 45)
  @Column(name = "name")
  private String name;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "topic")
  private Collection<Message> messageCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "topic")
  private Collection<Subscriber> subscriberCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "topic")
  private Collection<Publisher> publisherCollection;

  public Topic() {
  }

  public Topic(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlTransient
  public Collection<Message> getMessageCollection() {
    return messageCollection;
  }

  public void setMessageCollection(Collection<Message> messageCollection) {
    this.messageCollection = messageCollection;
  }

  @XmlTransient
  public Collection<Subscriber> getSubscriberCollection() {
    return subscriberCollection;
  }

  public void setSubscriberCollection(Collection<Subscriber> subscriberCollection) {
    this.subscriberCollection = subscriberCollection;
  }

  @XmlTransient
  public Collection<Publisher> getPublisherCollection() {
    return publisherCollection;
  }

  public void setPublisherCollection(Collection<Publisher> publisherCollection) {
    this.publisherCollection = publisherCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Topic)) {
      return false;
    }
    Topic other = (Topic) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "entity.Topic[ id=" + id + " ]";
  }
  
}
