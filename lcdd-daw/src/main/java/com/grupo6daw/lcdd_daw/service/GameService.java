package com.grupo6daw.lcdd_daw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.repository.GameRepository;

@Service
public class GameService {
  
	@Autowired
	private GameRepository repository;

	public Optional<Game> findById(long id) {
		return repository.findById(id);
	}

	public List<Game> findById(List<Long> ids){
		return repository.findAllById(ids);
	}
	
	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<Game> findAll() {
		return repository.findAll();
	}

	public List<Game> findByFilter(String name, String tag, Integer players, Integer duration) {
		return repository.findByNameAndTagAndPlayersAndDuration(name, tag, players, duration);
	}

	public void save(Game game) {
		repository.save(game);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}
}
