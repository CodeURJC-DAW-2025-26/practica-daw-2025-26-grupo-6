package com.grupo6daw.lcdd_daw.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grupo6daw.lcdd_daw.dto.EventParticipantsStatDTO;
import com.grupo6daw.lcdd_daw.dto.GameFavStatDTO;
import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.service.EventService;
import com.grupo6daw.lcdd_daw.service.NewService;
import com.grupo6daw.lcdd_daw.service.StatsService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdministrationRestController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private EventService eventService;

    @Autowired
    private NewService newService;

    // --- Graphics ---
    @GetMapping("/top-favorite-games")
    public ResponseEntity<List<GameFavStatDTO>> getTopGames() {
        return ResponseEntity.ok(statsService.topGamesByFavorites(10));
    }

    @GetMapping("/top-events-participants")
    public ResponseEntity<List<EventParticipantsStatDTO>> getTopEvents() {
        return ResponseEntity.ok(statsService.topEventsByParticipants(10));
    }

    // ---Accept / Deny (Swagger) ---

    @PutMapping("/events/{id}")
    public ResponseEntity<Void> approveEvent(@PathVariable long id) {
        Event event = eventService.findById(id);
        event.setValidated(true);
        eventService.save(event);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> rejectEvent(@PathVariable long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/news/{id}")
    public ResponseEntity<Void> approveNew(@PathVariable long id) {
        New news = newService.findById(id);
        news.setValidated(true);
        newService.save(news);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/news/{id}")
    public ResponseEntity<Void> rejectNew(@PathVariable long id) {
        newService.delete(id);
        return ResponseEntity.noContent().build();
    }
}