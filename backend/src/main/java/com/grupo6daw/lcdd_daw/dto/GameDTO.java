package com.grupo6daw.lcdd_daw.dto;

public record GameDTO(
        Long gameId,
        String gameName,
        String gameDescription,
        ImageDTO gameImage,
        Long minPlayers,
        Long maxPlayers,
        Long minDuration,
        Long maxDuration,
        String genre) {
}