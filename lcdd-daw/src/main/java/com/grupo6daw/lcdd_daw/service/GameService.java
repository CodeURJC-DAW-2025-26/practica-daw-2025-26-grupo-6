package com.grupo6daw.lcdd_daw.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.repository.GameRepository;
import org.springframework.data.domain.Sort;

import org.springframework.transaction.annotation.Transactional;

import com.grupo6daw.lcdd_daw.model.User;
import java.util.Set;

@Service
public class GameService {

	@Autowired
	private GameRepository repository;

	@Autowired
	private UserService userService;

	public Optional<Game> findById(long id) {
		return repository.findById(id);
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

	public Page<Game> findByFilter(String name, String tag, Integer players, Integer duration, Pageable pageable) {
		Pageable sortedPageable = PageRequest.of(
				pageable.getPageNumber(),
				pageable.getPageSize(),
				Sort.by("gameId").descending());

		return repository.findByNameAndTagAndPlayersAndDuration(name, tag, players, duration, sortedPageable);
	}

	public void save(Game game) {
		repository.save(game);
	}

	@Transactional
	public void delete(long id) {
		Optional<Game> gameOpt = repository.findById(id);

		if (gameOpt.isPresent()) {
			Game game = gameOpt.get();

		
			Set<User> users = new HashSet<>(game.getFavoritedByUsers());

	
			for (User user : users) {
				user.removeFavoriteGame(game);
				userService.save(user); 
			}

			repository.delete(game);
		}
	}

	public List<Game> findTop3() {
		return repository.findTop3ByOrderByGameIdDesc();
	}
}
