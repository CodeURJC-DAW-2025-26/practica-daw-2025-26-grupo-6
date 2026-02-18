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
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long eventId = null;

  private String eventName;

  @Column(columnDefinition = "TEXT")
  private String eventDescription;

  @OneToOne
  private Image eventImage;

  private String eventTag;

  @OneToMany
  private List<User> eventRegisteredUsers;

  @OneToOne
  private User eventCreator;

  @OneToMany
  private List<New> eventNews;

  public Event() {
  }

  public Event(String eventName, String eventDescription, String eventTag) {
    super();
    this.eventName = eventName;
    this.eventDescription = eventDescription;
    this.eventTag = eventTag;
  }

  public Long getEventId() {
    return eventId;
  }

  public void setEventId(Long eventId) {
    this.eventId = eventId;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getEventDescription() {
    return eventDescription;
  }

  public void setEventDescription(String eventDescription) {
    this.eventDescription = eventDescription;
  }

  public Image getEventImage() {
    return eventImage;
  }

  public void setEventImage(Image eventImage) {
    this.eventImage = eventImage;
  }

  public String geteventTag() {
    return eventTag;
  }

  public void seteventTag(String eventTag) {
    this.eventTag = eventTag;
  }

  public List<User> getEventRegisteredUsers() {
    return eventRegisteredUsers;
  }

  public void setEventRegisteredUsers(List<User> eventRegisteredUsers) {
    this.eventRegisteredUsers = eventRegisteredUsers;
  }

  public User getEventCreator() {
    return eventCreator;
  }

  public void setEventCreator(User eventCreator) {
    this.eventCreator = eventCreator;
  }

  public List<New> getEventNews() {
    return eventNews;
  }
  
  public void setEventNews(List<New> eventNews) {
    this.eventNews = eventNews;
  }
}
