package com.grupo6daw.lcdd_daw.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId = null;

    private String eventName;

    @Column(columnDefinition = "TEXT")
    private String eventDescription;

    @OneToOne
    private Image eventImage;

    private String eventTag;

    @ManyToMany(mappedBy = "userRegisteredEvents")
    private Set<User> eventRegisteredUsers = new HashSet<>();

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

    public String getEventTag() {
        return eventTag;
    }

    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    public Set<User> getEventRegisteredUsers() {
        return eventRegisteredUsers;
    }

    public void setEventRegisteredUsers(Set<User> eventRegisteredUsers) {
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
