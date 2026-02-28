package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.UserService;
import com.grupo6daw.lcdd_daw.service.EventService;
import com.grupo6daw.lcdd_daw.service.ImageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserService userService;

	@GetMapping("/events")
	public String events(Model model,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String tag) {

		model.addAttribute("event", eventService.findValidatedByFilter(name, tag));
		model.addAttribute("name", name == null ? "" : name);
		model.addAttribute("tag", tag == null ? "" : tag);
		return "events";
	}

	@GetMapping("/event/{id}")
	public String eventDetail(@PathVariable long id, Model model, HttpServletRequest request) {
		Optional<Event> event = eventService.findById(id);
		if (event.isPresent()) {
			model.addAttribute("event", event.get());

			boolean hasEditPermission = false;
			if (request.getUserPrincipal() != null) {
				Long currentUserId = Long.parseLong(request.getUserPrincipal().getName());
				boolean isOwner = event.get().getEventCreator() != null
						&& event.get().getEventCreator().getUserId().equals(currentUserId);
				boolean isAdmin = request.isUserInRole("ADMIN");
				hasEditPermission = isOwner || isAdmin;
			}
			model.addAttribute("hasEditPermission", hasEditPermission);
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

		return "event_form";
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
	public String saveEvent(Model model, @Valid Event event, BindingResult bindingResult,
			@RequestParam("imageField") MultipartFile imageField, HttpServletRequest request) throws IOException {

		List<String> errorMessages = new ArrayList<>();
		boolean isNewEvent = (event.getEventId() == null);

		Long currentUserId = Long.parseLong(request.getUserPrincipal().getName());
		User currentUser = userService.getUser(currentUserId).orElseThrow();

		// Error check (@Valid)
		if (bindingResult.hasFieldErrors("eventName")) {
			errorMessages.add(bindingResult.getFieldError("eventName").getDefaultMessage());
		}
		if (bindingResult.hasFieldErrors("eventDescription")) {
			errorMessages.add(bindingResult.getFieldError("eventDescription").getDefaultMessage());
		}

		// Checking the registration requirement and link)
		if (event.isRequiresRegistration()) {
			if (event.getLink() == null || event.getLink().trim().isEmpty()) {
				errorMessages.add("El enlace de registro es obligatorio si el evento requiere inscripción.");
			}
		} else {
			// if the event doesn't require registration, we ignore any link provided and set it to null
			event.setLink(null);
		}

		// Image validation (only for new events, for existing ones we allow them to keep their old image if they don't upload a new one)
		if (isNewEvent && imageField.isEmpty()) {
			errorMessages.add("Debes adjuntar una imagen para el evento.");
		}

		// if the image is provided, we check if it's a valid image file (by checking the content type)
		if (!errorMessages.isEmpty()) {
			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null) {
				model.addAttribute("token", csrfToken.getToken());
			}
			model.addAttribute("hasErrors", true);
			model.addAttribute("allErrors", errorMessages);
			model.addAttribute("event", event);
			return "event_form";
		}

		// OWNER CHECK (if it's a new event, we set the owner to the current user, if it's an existing event, we check if the current user is the owner or an admin, if not, we redirect to the events page with an error)
		if (!isNewEvent) {
			Event existingEvent = eventService.findById(event.getEventId()).get();
			boolean isOwner = existingEvent.getEventCreator() != null
					&& existingEvent.getEventCreator().getUserId().equals(currentUserId);
			boolean isAdmin = request.isUserInRole("ADMIN");

			if (!isOwner && !isAdmin) {
				return "redirect:/events?error=unauthorized";
			}

			event.setEventCreator(existingEvent.getEventCreator());
		} else {
			event.setEventCreator(currentUser);
		}

		// IMAGE HANDLING (if a new image is uploaded, we create a new Image entity and set it to the event, if it's an existing event and no new image is uploaded, we keep the old image)
		if (event.getEventTag() == null) {
			event.setEventTag("");
		}

		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			event.setEventImage(image);
		} else if (!isNewEvent) {
			Event oldEvent = eventService.findById(event.getEventId()).get();
			event.setEventImage(oldEvent.getEventImage());
		}

		// Finally, we save the event
		eventService.save(event);

		return "redirect:/events";
	}

	@PostMapping("/removeEvent/{id}")
	public String removeEvent(@PathVariable long id, HttpServletRequest request) {
		Optional<Event> event = eventService.findById(id);

		if (event.isPresent()) {
			Event e = event.get();
			boolean isAdmin = request.isUserInRole("ADMIN");
			boolean isOwner = false;

			if (request.getUserPrincipal() != null) {
				Long currentUserId = Long.parseLong(request.getUserPrincipal().getName());
				isOwner = e.getEventCreator() != null && e.getEventCreator().getUserId().equals(currentUserId);
			}

			if (isAdmin || isOwner) {
				eventService.delete(id);
			}
		}
		return "redirect:/events";
	}
}