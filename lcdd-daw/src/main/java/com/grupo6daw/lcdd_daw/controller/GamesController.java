package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.service.GameService;
import com.grupo6daw.lcdd_daw.service.ImageService;

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
	public String game_form() {
		return "game_form";
	}

	@PostMapping("/game_form")
	public String newGameProcess(Model model, Game game, MultipartFile imageField) throws IOException {
		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			game.setGameImage(image);
		}

		gameService.save(game);

		return "redirect:games";
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