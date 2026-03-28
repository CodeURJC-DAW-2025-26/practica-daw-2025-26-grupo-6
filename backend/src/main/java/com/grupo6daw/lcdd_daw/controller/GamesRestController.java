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

import com.grupo6daw.lcdd_daw.dto.GameDTO;
import com.grupo6daw.lcdd_daw.dto.ImageDTO;
import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.service.GameService;
import com.grupo6daw.lcdd_daw.service.ImageService;

@RestController
@RequestMapping("/api/v1/games")
public class GamesRestController {

    @Autowired
    private GameService gameService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/")
    public Page<GameDTO> findByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Integer players,
            @RequestParam(required = false) Integer duration,
            Pageable pageable) {

        return gameService.findByFilter(name, tag, players, duration, pageable)
                .map(game -> gameService.toDTO(game));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGame(
            @PathVariable long id) {

        Game game = gameService.findById(id);

        return ResponseEntity.ok(gameService.toDTO(game));
    }

    @PostMapping("/")
    public ResponseEntity<GameDTO> createGame(
            @RequestBody GameDTO gameDTO,
            Authentication authentication) {

        Game game = gameService.toDomain(gameDTO);
        gameService.save(game);

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(game.getGameId()).toUri();

        return ResponseEntity.created(location)
                .body(gameService.toDTO(game));
    }

    @PostMapping("/{id}/images/")
    public ResponseEntity<ImageDTO> createGameImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile) throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        gameService.addImageToGame(id, image);

        URI location = fromCurrentContextPath()
                .path("/api/images/{imageId}/media")
                .buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(new ImageDTO(image.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GameDTO> deleteGame(
            @PathVariable long id,
            Authentication authentication) {

        Game deleted = gameService.delete(id);

        return ResponseEntity.ok(gameService.toDTO(deleted));
    }

    @DeleteMapping("/{gameId}/images/{imageId}")
    public ResponseEntity<ImageDTO> deleteGameImage(
            @PathVariable long gameId,
            @PathVariable long imageId) throws IOException {

        Image image = imageService.getImage(imageId);

        gameService.removeImageFromGame(gameId, imageId);
        imageService.deleteImage(imageId);

        return ResponseEntity.ok(new ImageDTO(imageId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> replaceGame(
            @PathVariable long id,
            @RequestBody GameDTO updatedGameDTO,
            Authentication authentication) throws SQLException {

        Game updatedGame = gameService.toDomain(updatedGameDTO);
        updatedGame.setGameId(id);
        updatedGame.setGameImage(gameService.findById(id).getGameImage());

        gameService.save(updatedGame);

        return ResponseEntity.ok(gameService.toDTO(updatedGame));
    }

    @PutMapping("/{id}/images/")
    public ResponseEntity<ImageDTO> updateGameImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile) throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        gameService.addImageToGame(id, image);

        URI location = fromCurrentContextPath()
                .path("/api/images/{imageId}/media")
                .buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(new ImageDTO(image.getId()));
    }

    @PostMapping("/{id}/favourites")
    public ResponseEntity<GameDTO> addFavorite(
            @PathVariable long id,
            Authentication authentication) {

        if (authentication == null) {
            throw new AccessDeniedException("Debes iniciar sesión para añadir favoritos");
        }

        long userId = Long.parseLong(authentication.getName());

        Game game = gameService.addFavorite(id, userId);

        return ResponseEntity.ok(gameService.toDTO(game));
    }

    @DeleteMapping("/{id}/favourites")
    public ResponseEntity<GameDTO> removeFavorite(
            @PathVariable long id,
            Authentication authentication) {

        if (authentication == null) {
            throw new AccessDeniedException("Debes iniciar sesión para eliminar favoritos");
        }

        long userId = Long.parseLong(authentication.getName());

        Game game = gameService.removeFavorite(id, userId);

        return ResponseEntity.ok(gameService.toDTO(game));
    }

}
