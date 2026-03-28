package com.grupo6daw.lcdd_daw.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo6daw.lcdd_daw.dto.GameDTO;
import com.grupo6daw.lcdd_daw.dto.GameMapper;
import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.GameRepository;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

@Service
public class GameService {

    @Autowired
    private GameRepository repository;

    @Autowired
    private GameMapper mapper;

    @Autowired
    private UserRepository userRepository;

    public Game findById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public List<Game> findById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    public boolean exist(long id) {
        return repository.existsById(id);
    }

    public List<Game> findAll() {
        return repository.findAll();
    }

    public Page<Game> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Game> findByFilter(String name, String tag, Integer players, Integer duration, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("gameId").descending());

        return repository.findByNameAndTagAndPlayersAndDuration(name, tag, players, duration, sortedPageable);
    }

    public List<String> validateContent(Game newGame) {
        final int MIN_NAME_LENGTH = 2;
        final int MAX_NAME_LENGTH = 100;
        final int MIN_DESCRIPTION_LENGTH = 10;
        final int MAX_DESCRIPTION_LENGTH = 3000;
        final Long MIN_PLAYERS = 1L;
        final Long MIN_DURATION = 1L;

        if (newGame == null) {
            return List.of("El juego es obligatorio");
        }

        List<String> errors = new ArrayList<>();

        String name = newGame.getGameName();
        if (name == null || name.isBlank()) {
            errors.add("El nombre del juego es obligatorio");
        } else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            errors.add("El nombre del juego debe tener entre 2 y 100 caracteres");
        }

        String description = newGame.getGameDescription();
        if (description == null || description.isBlank()) {
            errors.add("La descripción del juego no puede estar vacía");
        } else if (description.length() < MIN_DESCRIPTION_LENGTH || description.length() > MAX_DESCRIPTION_LENGTH) {
            errors.add("La descripción del juego debe tener entre 10 y 3000 caracteres");
        }

        String genre = newGame.getGenre();
        if (genre == null || genre.isBlank()) {
            errors.add("La categoría del juego es obligatoria");
        }

        Long minPlayers = newGame.getMinPlayers();
        if (minPlayers == null || minPlayers < MIN_PLAYERS) {
            errors.add("El número de jugadores mínimo no puede estar vacío y ha de ser mayor de 0");
        }

        Long maxPlayers = newGame.getMaxPlayers();
        if (maxPlayers == null || maxPlayers < MIN_PLAYERS) {
            errors.add("El número de jugadores máximos no puede estar vacío y ha de ser mayor de 0");
        }

        if (minPlayers > maxPlayers) {
            errors.add("El número mínimo de jugadores no puede ser mayor que el número máximo de jugadores");
        }

        Long minDuration = newGame.getMinDuration();
        if (minDuration == null || minDuration < MIN_DURATION) {
            errors.add("La duración mínima no puede estar vacía y ha de ser mayor de 0");
        }

        Long maxDuration = newGame.getMaxDuration();
        if (maxDuration == null || maxDuration < MIN_DURATION) {
            errors.add("La duración máxima no puede estar vacía y ha de ser mayor de 0");
        }

        if (minDuration > maxDuration) {
            errors.add("La duración mínima de partida no puede ser mayor que la duración máxima de partida");
        }

        return errors;
    }

    public Game save(Game game) {
        List<String> errors = validateContent(game);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" | ", errors));
        }
        boolean isNewGame = (game.getGameId() == null);

        if (!isNewGame) {
            Game existingGame = findById(game.getGameId());
            if (game.getGameImage() == null) {
                game.setGameImage(existingGame.getGameImage());
            }
        }

        repository.save(game);
        return game;
    }

    @Transactional
    public Game addFavorite(long gameId, long userId) {
        Game game = repository.findById(gameId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        user.addFavoriteGame(game);
        userRepository.save(user);

        return game;
    }

    @Transactional
    public Game removeFavorite(long gameId, long userId) {
        Game game = repository.findById(gameId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        user.removeFavoriteGame(game);
        userRepository.save(user);

        return game;
    }

    @Transactional
    public Game delete(long id) {
        Game game = repository.findById(id).orElseThrow();

        if (game != null) {
            Set<User> users = new HashSet<>(game.getFavoritedByUsers());
            for (User user : users) {
                user.removeFavoriteGame(game);
                userRepository.save(user);
            }
            repository.delete(game);
        }
        return game;
    }

    public List<Game> findTop3() {
        return repository.findTop3ByOrderByGameIdDesc();
    }

    public Game addImageToGame(long id, Image image) {
        Game game = repository.findById(id).orElseThrow();
        game.setGameImage(image);
        repository.save(game);

        return game;
    }

    public Game removeImageFromGame(long gameId, long imageId) {

        Game game = repository.findById(gameId).orElseThrow();

        if (game.getGameImage() == null || game.getGameImage().getId() != imageId) {
            throw new IllegalArgumentException("Image does not belong to this game");
        }

        game.setGameImage(null);
        repository.save(game);

        return game;
    }

    public GameDTO toDTO(Game game) {
        return mapper.toDTO(game);
    }

    public Game toDomain(GameDTO gameDTO) {
        return mapper.toDomain(gameDTO);
    }
}
