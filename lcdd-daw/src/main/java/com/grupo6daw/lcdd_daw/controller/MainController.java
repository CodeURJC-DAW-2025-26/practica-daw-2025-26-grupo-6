package com.grupo6daw.lcdd_daw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.grupo6daw.lcdd_daw.service.GameService;
import com.grupo6daw.lcdd_daw.service.NewService;

@Controller
public class MainController {

	@Autowired
	private GameService gameService;

	@Autowired
	private NewService newService;

	@GetMapping("/")
	public String main(Model model) {

		model.addAttribute("latestNews", newService.findTop3());
		model.addAttribute("featuredGames", gameService.findTop3());

		return "index";
	}

	@GetMapping("/index")
	public String index() {
		return "redirect:/";
	}
}