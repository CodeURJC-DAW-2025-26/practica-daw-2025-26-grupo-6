package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
import com.grupo6daw.lcdd_daw.dto.GameMapper;
import com.grupo6daw.lcdd_daw.dto.ImageDTO;
import com.grupo6daw.lcdd_daw.dto.ImageMapper;
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

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private ImageMapper imageMapper;

    @GetMapping("/")
    public Page<GameDTO> findAll(Pageable pageable) {

        return gameService.findAll(pageable).map(gameMapper::toDTO);
    }

    @GetMapping("/{id}")
    public GameDTO getGame(@PathVariable long id) {

        return gameMapper.toDTO(gameService.findById(id));
    }

    @PostMapping("/")
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) {

        Game game = gameMapper.toDomain(gameDTO);
        gameService.save(game);
        gameDTO = gameMapper.toDTO(game);

        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(gameDTO.gameId()).toUri();

        return ResponseEntity.created(location).body(gameDTO);
    }

    @PostMapping("/{id}/images/")
    public ResponseEntity<ImageDTO> createGameImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        gameService.addImageToGame(id, image);

        URI location = fromCurrentContextPath()
                .path("/api/images/{imageId}/media")
                .buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    @DeleteMapping("/{id}")
    public GameDTO deleteGame(@PathVariable long id) {

        return gameMapper.toDTO(gameService.delete(id));
    }

    @DeleteMapping("/{gameId}/images/{imageId}")
    public ImageDTO deleteGameImage(@PathVariable long gameId, @PathVariable long imageId)
            throws IOException {

        Image image = imageService.getImage(imageId);
        gameService.removeImageFromGame(gameId, imageId);
        imageService.deleteImage(imageId);

        return imageMapper.toDTO(image);
    }

    @PutMapping("/{id}")
    public GameDTO replaceGame(@PathVariable long id, @RequestBody GameDTO updatedGameDTO) throws SQLException {

        Game updatedGame = gameMapper.toDomain(updatedGameDTO);

        updatedGame.setGameId(id);
        updatedGame.setGameImage(gameService.findById(id).getGameImage());
        gameService.save(updatedGame);

        return gameMapper.toDTO(updatedGame);
    }

    @PutMapping("/{id}/images/")
    public ResponseEntity<ImageDTO> updateGameImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
            throws IOException {

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Image image = imageService.createImage(imageFile.getInputStream());
        gameService.addImageToGame(id, image);

        URI location = fromCurrentContextPath().path("/api/images/{imageId}/media").buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }
}
