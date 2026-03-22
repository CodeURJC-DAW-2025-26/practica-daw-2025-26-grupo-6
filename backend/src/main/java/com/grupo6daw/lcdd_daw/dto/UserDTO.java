package com.grupo6daw.lcdd_daw.dto;

import java.util.List;

public record UserDTO(
        Long userId,
        String userName,
        String userSurname,
        String userNickname,
        String userInterests,
        String userEmail,
        ImageDTO userImage,
        List<String> userRoles,
        List<EventBasicDTO> userRegisteredEvents,
        List<EventBasicDTO> userOwnEvents,
        List<GameDTO> userFavGames,
        List<NewBasicDTO> userNews) {
}