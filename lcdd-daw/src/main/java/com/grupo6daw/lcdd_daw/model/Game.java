package com.grupo6daw.lcdd_daw.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId = null;

    private String gameName;

    @Column(columnDefinition = "TEXT")
    private String gameDescription;

    @OneToOne
    private Image gameImage;

    private Long minPlayers;

    private Long maxPlayers;

    private Long minDuration;

    private Long maxDuration;

    private String genre;

    @ManyToMany(mappedBy = "userFavGames")
    private Set<User> favoritedByUsers = new HashSet<>();

    public Game() {
    }

    public Game(String gameName, String gameDescription, Long minPlayers, Long maxPlayers, Long minDuration,
            Long maxDuration, String genre) {
        super();
        this.gameName = gameName;
        this.gameDescription = gameDescription;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.genre = genre;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public void setGameDescription(String gameDescription) {
        this.gameDescription = gameDescription;
    }

    public Image getGameImage() {
        return gameImage;
    }

    public void setGameImage(Image gameImage) {
        this.gameImage = gameImage;
    }

    public Long getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(Long minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Long getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Long maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Long getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Long minDuration) {
        this.minDuration = minDuration;
    }

    public Long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Set<User> getFavoritedByUsers() {
        return favoritedByUsers;
    }

    public void setFavoritedByUsers(Set<User> favoritedByUsers) {
        this.favoritedByUsers = favoritedByUsers;
    }

}
