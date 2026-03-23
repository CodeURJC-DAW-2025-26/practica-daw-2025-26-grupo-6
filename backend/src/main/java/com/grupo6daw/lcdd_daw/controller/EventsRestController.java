package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
import com.grupo6daw.lcdd_daw.dto.EventMapper;
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
    private EventMapper eventMapper;

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

        long userId = Long.parseLong(authentication.getName());
        User logged = userRepository.findById(userId).orElseThrow();

        return eventService.findValidatedByFilter(name, tag, pageable)
                .map(event -> eventService.toDTO(event, logged));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable long id,
            Authentication authentication) {

        long userId = Long.parseLong(authentication.getName());
        User logged = userRepository.findById(userId).orElseThrow();

        Event event = eventService.findById(id);

        return ResponseEntity.ok(eventService.toDTO(event, logged));
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDTO,
            Authentication authentication) {

        long userId = Long.parseLong(authentication.getName());
        LocalDateTime date = LocalDateTime.now();

        Event event = eventMapper.toDomainFromFullDTO(eventDTO);
        eventService.saveRest(event, userId, date);

        User logged = userRepository.findById(userId).orElseThrow();

        URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(event.getEventId())
                .toUri();

        return ResponseEntity.created(location)
                .body(eventService.toDTO(event, logged));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ImageDTO> createEventImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        eventService.addImageToEvent(id, image);

        URI location = fromCurrentContextPath().path("/api/images/{imageId}/media").buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable long id, Authentication authentication) {

        long userId = Long.parseLong(authentication.getName());
        User logged = userRepository.findById(userId).orElseThrow();

        Event deleted = eventService.delete(id);
        return ResponseEntity.ok(eventService.toDTO(deleted, logged));
    }

    @DeleteMapping("/{eventId}/images/{imageId}")
    public ImageDTO deleteEventImage(@PathVariable long eventId, @PathVariable long imageId)
            throws IOException {

        Image image = imageService.getImage(imageId);
        eventService.removeImageFromEvent(eventId, imageId);
        imageService.deleteImage(imageId);

        return imageMapper.toDTO(image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> replaceEvent(@PathVariable long id,
            @RequestBody EventDTO updatedEventDTO,
            Authentication authentication) throws SQLException {

        long userId = Long.parseLong(authentication.getName());
        User logged = userRepository.findById(userId).orElseThrow();

        Event updatedEvent = eventMapper.toDomainFromFullDTO(updatedEventDTO);

        updatedEvent.setEventId(id);
        updatedEvent.setEventImage(eventService.findById(id).getEventImage());
        eventService.save(updatedEvent);

        return ResponseEntity.ok(eventService.toDTO(updatedEvent, logged));
    }

    @PutMapping("/{id}/images")
    public ResponseEntity<ImageDTO> updateEventImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        eventService.addImageToEvent(id, image);

        URI location = fromCurrentContextPath().path("/api/images/{imageId}/media").buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    @PostMapping("/{eventId}/participants")
    public EventDTO joinEvent(
            @PathVariable long eventId,
            Authentication authentication) {

        long userId = Long.parseLong(authentication.getName());
        return eventService.addParticipant(eventId, userId);

    }

    @DeleteMapping("/{eventId}/participants")
    public ResponseEntity<?> leaveEvent(
            @PathVariable long eventId,
            Authentication authentication) {

        long userId = Long.parseLong(authentication.getName());
        User logged = userRepository.findById(userId).orElseThrow();

        Event updated = eventService.removeParticipant(eventId, userId);

        return ResponseEntity.ok(eventService.toDTO(updated, logged));
    }

}
