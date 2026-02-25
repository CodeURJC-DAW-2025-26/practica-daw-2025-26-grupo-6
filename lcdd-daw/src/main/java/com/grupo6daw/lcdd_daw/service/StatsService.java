package com.grupo6daw.lcdd_daw.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.dto.GameFavStatDTO;
import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.repository.GameRepository;

@Service
public class StatsService {

    private final GameRepository gameRepository;

    public StatsService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameFavStatDTO> topGamesByFavorites(int limit) {
        // Validación simple del parámetro
        int pageSize = Math.max(1, limit);

        var page = gameRepository.findGamesOrderedByFavs(PageRequest.of(0, pageSize));

        return page.stream()
                .map(row -> {
                    Game g = (Game) row[0];
                    long count = ((Number) row[1]).longValue();
                    return new GameFavStatDTO(g.getGameId(), g.getGameName(), count);
                })
                .toList();
    }
}
