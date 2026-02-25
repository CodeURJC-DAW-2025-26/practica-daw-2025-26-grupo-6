package com.grupo6daw.lcdd_daw.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grupo6daw.lcdd_daw.dto.GameFavStatDTO;
import com.grupo6daw.lcdd_daw.service.StatsService;

@Controller
@RequestMapping("/admin")
public class AdministrationController {

    private final StatsService statsService;

    public AdministrationController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public String admin() {
        return "admin";
    }

    @GetMapping("/api/top-favorite-games")
    @ResponseBody
    public ResponseEntity<List<GameFavStatDTO>> topFavoriteGames(
            @RequestParam(defaultValue = "10") int limit) {

        List<GameFavStatDTO> data = statsService.topGamesByFavorites(limit);
        return ResponseEntity.ok(data);
    }
}
