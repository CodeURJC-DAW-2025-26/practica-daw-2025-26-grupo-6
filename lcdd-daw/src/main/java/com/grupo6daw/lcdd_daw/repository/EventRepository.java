package com.grupo6daw.lcdd_daw.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6daw.lcdd_daw.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
    SELECT e AS event, COUNT(DISTINCT u) AS participantCount
    FROM Event e
    LEFT JOIN e.eventRegisteredUsers u
    GROUP BY e
    ORDER BY COUNT(DISTINCT u) DESC, e.eventName ASC
""")
    Page<Object[]> findEventsOrderedByParticipants(Pageable pageable);
    Optional<Event> findByEventName(String eventName);

}
