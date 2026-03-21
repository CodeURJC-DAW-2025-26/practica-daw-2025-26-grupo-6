package com.grupo6daw.lcdd_daw.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.dto.EventDTO;
import com.grupo6daw.lcdd_daw.dto.EventMapper;
import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.EventRepository;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private EventMapper mapper;

    @Autowired
    private UserRepository userRepository;

    public Event delete(long id) {
        Event eventOpt = repository.findById(id).orElseThrow();

        if (eventOpt != null) {
            Event event = eventOpt;

            User creator = event.getEventCreator();
            if (creator != null) {
                creator.getUserOwnEvents().remove(event);
                userRepository.save(creator);
            }

            for (User participant : event.getEventRegisteredUsers()) {
                participant.getUserRegisteredEvents().remove(event);
                userRepository.save(participant);
            }

            repository.deleteById(id);
        }
        return eventOpt;
    }

    public Event findById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Event> findById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    public boolean exist(long id) {
        return repository.existsById(id);
    }

    public List<Event> findAll() {
        return repository.findAll();
    }

    public Page<EventDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    public List<Event> findByFilter(String name, String tag) {
        return repository.findByNameAndTag(name, tag);
    }

    public Event save(Event event) {
        repository.save(event);
        return event;
    }

    public Event saveRest(Event event, Long id, LocalDateTime date) {
        User author = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        event.setEventCreator(author);
        event.setCreationDate(date);

        repository.save(event);
        return event;
    }

    public List<Event> findByValidatedFalse() {
        return repository.findByValidatedFalse();
    }

    public List<Event> findByValidatedTrue() {
        return repository.findByValidatedTrue();
    }

    public List<Event> findUpcomingEvents(LocalDate date) {
        return repository.findByEventDateGreaterThanEqualOrderByEventDateAsc(date);
    }

    public Page<Event> findValidatedByFilter(String name, String tag, Pageable page) {
        return repository.findValidatedByNameAndTag(name, tag, page);
    }

    public List<Event> findValidatedByTag(String tag) {
        return repository.findValidatedByTag(tag);
    }

    public Event addImageToEvent(long id, Image image) {
        Event event = repository.findById(id).orElseThrow();
        event.setEventImage(image);
        repository.save(event);

        return event;
    }

    public Event removeImageFromEvent(long eventId, long imageId) {
        Event event = repository.findById(eventId).orElseThrow();

        if (event.getEventImage() == null || event.getEventImage().getId() != imageId) {
            throw new IllegalArgumentException("Image does not belong to this event");
        }

        event.setEventImage(null);
        repository.save(event);

        return event;
    }

    private EventDTO toDTO(Event event) {
        return mapper.toDTO(event);
    }

    private Event toDomain(EventDTO eventDTO) {
        return mapper.toDomain(eventDTO);
    }
}