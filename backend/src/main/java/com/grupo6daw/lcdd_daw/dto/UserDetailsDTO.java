package com.grupo6daw.lcdd_daw.dto;

import org.springframework.web.multipart.MultipartFile;

public record UserDetailsDTO(
        String userEmail,
        String userName,
        String userSurname,
        String userNickname,
        String password,
        String confirmPassword,
        String userInterests,
        MultipartFile userImage) {
}