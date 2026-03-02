package com.grupo6daw.lcdd_daw.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class ImageValidationService {

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    public void validate(MultipartFile imageField, List<String> errorMessages, boolean isNew) {
        if (imageField != null && !imageField.isEmpty()) {
            // 1. Check Size
            if (imageField.getSize() > MAX_SIZE) {
                errorMessages.add("El archivo es demasiado grande. El límite son 10MB.");
            }

            // 2. Check Format
            String contentType = imageField.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                errorMessages.add("Formato de imagen no permitido. Por favor, usa JPG, PNG o WebP.");
            }
        } else if (isNew) {
            // Only require an image if it's a new game. For edits, it's optional.
            errorMessages.add("Debes adjuntar una imagen.");
        }
    }
}