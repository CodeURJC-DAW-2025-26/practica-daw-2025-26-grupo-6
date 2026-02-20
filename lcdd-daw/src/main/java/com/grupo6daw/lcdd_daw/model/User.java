package com.grupo6daw.lcdd_daw.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity(name = "UserTable")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long userId = null;

  private String userName;

  private String userSurname;

  private String userNickname;

  @Column(columnDefinition = "TEXT")
  private String userInterests;

  private String userEmail;

  private String userEncodedPassword;

  @OneToMany
  private List<Game> userFavGames;

  @OneToMany
  private List<Event> userOwnEvents;

  @OneToMany
  private List<Event> userRegisteredEvents;

  @OneToMany
  private List<New> userNews;

  @OneToOne
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

  public List<Game> getUserFavGames() {
    return userFavGames;
  }

  public void setUserFavGames(List<Game> userFavGames) {
    this.userFavGames = userFavGames;
  }

  public List<Event> getUserOwnEvents() {
    return userOwnEvents;
  }

  public void setUserOwnEvents(List<Event> userOwnEvents) {
    this.userOwnEvents = userOwnEvents;
  }

  public List<Event> getUserRegisteredEvents() {
    return userRegisteredEvents;
  }

  public void setUserRegisteredEvents(List<Event> userRegisteredEvents) {
    this.userRegisteredEvents = userRegisteredEvents;
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
