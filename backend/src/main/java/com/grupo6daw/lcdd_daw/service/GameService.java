package com.grupo6daw.lcdd_daw.service;

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

    public GameDTO toDTO(Game game) {
        return mapper.toDTO(game);
    }

    public Game save(Game game) {
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

    public Game toDomain(GameDTO gameDTO) {
        return mapper.toDomain(gameDTO);
    }
}
