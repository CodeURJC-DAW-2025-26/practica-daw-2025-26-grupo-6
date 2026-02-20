package com.grupo6daw.lcdd_daw.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class New {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long newId = null;

  private String newName;

  @Column(columnDefinition = "TEXT")
  private String newDescription;

  @OneToOne
  private Image newImage;

  private String newTag;

  @OneToMany
  private List<Event> newEvents;

  @OneToOne
  private User newCreator;

  public New() {
  }

  public New(String newName, String newDescription, String newTag) {
    super();
    this.newName = newName;
    this.newDescription = newDescription;
    this.newTag = newTag;
  }

  public Long getNewId() {
    return newId;
  }

  public void setNewId(Long newId) {
    this.newId = newId;
  }

  public String getNewName() {
    return newName;
  }

  public void setNewName(String newName) {
    this.newName = newName;
  }

  public String getNewDescription() {
    return newDescription;
  }

  public void setNewDescription(String newDescription) {
    this.newDescription = newDescription;
  }

  public Image getNewImage() {
    return newImage;
  }

  public void setNewImage(Image newImage) {
    this.newImage = newImage;
  }

  public String getNewTag() {
    return newTag;
  }

  public void setNewTag(String newTag) {
    this.newTag = newTag;
  }

  public List<Event> getNewEvents() {
    return newEvents;
  }

  public void setNewEvents(List<Event> newEvents) {
    this.newEvents = newEvents;
  }

  public User getNewCreator() {
    return newCreator;
  }

  public void setNewCreator(User newCreator) {
    this.newCreator = newCreator;
  }
}
