package com.grupo6daw.lcdd_daw.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public EventDTO addParticipant(long eventId, long userId) {

        Event event = repository.findById(eventId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        if (!event.getEventRegisteredUsers().contains(user)) {
            event.getEventRegisteredUsers().add(user);
            user.getUserRegisteredEvents().add(event);
            repository.save(event);
            userRepository.save(user);
        }

        EventDTO dto = mapper.toFullDTO(event);

        boolean isCreator = event.getEventCreator() != null
                && event.getEventCreator().getUserId().equals(userId);

        boolean isAdmin = user.getUserRoles().contains("ADMIN");

        if (!isCreator && !isAdmin) {
            dto = new EventDTO(
                    dto.eventId(),
                    dto.eventName(),
                    dto.eventDescription(),
                    dto.eventImage(),
                    dto.eventTag(),
                    dto.requiresRegistration(),
                    dto.link(),
                    dto.eventCreator(),
                    dto.eventNews(),
                    dto.creationDate(),
                    dto.eventDate(),
                    dto.maxParticipants(),
                    dto.validated(),
                    List.of()
            );
        }

        return dto;
    }

    @Transactional
    public Event delete(long id) {
        Event eventOpt = repository.findById(id).orElseThrow();

        if (eventOpt != null) {
            Event event = eventOpt;

            // Initialize lazy associations required by EventDTO mapping after transaction ends
            event.getEventNews().size();
            event.getEventRegisteredUsers().size();
            if (event.getEventImage() != null) {
                event.getEventImage().getId();
            }

            User creator = event.getEventCreator();
            if (creator != null) {
                creator.getUserId();
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

    public Page<Event> findAll(Pageable pageable) {
        return repository.findAll(pageable);
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

    public List<Event> findTop3UpcomingValidatedEvents(LocalDate date) {
        return repository.findTop3ByValidatedTrueAndEventDateGreaterThanEqualOrderByEventDateAsc(date);
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
        return mapper.toFullDTO(event);
    }

    private Event toDomain(EventDTO eventDTO) {
        return mapper.toDomainFromFullDTO(eventDTO);
    }
}
