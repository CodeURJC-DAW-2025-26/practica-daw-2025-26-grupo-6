package com.grupo6daw.lcdd_daw.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.EventRepository;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private UserRepository userRepository;

    public void delete(long id) {
        Optional<Event> eventOpt = repository.findById(id);

        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();

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
    }

    public Optional<Event> findById(long id) {
        return repository.findById(id);
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

    public List<Event> findByFilter(String name, String tag) {
        return repository.findByNameAndTag(name, tag);
    }

    public void save(Event event) {
        repository.save(event);
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

}