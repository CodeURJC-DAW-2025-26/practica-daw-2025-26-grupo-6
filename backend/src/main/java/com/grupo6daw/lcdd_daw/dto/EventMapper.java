package com.grupo6daw.lcdd_daw.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.grupo6daw.lcdd_daw.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // BASIC DTO
    @Named("toBasicDTO")
    EventBasicDTO toBasicDTO(Event event);

    @IterableMapping(qualifiedByName = "toBasicDTO")
    List<EventBasicDTO> toBasicDTOs(Collection<Event> events);

    @Mapping(target = "eventDescription", ignore = true)
    @Mapping(target = "eventImage", ignore = true)
    @Mapping(target = "eventTag", ignore = true)
    @Mapping(target = "requiresRegistration", ignore = true)
    @Mapping(target = "link", ignore = true)
    @Mapping(target = "eventRegisteredUsers", ignore = true)
    @Mapping(target = "eventCreator", ignore = true)
    @Mapping(target = "eventNews", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    @Mapping(target = "maxParticipants", ignore = true)
    @Mapping(target = "validated", ignore = true)
    Event toDomainFromBasicDTO(EventBasicDTO eventDTO);

    // FULL DTO
    @Named("toFullDTO")
    @Mapping(target = "participants", source = "eventRegisteredUsers")
    EventDTO toFullDTO(Event event);


    @IterableMapping(qualifiedByName = "toFullDTO")
    List<EventDTO> toFullDTOs(Collection<Event> events);

    Event toDomainFromFullDTO(EventDTO eventDTO);
}
