package com.grupo6daw.lcdd_daw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grupo6daw.lcdd_daw.dto.EventParticipantsStatDTO;
import com.grupo6daw.lcdd_daw.dto.GameFavStatDTO;
import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.service.EventService;
import com.grupo6daw.lcdd_daw.service.NewService;
import com.grupo6daw.lcdd_daw.service.StatsService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdministrationController {

    private final StatsService statsService;
    private final EventService eventService;
    private final NewService newService;

    public AdministrationController(StatsService statsService, EventService eventService, NewService newService) {
        this.statsService = statsService;
        this.eventService = eventService;
        this.newService = newService;
    }

    @GetMapping
    public String admin(HttpServletRequest request, Model model) {
        model.addAttribute("events", eventService.findByValidatedFalse());
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("token", csrfToken.getToken());
        }
        return "admin";
    }

    @GetMapping("/api/top-favorite-games")
    @ResponseBody
    public ResponseEntity<List<GameFavStatDTO>> topFavoriteGames(
            @RequestParam(defaultValue = "5") int limit) {

        List<GameFavStatDTO> data = statsService.topGamesByFavorites(limit);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/api/top-events-participants")
    @ResponseBody
    public ResponseEntity<List<EventParticipantsStatDTO>> topEventsParticipants(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(statsService.topEventsByParticipants(limit));
    }

    @PostMapping("/approveEvent/{id}")
    public String approveEvent(@PathVariable long id) {
        Optional<Event> event = eventService.findById(id);
        if (event.isPresent()) {
            Event e = event.get();
            e.setValidated(true);
            eventService.save(e);
        }
        return "redirect:/admin";
    }

    @PostMapping("/rejectEvent/{id}")
    public String rejectEvent(@PathVariable long id) {
        eventService.delete(id);
        return "redirect:/admin";
    }
}
