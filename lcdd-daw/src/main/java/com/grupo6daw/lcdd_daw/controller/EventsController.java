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

import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.service.EventService;
import com.grupo6daw.lcdd_daw.service.ImageService;

@Controller
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private ImageService imageService;

	@GetMapping("/events")
	public String events(Model model) {
		model.addAttribute("event", eventService.findAll());
		return "events";
	}

	@GetMapping("/event/{id}")
	public String eventDetail(@PathVariable long id, Model model) {
		Optional<Event> event = eventService.findById(id);
		if (event.isPresent()) {
			model.addAttribute("event", event.get());
		}
		return "detail_event_page";
	}

	@GetMapping("/event_form")
	public String event_form() {
		return "event_form";
	}

	@PostMapping("/event_form")
	public String newEventProcess(Model model, Event event, MultipartFile imageField) throws IOException {
		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			event.setEventImage(image);
		}

		eventService.save(event);

		return "redirect:/events";
	}

	@PostMapping("/removeEvent/{id}")
	public String removeEvent(Model model, @PathVariable long id) {

		Optional<Event> event = eventService.findById(id);
		if (event.isPresent()) {
			eventService.delete(id);
			model.addAttribute("event", event.get());
		}
		
		return "redirect:/events";
	}
}