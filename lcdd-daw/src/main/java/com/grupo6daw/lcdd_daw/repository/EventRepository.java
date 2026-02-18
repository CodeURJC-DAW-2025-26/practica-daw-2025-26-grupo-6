package com.grupo6daw.lcdd_daw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo6daw.lcdd_daw.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}

