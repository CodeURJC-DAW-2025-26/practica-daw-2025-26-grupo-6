package com.grupo6daw.lcdd_daw.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.service.EventService;
import com.grupo6daw.lcdd_daw.service.GameService;
import com.grupo6daw.lcdd_daw.service.NewService;

@Controller
public class MainController {

	@Autowired
	private EventService eventService;

	@Autowired
	private GameService gameService;

	@Autowired
	private NewService newService;

	@GetMapping("/")
	public String main(Model model) {
		List<Event> nextEvents = eventService.findByEventEndDateAfter(LocalDateTime.now());
		
		if (!nextEvents.isEmpty()) {
			// Create a list of maps that makes only the first element active
			List<Map<String, Object>> eventsWithActive = new ArrayList<>();
			for (int i = 0; i < nextEvents.size(); i++) {
    			Map<String, Object> m = Map.of("event", nextEvents.get(i), "isActive", i == 0);
    			eventsWithActive.add(m);
			}
			model.addAttribute("nextEvents", eventsWithActive);
		}

		model.addAttribute("noEvents", nextEvents.isEmpty());
		model.addAttribute("moreThanOne", nextEvents.size() > 1);
		model.addAttribute("latestNews", newService.findTop3());
		model.addAttribute("featuredGames", gameService.findTop3());

		return "index";
	}

	@GetMapping("/index")
	public String index() {
		return "redirect:/";
	}
}