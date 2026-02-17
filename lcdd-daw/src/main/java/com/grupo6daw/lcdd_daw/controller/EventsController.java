package com.grupo6daw.lcdd_daw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventsController {
	
	@GetMapping("/events")
	public String events() {
		return "events";
	}

  @GetMapping("/detail_event_page")
	public String detail_event_page() {
		return "detail_event_page";
	}

  @GetMapping("/event_form")
	public String event_form() {
		return "event_form";
	}
}