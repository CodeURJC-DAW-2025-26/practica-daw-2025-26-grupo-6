package com.grupo6daw.lcdd_daw.dto;

import java.time.LocalDateTime;
import java.util.List;

public record NewDTO(
        Long newId,
        String newName,
        String newDescription,
        ImageDTO newImage,
        String newTag,
        List<EventBasicDTO> newEvents,
        UserBasicDTO newCreator,
        boolean validated,
        LocalDateTime creationDate) {
}