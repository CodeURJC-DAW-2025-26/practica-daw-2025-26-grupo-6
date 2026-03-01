package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.EventService;
import com.grupo6daw.lcdd_daw.service.ImageService;
import com.grupo6daw.lcdd_daw.service.ImageValidationService;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class EventsController {

	@ControllerAdvice
	public class GlobalExceptionHandler {

		@ExceptionHandler(MaxUploadSizeExceededException.class)
		public String handleMaxSizeException(MaxUploadSizeExceededException exc,
				RedirectAttributes redirectAttributes) {
			redirectAttributes.addFlashAttribute("hasErrors", true);
			redirectAttributes.addFlashAttribute("allErrors",
					List.of("El archivo es demasiado grande o el formato es incompatible. El límite configurado es 10MB."));
			return "redirect:/event_form";
		}
	}

	@Autowired
	private EventService eventService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserService userService;

	@Autowired
	private ImageValidationService imageValidationService;

	@GetMapping("/events")
	public String events(Model model,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String tag,
			@RequestParam(defaultValue = "0") int page) {

		Page<Event> eventsPage = eventService.findValidatedByFilter(name, tag,
				PageRequest.of(page, 10, Sort.by("eventId").descending()));

		model.addAttribute("event", eventsPage.getContent());
		model.addAttribute("name", name == null ? "" : name);
		model.addAttribute("tag", tag == null ? "" : tag);
		model.addAttribute("hasNext", eventsPage.hasNext());
		model.addAttribute("nextPage", page + 1);

		return "events";
	}

	@GetMapping("/event/{id}")
	public String eventDetail(@PathVariable long id, Model model, HttpServletRequest request) {
		Optional<Event> event = eventService.findById(id);
		if (event.isPresent()) {
			model.addAttribute("event", event.get());

			boolean hasEditPermission = false;
			if (request.getUserPrincipal() != null) {
				Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
				boolean isOwner = event.get().getEventCreator() != null
						&& event.get().getEventCreator().getUserId().equals(currentUserId);
				boolean isAdmin = request.isUserInRole("ADMIN");
				hasEditPermission = isOwner || isAdmin;
			}

			// Provide formatted date for the view
			model.addAttribute("formattedDate", event.get().getFormattedDate());

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

		// Initialize default values for the new event
		Event event = new Event();
		event.setEventName("");
		event.setEventDescription("");
		event.setEventDate(LocalDate.now().plusDays(1)); // Default to tomorrow
		event.setEventTag("");
		event.setLink("");
		event.setRequiresRegistration(false);

		model.addAttribute("event", event);
		model.addAttribute("currentDate", LocalDate.now().toString());
		model.addAttribute("hasErrors", false);
		model.addAttribute("allErrors", new ArrayList<String>());

		return "event_form";
	}

	@GetMapping("/event_form/{id}")
	public String editEventForm(@PathVariable long id, HttpServletRequest request, Model model) {
		Optional<Event> event = eventService.findById(id);

		if (event.isPresent()) {
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

			model.addAttribute("currentDate", LocalDate.now().toString());
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

		Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
		User currentUser = userService.getUser(currentUserId).orElseThrow();

		// Check basic validation errors (@Valid)
		if (bindingResult.hasFieldErrors("eventName")) {
			errorMessages.add(bindingResult.getFieldError("eventName").getDefaultMessage());
		}
		if (bindingResult.hasFieldErrors("eventDescription")) {
			errorMessages.add(bindingResult.getFieldError("eventDescription").getDefaultMessage());
		}
		if (bindingResult.hasFieldErrors("eventDate")) {
			errorMessages.add(bindingResult.getFieldError("eventDate").getDefaultMessage());
		}

		// Custom date validation
		if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDate.now())) {
			errorMessages.add("La fecha del evento no puede ser anterior a hoy.");
		}

		// Registration requirements and link validation
		if (event.isRequiresRegistration()) {
			if (event.getLink() == null || event.getLink().trim().isEmpty()) {
				errorMessages.add("El enlace de registro es obligatorio si el evento requiere inscripción.");
			}
		} else {
			// Ignore any link provided and set it to null if registration is not required
			event.setLink(null);
		}

		// Image validation
		imageValidationService.validate(imageField, errorMessages, isNewEvent);

		// If there are errors, return to the form view
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

		// Ownership check for editing existing events
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

		// Handle tags
		if (event.getEventTag() == null) {
			event.setEventTag("");
		}

		// Image handling: save new image or keep existing one
		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			event.setEventImage(image);
		} else if (!isNewEvent) {
			Event oldEvent = eventService.findById(event.getEventId()).get();
			event.setEventImage(oldEvent.getEventImage());
		}

		// Save event to the database
		eventService.save(event);

		// Assign the event to the user's list if it is newly created
		if (isNewEvent) {
			currentUser.getUserOwnEvents().add(event);
			userService.save(currentUser);
		}

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
				Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
				isOwner = e.getEventCreator() != null && e.getEventCreator().getUserId().equals(currentUserId);
			}

			// Ensure only authorized users can delete the event
			if (isAdmin || isOwner) {
				eventService.delete(id);
			}
		}
		return "redirect:/events";
	}
}