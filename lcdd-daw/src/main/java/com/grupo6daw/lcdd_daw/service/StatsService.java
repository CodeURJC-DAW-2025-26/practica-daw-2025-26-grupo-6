package com.grupo6daw.lcdd_daw.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.dto.EventParticipantsStatDTO;
import com.grupo6daw.lcdd_daw.dto.GameFavStatDTO;
import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.repository.EventRepository;
import com.grupo6daw.lcdd_daw.repository.GameRepository;

@Service
public class StatsService {

    private final GameRepository gameRepository;
    private final EventRepository eventRepository;

    public StatsService(GameRepository gameRepository, EventRepository eventRepository) {
        this.gameRepository = gameRepository;
        this.eventRepository = eventRepository;
    }

    public List<GameFavStatDTO> topGamesByFavorites(int limit) {
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
    
    public List<EventParticipantsStatDTO> topEventsByParticipants(int limit) {
        int pageSize = Math.max(1, limit);
        
        var page = eventRepository.findEventsOrderedByParticipants(PageRequest.of(0, pageSize));
        
        return page.stream()
                .map(row -> {
                    Event e = (Event) row[0];
                    long count = ((Number) row[1]).longValue();
                    return new EventParticipantsStatDTO(e.getEventId(), e.getEventName(), count);
                })
                .toList();
    }

}
