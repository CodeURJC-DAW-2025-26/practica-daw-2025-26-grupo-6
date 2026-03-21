package com.grupo6daw.lcdd_daw.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.grupo6daw.lcdd_daw.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDTO toDTO(Event event);

    List<EventDTO> toDTOs(Collection<Event> events);

    Event toDomain(EventDTO eventDTO);
}
