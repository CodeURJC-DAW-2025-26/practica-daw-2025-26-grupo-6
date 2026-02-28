package com.grupo6daw.lcdd_daw.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.EventService;
import com.grupo6daw.lcdd_daw.service.NewService;
import com.grupo6daw.lcdd_daw.service.StatsService;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdministrationController {

    private final StatsService statsService;
    private final EventService eventService;
    private final NewService newService;
    private final UserService userService;

    public AdministrationController(StatsService statsService, EventService eventService, NewService newService, UserService userService) {
        this.statsService = statsService;
        this.eventService = eventService;
        this.newService = newService;
        this.userService = userService;
    }

    @GetMapping
    public String admin(HttpServletRequest request, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userNickname,asc") String sort
    ) {

        model.addAttribute("events", eventService.findByValidatedFalse());

        String[] parts = sort.split(",", 2);
        String field = parts[0];
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, field));

        Page<User> usersPage = userService.findAll(pageable);

        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("usersPage", usersPage);

        int current = usersPage.getNumber();
        int total = usersPage.getTotalPages();
        int prev = Math.max(0, current - 1);
        int next = Math.min(Math.max(total - 1, 0), current + 1);
        boolean hasNext = current < total - 1;

        model.addAttribute("currentPage", current);
        model.addAttribute("totalPages", total);
        model.addAttribute("pageSize", usersPage.getSize());
        model.addAttribute("sort", sort);
        model.addAttribute("prevPage", prev);
        model.addAttribute("nextPage", next);
        model.addAttribute("hasNext", hasNext);

        // Numeración (0..total-1) para la barra de páginas
        List<Map<String, Object>> pages = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            pages.add(Map.of(
                    "index", i,
                    "display", i + 1,
                    "active", i == current
            ));
        }
        model.addAttribute("pages", pages);

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("token", csrfToken.getToken());
        }
        return "admin";
    }

    @GetMapping("/api/top-favorite-games")
    @ResponseBody
    public ResponseEntity<List<GameFavStatDTO>> topFavoriteGames(
            @RequestParam(defaultValue = "10") int limit) {

        List<GameFavStatDTO> data = statsService.topGamesByFavorites(limit);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/api/top-events-participants")
    @ResponseBody
    public ResponseEntity<List<EventParticipantsStatDTO>> topEventsParticipants(
            @RequestParam(defaultValue = "10") int limit) {
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

    @GetMapping("/user/{id}")
    public String viewUserProfile(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        model.addAttribute("user", user);
        return "profile";
    }

}
