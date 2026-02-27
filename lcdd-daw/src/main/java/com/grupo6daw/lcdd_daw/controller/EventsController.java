package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.util.ArrayList;
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

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private ImageService imageService;

	@GetMapping("/events")
	public String events(Model model,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String tag) {

		model.addAttribute("event", eventService.findByFilter(name, tag));
		model.addAttribute("name", name == null ? "" : name);
		model.addAttribute("tag", tag == null ? "" : tag);
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
	public String showEventForm(HttpServletRequest request, Model model) {

		CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrfToken != null) {
			model.addAttribute("token", csrfToken.getToken());
		}

		Event event = new Event();
		event.setEventName("");
		event.setEventDescription("");
		event.setEventTag("");
		event.setLink("");
		event.setRequiresRegistration(false);

		model.addAttribute("event", event);

		model.addAttribute("hasErrors", false);
		model.addAttribute("allErrors", new ArrayList<String>());

		return "redirect:/event_form";
	}

	@GetMapping("/event_form/{id}")
	public String editEventForm(@PathVariable long id, HttpServletRequest request, Model model) {
		Optional<Event> event = eventService.findById(id);

		if (event.isPresent()) {
			model.addAttribute("event", event.get());
			Event e = event.get();

			if (e.getLink() == null)
				e.setLink("");
			if (e.getEventTag() == null)
				e.setEventTag("");

			model.addAttribute("event", e);

			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null) {
				model.addAttribute("token", csrfToken.getToken());
			}

			model.addAttribute("hasErrors", false);
			model.addAttribute("allErrors", new ArrayList<String>());

			return "event_form";
		}
		return "redirect:/events";
	}

	@PostMapping("/event_form")
	public String saveEvent(HttpServletRequest request,
			@Valid Event event,
			BindingResult bindingResult,
			@RequestParam("imageField") MultipartFile imageField,
			Model model) throws IOException {

		List<String> errorMessages = new ArrayList<>();

		boolean isNewEvent = (event.getEventId() == null);

		if (event.isRequiresRegistration() && (event.getLink() == null || event.getLink().trim().isEmpty())) {
			bindingResult.rejectValue("link", "error.link", "El enlace es obligatorio si activas el registro");
		}

		if (bindingResult.hasErrors() || (isNewEvent && imageField.isEmpty())) {

			if (bindingResult.hasFieldErrors("eventName")) {
				errorMessages.add(bindingResult.getFieldError("eventName").getDefaultMessage());
			}

			if (bindingResult.hasFieldErrors("eventDescription")) {
				errorMessages.add(bindingResult.getFieldError("eventDescription").getDefaultMessage());
			}

			if (isNewEvent && imageField.isEmpty()) {
				errorMessages.add("Por favor, sube una imagen para el evento");
				model.addAttribute("imageError", "La imagen es obligatoria");
			}

			if (event.getLink() == null)
				event.setLink("");
			if (event.getEventTag() == null)
				event.setEventTag("");

			if (bindingResult.hasFieldErrors("link")) {

				errorMessages.add(bindingResult.getFieldError("link").getDefaultMessage());
			}

			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null)
				model.addAttribute("token", csrfToken.getToken());

			model.addAttribute("hasErrors", true);
			model.addAttribute("allErrors", errorMessages);
			model.addAttribute("event", event);

			if (event.getEventTag() == null)
				event.setEventTag("");

			return "event_form";
		}

		if (!imageField.isEmpty()) {
			// If the user uploads a new image, we create it and set it to the event
			Image image = imageService.createImage(imageField.getInputStream());
			event.setEventImage(image);
		} else if (!isNewEvent) {
			// If not new event and no new image uploaded, we keep the old image
			Event oldEvent = eventService.findById(event.getEventId()).get();
			event.setEventImage(oldEvent.getEventImage());
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