package com.grupo6daw.lcdd_daw.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId = null;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String eventName;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(columnDefinition = "TEXT")
    private String eventDescription;

    @OneToOne
    @JoinColumn(name = "fk_image") // Forcing column to change name to 'fk_image'
    private Image eventImage;

    private String eventTag;

    private boolean requiresRegistration;
    private String link;

    @ManyToMany(mappedBy = "userRegisteredEvents")
    private Set<User> eventRegisteredUsers = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "fk_creator") // Forcing column to change name to 'fk_creator'
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

    public boolean isRequiresRegistration() {
        return requiresRegistration;
    }

    public void setRequiresRegistration(boolean requiresRegistration) {
        this.requiresRegistration = requiresRegistration;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
