package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.service.GameService;
import com.grupo6daw.lcdd_daw.service.ImageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.validation.FieldError;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GamesController {

	@Autowired
	private GameService gameService;

	@Autowired
	private ImageService imageService;

	@GetMapping("/games")
	public String games(Model model) {
		model.addAttribute("game", gameService.findAll());
		return "games";
	}

	@GetMapping("/game/{id}")
	public String gameDetail(@PathVariable long id, Model model) {
		Optional<Game> game = gameService.findById(id);
		if (game.isPresent()) {
			model.addAttribute("game", game.get());
		}
		return "detail_game_page";
	}

	@GetMapping("/game_form")
	public String showGameForm(HttpServletRequest request, Model model) {

		CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrfToken != null) {
			model.addAttribute("token", csrfToken.getToken());
		}

		Game game = new Game();
		game.setGameName("");
		game.setGameDescription("");
		game.setGenre("");

		model.addAttribute("game", game);

		model.addAttribute("hasErrors", false);
		model.addAttribute("allErrors", new ArrayList<String>());

		return "game_form";
	}

	@PostMapping("/game_form")
	public String saveGame(HttpServletRequest request,
			@Valid Game game,
			BindingResult bindingResult,
			@RequestParam("imageField") MultipartFile imageField,
			Model model) throws IOException {

		List<String> errorMessages = new ArrayList<>();

	

    // Check the players count (if both are provided)

    if (game.getMinPlayers() != null && game.getMaxPlayers() != null) {
        if (game.getMinPlayers() > game.getMaxPlayers()) {
            // If minPlayers is greater than maxPlayers, we add an error to the BindingResult
            bindingResult.rejectValue("maxPlayers", "error.maxPlayers", "El máximo de jugadores no puede ser menor que el mínimo.");
        }
    }

    // Check the duration (if both are provided)
    if (game.getMinDuration() != null && game.getMaxDuration() != null) {
        if (game.getMinDuration() > game.getMaxDuration()) {
            bindingResult.rejectValue("maxDuration", "error.maxDuration", "La duración máxima no puede ser menor que la mínima.");
        }
    }


		if (bindingResult.hasErrors() || imageField.isEmpty()) {


			if (bindingResult.hasFieldErrors("gameName")) {
				errorMessages.add(bindingResult.getFieldError("gameName").getDefaultMessage());
			}

			if (bindingResult.hasFieldErrors("gameDescription")) {
				errorMessages.add(bindingResult.getFieldError("gameDescription").getDefaultMessage());
			}

			if (bindingResult.hasFieldErrors("genre")) {
				errorMessages.add(bindingResult.getFieldError("genre").getDefaultMessage());
			}

			if (bindingResult.hasFieldErrors("minPlayers")) {
				errorMessages.add(bindingResult.getFieldError("minPlayers").getDefaultMessage());
			}

			if (bindingResult.hasFieldErrors("maxPlayers")) {
				errorMessages.add(bindingResult.getFieldError("maxPlayers").getDefaultMessage());
			}

			if (bindingResult.hasFieldErrors("minDuration")) {
				errorMessages.add(bindingResult.getFieldError("minDuration").getDefaultMessage());
			}

			if (bindingResult.hasFieldErrors("maxDuration")) {
				errorMessages.add(bindingResult.getFieldError("maxDuration").getDefaultMessage());
			}

			if (imageField.isEmpty()) {
				errorMessages.add("Por favor, sube una imagen de portada para el juego");
			}

			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null) {
				model.addAttribute("token", csrfToken.getToken());
			}

			model.addAttribute("hasErrors", true);
			model.addAttribute("allErrors", errorMessages);
			model.addAttribute("game", game);

			return "game_form";
		}
;
        Image image = imageService.createImage(imageField.getInputStream());
        
      
        game.setGameImage(image);
        
    
        gameService.save(game); 

		return "redirect:/games";
	}

	@PostMapping("/removeGame/{id}")
	public String removeGame(Model model, @PathVariable long id) {

		Optional<Game> game = gameService.findById(id);
		if (game.isPresent()) {
			gameService.delete(id);
			model.addAttribute("game", game.get());
		}

		return "redirect:/games";
	}

}