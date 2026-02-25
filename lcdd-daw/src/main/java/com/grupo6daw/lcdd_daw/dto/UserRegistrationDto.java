package com.grupo6daw.lcdd_daw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email isn't valid")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surnames are required")
    private String surnames;

    @NotBlank(message = "Nickname is required")
    private String nickname;

    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }
    
    public String getSurnames() {
        return surnames;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }
}