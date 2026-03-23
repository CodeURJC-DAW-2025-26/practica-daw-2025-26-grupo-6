package com.grupo6daw.lcdd_daw.dto;

public record UserDetailsDTO(
        String userEmail,
        String userName,
        String userSurname,
        String userNickname,
        String password,
        String confirmPassword,
        String userInterests) {
}