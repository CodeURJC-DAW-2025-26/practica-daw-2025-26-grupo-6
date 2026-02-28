package com.grupo6daw.lcdd_daw.model;

import java.time.LocalDateTime;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId = null;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre debe tener entre 5 y 100 caracteres")
    private String eventName;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 50, max = 3000, message = "La descripción debe tener entre 50 y 3000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String eventDescription;

    @OneToOne
    @JoinColumn(name = "fk_image") // Forcing column to change name to 'fk_image'
    private Image eventImage;

    private String eventTag;

    private boolean requiresRegistration;

    @Size(max = 255, message = "El enlace no puede superar los 255 caracteres")
    private String link;

    @ManyToMany(mappedBy = "userRegisteredEvents")
    private Set<User> eventRegisteredUsers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "fk_creator") // Forcing column to change name to 'fk_creator'
    private User eventCreator;

    @OneToMany
    private List<New> eventNews;

    @Column(nullable = false)
    private boolean validated = false;

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Column(updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    public Event() {
    }

    public Event(String eventName, String eventDescription, String eventTag, boolean validated) {
        super();
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventTag = eventTag;
        this.validated = validated;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
