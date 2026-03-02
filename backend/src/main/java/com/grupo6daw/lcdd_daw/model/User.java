package com.grupo6daw.lcdd_daw.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity(name = "UserTable")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId = null;

    private String userName;

    private String userSurname;

    private String userNickname;

    @Column(columnDefinition = "TEXT")
    private String userInterests;

    private String userEmail;

    private String userEncodedPassword;

    @ManyToMany
    @JoinTable(
            name = "user_favorite_games",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(name = "game_id", referencedColumnName = "gameId")
    )
    private Set<Game> userFavGames = new HashSet<>();

    public void addFavoriteGame(Game game) {
        if (this.userFavGames.add(game)) {
            game.getFavoritedByUsers().add(this);
        }
    }

    public void removeFavoriteGame(Game game) {
        if (this.userFavGames.remove(game)) {
            game.getFavoritedByUsers().remove(this);
        }
    }

    @OneToMany
    private List<Event> userOwnEvents;

    @ManyToMany
    @JoinTable(
            name = "user_registered_events",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "eventId")
    )
    private Set<Event> userRegisteredEvents = new HashSet<>();

    public void registerToEvent(Event event) {
        if (this.userRegisteredEvents.add(event)) {
            event.getEventRegisteredUsers().add(this);
        }
    }

    public void unregisterFromEvent(Event event) {
        if (this.userRegisteredEvents.remove(event)) {
            event.getEventRegisteredUsers().remove(this);
        }
    }

    @OneToMany
    private List<New> userNews;

    @OneToOne(cascade = CascadeType.ALL)
    private Image userImage;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> userRoles;

    public User() {
    }

    public User(String userName, String userSurname, String userNickname, String userInterests, String userEmail,
            String userEncodedPassword, String... roles) {
        super();
        this.userName = userName;
        this.userSurname = userSurname;
        this.userNickname = userNickname;
        this.userInterests = userInterests;
        this.userEmail = userEmail;
        this.userEncodedPassword = userEncodedPassword;
        this.userRoles = List.of(roles);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserInterests() {
        return userInterests;
    }

    public void setUserInterests(String userInterests) {
        this.userInterests = userInterests;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEncodedPassword() {
        return userEncodedPassword;
    }

    public void setUserEncodedPassword(String userEncodedPassword) {
        this.userEncodedPassword = userEncodedPassword;
    }

    public Image getUserImage() {
        return userImage;
    }

    public void setUserImage(Image userImage) {
        this.userImage = userImage;
    }

    public Set<Game> getUserFavGames() {
        return userFavGames;
    }

    public void setUserFavGames(Set<Game> userFavGames) {
        this.userFavGames = userFavGames;
    }

    public Set<Event> getUserRegisteredEvents() {
        return userRegisteredEvents;
    }

    public void setUserRegisteredEvents(Set<Event> userRegisteredEvents) {
        this.userRegisteredEvents = userRegisteredEvents;
    }

    public List<Event> getUserOwnEvents() {
        return userOwnEvents;
    }

    public void setUserOwnEvents(List<Event> userOwnEvents) {
        this.userOwnEvents = userOwnEvents;
    }

    public List<New> getUserNews() {
        return userNews;
    }

    public void setUserNews(List<New> userNews) {
        this.userNews = userNews;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }
}
