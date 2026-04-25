package com.grupo6daw.lcdd_daw.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventPublicDTO(
        Long eventId,
        String eventName,
        String eventDescription,
        ImageDTO eventImage,
        String eventTag,
        boolean requiresRegistration,
        String link,
        UserBasicDTO eventCreator,
        LocalDateTime creationDate,
        LocalDate eventDate,
        Integer maxParticipants,
        Long participantCount,
        boolean validated) {
}
