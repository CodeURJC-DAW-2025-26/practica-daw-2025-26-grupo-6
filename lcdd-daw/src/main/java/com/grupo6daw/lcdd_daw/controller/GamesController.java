package com.grupo6daw.lcdd_daw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GamesController {
	
	@GetMapping("/games")
	public String games() {
		return "games";
	}

  @GetMapping("/detail_game_page")
	public String detail_game_page() {
		return "detail_game_page";
	}

  @GetMapping("/game_form")
	public String game_form() {
		return "game_form";
	}
}