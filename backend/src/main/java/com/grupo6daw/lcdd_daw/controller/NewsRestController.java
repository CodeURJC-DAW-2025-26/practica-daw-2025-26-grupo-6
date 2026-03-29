package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.grupo6daw.lcdd_daw.dto.ImageDTO;
import com.grupo6daw.lcdd_daw.dto.ImageMapper;
import com.grupo6daw.lcdd_daw.dto.NewDTO;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.service.ImageService;
import com.grupo6daw.lcdd_daw.service.NewService;

@RestController
@RequestMapping("/api/v1/news")
public class NewsRestController {

    @Autowired
    private NewService newService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;

    @GetMapping("/")
    public Page<NewDTO> findByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tag,
            Pageable pageable) {

        return newService.findValidatedByFilter(name, tag, pageable)
                .map(newService::toDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewDTO> getNew(@PathVariable long id) {

        New n = newService.findById(id);
        return ResponseEntity.ok(newService.toDTO(n));
    }

    @PostMapping("/")
    public ResponseEntity<NewDTO> createNew(
            @RequestBody NewDTO newDTO,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        New news = newService.toDomain(newDTO);
        newService.save(news, userId);

        URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(news.getNewId())
                .toUri();

        return ResponseEntity.created(location)
                .body(newService.toDTO(news));
    }

    @PostMapping("/{id}/images/")
    public ResponseEntity<ImageDTO> createNewImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile,
            Authentication authentication) throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        newService.addImageToNew(id, image, Long.parseLong(authentication.getName()));

        URI location = fromCurrentContextPath()
                .path("/api/images/{imageId}/media")
                .buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(imageMapper.toDTO(image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NewDTO> deleteNew(@PathVariable long id,
            Authentication authentication) throws SQLException {

        Long userId = Long.parseLong(authentication.getName());

        New deleted = newService.deleteAuthorized(id, userId);
        if (deleted == null) {
            throw new AccessDeniedException("No tienes permiso para borrar esta noticia");
        }
        return ResponseEntity.ok(newService.toDTO(deleted));
    }

    @DeleteMapping("/{newId}/images/{imageId}")
    public ResponseEntity<ImageDTO> deleteNewImage(@PathVariable long newId, @PathVariable long imageId,
            Authentication authentication)
            throws IOException {

        Image image = imageService.getImage(imageId);

        newService.removeImageFromNew(newId, imageId, Long.parseLong(authentication.getName()));
        imageService.deleteImage(imageId);

        return ResponseEntity.ok(imageMapper.toDTO(image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewDTO> replaceNew(
            @PathVariable long id,
            @RequestBody NewDTO updatedNewDTO,
            Authentication authentication) throws SQLException {

        Long userId = Long.parseLong(authentication.getName());
        New updated = newService.toDomain(updatedNewDTO);

        updated.setNewId(id);

        New saved = newService.save(updated, userId);
        if (saved == null) {
            throw new AccessDeniedException("No tienes permiso para editar esta noticia");
        }

        return ResponseEntity.ok(newService.toDTO(saved));
    }

    @PutMapping("/{id}/images/")
    public ResponseEntity<ImageDTO> updateNewImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile,
            Authentication authentication) throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        newService.addImageToNew(id, image, Long.parseLong(authentication.getName()));

        URI location = fromCurrentContextPath()
                .path("/api/images/{imageId}/media")
                .buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

}
