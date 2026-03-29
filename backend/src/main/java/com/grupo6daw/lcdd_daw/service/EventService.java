package com.grupo6daw.lcdd_daw.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

    public Object toDTO(Event event, User logged) {

        if (logged == null) {
            return mapper.toPublicDTO(event);
        }

        boolean isOwner = event.getEventCreator() != null
                && event.getEventCreator().getUserId().equals(logged.getUserId());

        boolean isAdmin = logged.getUserRoles().contains("ADMIN");

        if (isOwner || isAdmin) {
            return mapper.toFullDTO(event);
        } else {
            return mapper.toPublicDTO(event);
        }
    }

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

        return mapper.toFullDTO(event);
    }

    @Transactional
    public Event removeParticipant(long eventId, long userId) {

        Event event = repository.findById(eventId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        if (event.getEventRegisteredUsers().contains(user)) {

            event.getEventRegisteredUsers().remove(user);
            user.getUserRegisteredEvents().remove(event);

            repository.save(event);
            userRepository.save(user);
        }

        return event;
    }

    @Transactional
    public Event delete(long id) {
        Event eventOpt = repository.findById(id).orElseThrow();

        if (eventOpt != null) {
            Event event = eventOpt;

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

    public Event deleteAuthorized(Long id, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Event newEvent = findById(id);

        if (checkPermissions(newEvent, currentUser, false)) {
            return delete(id);
        }

        return null;
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

    public List<String> validateContent(Event newEvent) {
        final int MIN_NAME_LENGTH = 5;
        final int MAX_NAME_LENGTH = 100;
        final int MIN_DESCRIPTION_LENGTH = 50;
        final int MAX_DESCRIPTION_LENGTH = 3000;

        if (newEvent == null) {
            return List.of("El evento es obligatorio");
        }

        List<String> errors = new ArrayList<>();

        String name = newEvent.getEventName();
        if (name == null || name.isBlank()) {
            errors.add("El nombre/título del evento es obligatorio");
        } else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            errors.add("El título debe tener entre 5 y 100 caracteres");
        }

        String description = newEvent.getEventDescription();
        if (description == null || description.isBlank()) {
            errors.add("La descripción no puede estar vacía");
        } else if (description.length() < MIN_DESCRIPTION_LENGTH || description.length() > MAX_DESCRIPTION_LENGTH) {
            errors.add("La descripción debe tener entre 50 y 3000 caracteres");
        }

        String tag = newEvent.getEventTag();
        if (tag == null || tag.isBlank()) {
            errors.add("La etiqueta del evento es obligatoria");
        }

        LocalDate eventDate = newEvent.getEventDate();
        if (eventDate == null) {
            errors.add("La fecha del evento es obligatoria");
        }

        if (eventDate != null && eventDate.isBefore(LocalDate.now())) {
            errors.add("La fecha del evento no puede ser anterior a hoy.");
        }

        if (newEvent.isRequiresRegistration()) {
            if (newEvent.getLink() == null || newEvent.getLink().trim().isEmpty()) {
                errors.add("El enlace de registro es obligatorio si el evento requiere inscripción.");
            }
        }

        return errors;
    }

    public boolean hasEditPermission(Event newEvent, Long currentUserId, boolean isAdmin) {
        return isAdmin || (newEvent.getEventCreator() != null
                && newEvent.getEventCreator().getUserId().equals(currentUserId));
    }

    public Event save(Event newEvent, Long currentUserId) {
        List<String> errors = validateContent(newEvent);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" | ", errors));
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isNewEvent = (newEvent.getEventId() == null);

        if (!isNewEvent) {
            Event existingEvent = findById(newEvent.getEventId());
            if (newEvent.getEventImage() == null) {
                newEvent.setEventImage(existingEvent.getEventImage());
            }
        }

        if (checkPermissions(newEvent, currentUser, isNewEvent)) {
            Event savedEvent = save(newEvent);

            if (isNewEvent) {
                if (currentUser.getUserOwnEvents() == null) {
                    currentUser.setUserOwnEvents(new ArrayList<>());
                }
                currentUser.getUserOwnEvents().add(savedEvent);
                userRepository.save(currentUser);
            }

            return savedEvent;
        }

        return null;
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

    public Event addImageToEvent(long id, Image image, Long currentUserId) {
        Event event = repository.findById(id).orElseThrow();
        if (checkPermissions(event, userRepository.findById(currentUserId).orElseThrow(),
                (event.getEventId() == null))) {
            event.setEventImage(image);
            repository.save(event);
        } else {
            throw new AccessDeniedException("No tienes permiso para modificar este evento");
        }

        return event;
    }

    public Event removeImageFromEvent(long eventId, long imageId, Long currentUserId) {
        Event event = repository.findById(eventId).orElseThrow();

        if (event.getEventImage() == null || event.getEventImage().getId() != imageId) {
            throw new IllegalArgumentException("Image does not belong to this event");
        }

        if (checkPermissions(event, userRepository.findById(currentUserId).orElseThrow(),
                (event.getEventId() == null))) {
            event.setEventImage(null);
            repository.save(event);
        } else {
            throw new AccessDeniedException("No tienes permiso para eliminar contenido de este evento");
        }

        return event;
    }

    public EventDTO toDTO(Event event) {
        return mapper.toFullDTO(event);
    }

    public Event toDomain(EventDTO eventDTO) {
        return mapper.toDomainFromFullDTO(eventDTO);
    }

    public boolean checkPermissions(Event event, User currentUser, boolean isNewEvent) {
        if (!isNewEvent) {
            Event existingEvent = findById(event.getEventId());
            boolean isOwner = existingEvent.getEventCreator() != null
                    && existingEvent.getEventCreator().getUserId().equals(currentUser.getUserId());
            boolean isAdmin = currentUser.getUserRoles().contains("ADMIN");

            if (!isOwner && !isAdmin) {
                return false;
            }

            event.setEventCreator(existingEvent.getEventCreator());
        } else {
            event.setEventCreator(currentUser);
        }

        return true;
    }
}
