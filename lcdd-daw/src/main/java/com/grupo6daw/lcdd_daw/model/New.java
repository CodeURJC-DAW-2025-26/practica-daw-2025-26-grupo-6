package com.grupo6daw.lcdd_daw.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class New {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long newId = null;

    @NotBlank(message = "El nombre/título de la noticia es obligatorio")
    @Size(min = 10, max = 100, message = "El título debe tener entre 10 y 100 caracteres")
    private String newName;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 50, max = 3000, message = "La descripción debe tener entre 50 y 3000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String newDescription;

    @OneToOne
    private Image newImage;

    private String newTag;

    @OneToMany
    private List<Event> newEvents;

    @ManyToOne
    private User newCreator;

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

    public New() {
    }

    public New(String newName, String newDescription, String newTag, boolean validated) {
        super();
        this.newName = newName;
        this.newDescription = newDescription;
        this.newTag = newTag;
        this.validated = validated;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
