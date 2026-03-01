package com.grupo6daw.lcdd_daw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.repository.NewRepository;

import com.grupo6daw.lcdd_daw.repository.UserRepository;
import com.grupo6daw.lcdd_daw.model.User;

@Service
public class NewService {

	@Autowired
	private NewRepository repository;

	@Autowired
	private UserRepository userRepository;

	public Optional<New> findById(long id) {
		return repository.findById(id);
	}

	public List<New> findById(List<Long> ids) {
		return repository.findAllById(ids);
	}

	public boolean exist(long id) {
		return repository.existsById(id);
	}

	public List<New> findAll() {
		return repository.findAll();
	}

	public Page<New> findByFilter(String name, String tag, Pageable page) {
		return repository.findByNameAndTag(name, tag, page);
	}

	public void save(New newEntity) {
		repository.save(newEntity);
	}

	public void delete(long id) {
		Optional<New> newOpt = repository.findById(id);

		if (newOpt.isPresent()) {
			New newsItem = newOpt.get();

			
			User creator = newsItem.getNewCreator();
			if (creator != null) {
				
				creator.getUserNews().remove(newsItem);
				userRepository.save(creator); 
			}

		
			repository.deleteById(id);
		}
	}

	public List<New> findTop3() {
		return repository.findTop3ByOrderByCreationDateDesc();
	}

	public List<New> findByValidatedFalse() {
		return repository.findByValidatedFalse();
	}

	public List<New> findByValidatedTrue() {
		return repository.findByValidatedTrue();
	}

	public Page<New> findValidatedByFilter(String name, String tag, Pageable page) {
		return repository.findValidatedByNameAndTag(name, tag, page);
	}
}
