package com.grupo6daw.lcdd_daw.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.dto.GameDTO;
import com.grupo6daw.lcdd_daw.dto.GameMapper;
import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.repository.GameRepository;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

import org.springframework.data.domain.Sort;

import org.springframework.transaction.annotation.Transactional;

import com.grupo6daw.lcdd_daw.model.User;
import java.util.Set;

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

    public Page<GameDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    public Page<Game> findByFilter(String name, String tag, Integer players, Integer duration, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("gameId").descending());

        return repository.findByNameAndTagAndPlayersAndDuration(name, tag, players, duration, sortedPageable);
    }

    public Game save(Game game) {
        repository.save(game);
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

    public Game removeImageFromGame(long gameId) {
        Game game = repository.findById(gameId).orElseThrow();
        game.setGameImage(null);
        repository.save(game);

        return game;
    }

    private GameDTO toDTO(Game game) {
        return mapper.toDTO(game);
    }

    private Game toDomain(GameDTO gameDTO) {
        return mapper.toDomain(gameDTO);
    }
}
