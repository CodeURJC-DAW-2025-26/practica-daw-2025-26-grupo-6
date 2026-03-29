package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.grupo6daw.lcdd_daw.dto.EventDTO;
import com.grupo6daw.lcdd_daw.dto.ImageDTO;
import com.grupo6daw.lcdd_daw.dto.ImageMapper;
import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.UserRepository;
import com.grupo6daw.lcdd_daw.service.EventService;
import com.grupo6daw.lcdd_daw.service.ImageService;

@RestController
@RequestMapping("/api/v1/events")
public class EventsRestController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Page<?> findByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tag,
            Pageable pageable,
            Authentication authentication) {

        User logged = (authentication != null)
                ? userRepository.findById(Long.parseLong(authentication.getName())).orElse(null)
                : null;

        return eventService.findValidatedByFilter(name, tag, pageable)
                .map(event -> eventService.toDTO(event, logged));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable long id,
            Authentication authentication) {

        User logged = (authentication != null)
                ? userRepository.findById(Long.parseLong(authentication.getName())).orElse(null)
                : null;

        Event event = eventService.findById(id);

        return ResponseEntity.ok(eventService.toDTO(event, logged));
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDTO,
            Authentication authentication) {

        if (authentication == null) {
            throw new SecurityException("Must be authenticated");
        }

        long userId = Long.parseLong(authentication.getName());

        Event event = eventService.toDomain(eventDTO);
        eventService.save(event, userId);

        User logged = userRepository.findById(userId).orElse(null);

        URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(event.getEventId())
                .toUri();

        return ResponseEntity.created(location)
                .body(eventService.toDTO(event, logged));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ImageDTO> createEventImage(@PathVariable long id,
            @RequestParam MultipartFile imageFile,
            Authentication authentication) throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        eventService.addImageToEvent(id, image, Long.parseLong(authentication.getName()));

        URI location = fromCurrentContextPath().path("/api/images/{imageId}/media").buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EventDTO> deleteEvent(@PathVariable long id,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        Event deleted = eventService.deleteAuthorized(id, userId);

        if (deleted == null) {
            throw new AccessDeniedException("No tienes permiso para borrar este evento");
        }

        return ResponseEntity.ok(eventService.toDTO(deleted));
    }

    @DeleteMapping("/{eventId}/images/{imageId}")
    public ResponseEntity<ImageDTO> deleteEventImage(@PathVariable long eventId, @PathVariable long imageId,
            Authentication authentication)
            throws IOException {

        Image image = imageService.getImage(imageId);
        eventService.removeImageFromEvent(eventId, imageId, Long.parseLong(authentication.getName()));
        imageService.deleteImage(imageId);

        return ResponseEntity.ok(imageMapper.toDTO(image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceEvent(
            @PathVariable long id,
            @RequestBody EventDTO updatedEventDTO,
            Authentication authentication) throws SQLException {

        Long userId = Long.parseLong(authentication.getName());
        Event updatedEvent = eventService.toDomain(updatedEventDTO);

        updatedEvent.setEventId(id);

        Event saved = eventService.save(updatedEvent, userId);
        if (saved == null) {
            throw new AccessDeniedException("No tienes permiso para editar este evento");
        }

        return ResponseEntity.ok(eventService.toDTO(saved));
    }

    @PutMapping("/{id}/images")
    public ResponseEntity<ImageDTO> updateEventImage(@PathVariable long id, @RequestParam MultipartFile imageFile,
            Authentication authentication)
            throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        eventService.addImageToEvent(id, image, Long.parseLong(authentication.getName()));

        URI location = fromCurrentContextPath().path("/api/images/{imageId}/media").buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    @PostMapping("/{eventId}/participants")
    public EventDTO joinEvent(@PathVariable long eventId,
            Authentication authentication) {

        if (authentication == null) {
            throw new SecurityException("Must be authenticated");
        }

        long userId = Long.parseLong(authentication.getName());

        return eventService.addParticipant(eventId, userId);
    }

    @DeleteMapping("/{eventId}/participants")
    public ResponseEntity<?> leaveEvent(@PathVariable long eventId,
            Authentication authentication) {

        if (authentication == null) {
            throw new SecurityException("Must be authenticated");
        }

        long userId = Long.parseLong(authentication.getName());

        User logged = userRepository.findById(userId).orElse(null);

        Event updated = eventService.removeParticipant(eventId, userId);

        return ResponseEntity.ok(eventService.toDTO(updated, logged));
    }

}
