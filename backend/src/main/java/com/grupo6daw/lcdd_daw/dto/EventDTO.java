package com.grupo6daw.lcdd_daw.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record EventDTO(
        Long eventId,
        String eventName,
        String eventDescription,
        ImageDTO eventImage,
        String eventTag,
        boolean requiresRegistration,
        String link,
        UserBasicDTO eventCreator,
        List<NewBasicDTO> eventNews,
        LocalDateTime creationDate,
        LocalDate eventDate,
        Integer maxParticipants,
        boolean validated) {
}
