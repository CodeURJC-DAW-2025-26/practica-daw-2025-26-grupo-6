package com.grupo6daw.lcdd_daw.dto;

import java.util.List;

public record UserDTO(
    Long userId,
    String userName,
    String userSurname,
    String userNickname,
    String userInterests,
    String userEmail,
    String userImageUrl,
    List<String> userRoles
) {}