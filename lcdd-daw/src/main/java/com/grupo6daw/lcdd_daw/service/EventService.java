package com.grupo6daw.lcdd_daw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.repository.EventRepository;

@Service
public class EventService {
  
	@Autowired
	private EventRepository repository;

	public Optional<Event> findById(long id) {
		return repository.findById(id);
	}

	public List<Event> findById(List<Long> ids){
		return repository.findAllById(ids);
	}
	
	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<Event> findAll() {
		return repository.findAll();
	}

	public List<Event> findByFilter(String name, String tag) {
		return repository.findByNameAndTag(name, tag);
	}

	public void save(Event event) {
		repository.save(event);
	}

	public void delete(long id) {
		repository.deleteById(id);
	}
}
