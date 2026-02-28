package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.service.GameService;
import com.grupo6daw.lcdd_daw.service.ImageService;
import com.grupo6daw.lcdd_daw.service.ImageValidationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class GamesController {

	@Autowired
	private GameService gameService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ImageValidationService imageValidationService;

	@GetMapping("/games")
	public String games(Model model,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String tag,
			@RequestParam(required = false) Integer players,
			@RequestParam(required = false) Integer duration,
			@RequestParam(defaultValue = "0") int page) {

		Page<Game> gamesPage = gameService.findByFilter(name, tag, players, duration, PageRequest.of(page, 10));

		model.addAttribute("game", gamesPage.getContent());
		model.addAttribute("name", name == null ? "" : name);
		model.addAttribute("tag", tag == null ? "" : tag);
		model.addAttribute("hasNext", gamesPage.hasNext());
		model.addAttribute("nextPage", page + 1);
		
		return "games";
	}

	@GetMapping("/game/{id}")
	public String gameDetail(@PathVariable long id, Model model, HttpServletRequest request) {
		Optional<Game> game = gameService.findById(id);
		if (game.isPresent()) {
			model.addAttribute("game", game.get());

			boolean hasEditPermission = false;
			if (request.getUserPrincipal() != null) {
				hasEditPermission = request.isUserInRole("ADMIN");
			}
			model.addAttribute("hasEditPermission", hasEditPermission);
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

	@GetMapping("/game_form/{id}")
	public String editGameForm(@PathVariable long id, Model model, HttpServletRequest request) {
		Optional<Game> game = gameService.findById(id);
		if (game.isPresent()) {
			model.addAttribute("game", game.get());
			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null) {
				model.addAttribute("token", csrfToken.getToken());
			}
			model.addAttribute("hasErrors", false);
			model.addAttribute("allErrors", new ArrayList<String>());
			return "game_form";
		}
		return "redirect:/games";
	}

	@PostMapping("/game_form")
	public String saveGame(HttpServletRequest request,
			@Valid Game game,
			BindingResult bindingResult,
			@RequestParam("imageField") MultipartFile imageField,
			Model model) throws IOException {

		List<String> errorMessages = new ArrayList<>();
		boolean isNewGame = (game.getGameId() == null);

		// logical validation for players and duration
		if (game.getMinPlayers() != null && game.getMaxPlayers() != null) {
			if (game.getMinPlayers() > game.getMaxPlayers()) {
				bindingResult.rejectValue("maxPlayers", "error.maxPlayers",
						"El máximo de jugadores no puede ser menor que el mínimo.");
			}
		}

		if (game.getMinDuration() != null && game.getMaxDuration() != null) {
			if (game.getMinDuration() > game.getMaxDuration()) {
				bindingResult.rejectValue("maxDuration", "error.maxDuration",
						"La duración máxima no puede ser menor que la mínima.");
			}
		}

		// img validation
		imageValidationService.validate(imageField, errorMessages, isNewGame);

		if (bindingResult.hasErrors() || !errorMessages.isEmpty()) {

			// passing annotation errors
			if (bindingResult.hasFieldErrors("gameName"))
				errorMessages.add(bindingResult.getFieldError("gameName").getDefaultMessage());
			if (bindingResult.hasFieldErrors("gameDescription"))
				errorMessages.add(bindingResult.getFieldError("gameDescription").getDefaultMessage());
			if (bindingResult.hasFieldErrors("genre"))
				errorMessages.add(bindingResult.getFieldError("genre").getDefaultMessage());
			if (bindingResult.hasFieldErrors("minPlayers"))
				errorMessages.add(bindingResult.getFieldError("minPlayers").getDefaultMessage());
			if (bindingResult.hasFieldErrors("maxPlayers"))
				errorMessages.add(bindingResult.getFieldError("maxPlayers").getDefaultMessage());
			if (bindingResult.hasFieldErrors("minDuration"))
				errorMessages.add(bindingResult.getFieldError("minDuration").getDefaultMessage());
			if (bindingResult.hasFieldErrors("maxDuration"))
				errorMessages.add(bindingResult.getFieldError("maxDuration").getDefaultMessage());

			// Token CSRF 
			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null)
				model.addAttribute("token", csrfToken.getToken());

			model.addAttribute("hasErrors", true);
			model.addAttribute("allErrors", errorMessages);
			model.addAttribute("game", game);

			return "game_form";
		}

		// saving game and image
		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			game.setGameImage(image);
		} else if (!isNewGame) {
			Game oldGame = gameService.findById(game.getGameId()).get();
			game.setGameImage(oldGame.getGameImage());
		}

		gameService.save(game);
		return "redirect:/games";
	}

	@PostMapping("/removeGame/{id}")
	public String removeGame(@PathVariable long id, HttpServletRequest request) {

		if (request.isUserInRole("ADMIN")) {
			gameService.delete(id);
		}
		return "redirect:/games";
	}

}