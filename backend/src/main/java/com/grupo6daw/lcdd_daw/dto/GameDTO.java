package com.grupo6daw.lcdd_daw.dto;

import java.util.Set;

public record GameDTO(
        Long gameId,
        String gameName,
        String gameDescription,
        ImageDTO gameImage,
        Long minPlayers,
        Long maxPlayers,
        Long minDuration,
        Long maxDuration,
        String genre,
        Set<UserBasicDTO> favoritedByUsers) {
}