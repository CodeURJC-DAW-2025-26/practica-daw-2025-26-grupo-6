package com.grupo6daw.lcdd_daw.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo6daw.lcdd_daw.dto.NewDTO;
import com.grupo6daw.lcdd_daw.dto.NewMapper;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.NewRepository;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

@Service
public class NewService {

    @Autowired
    private NewRepository repository;

    @Autowired
    private NewMapper mapper;

    @Autowired
    private UserRepository userRepository;

    public New findById(long id) {
        return repository.findById(id).orElseThrow();
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

    public Page<NewDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    public Page<New> findByFilter(String name, String tag, Pageable page) {
        return repository.findByNameAndTag(name, tag, page);
    }

    public New save(New newEntity) {
        repository.save(newEntity);
        return newEntity;
    }

    public New saveRest(New n, Long id, LocalDateTime date) {
        User author = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        n.setNewCreator(author);
        n.setCreationDate(date);

        repository.save(n);
        return n;
    }

    @Transactional
    public New delete(long id) {
        New newOpt = repository.findById(id).orElseThrow();

        if (newOpt != null) {
            New newsItem = newOpt;

            // Initialize lazy associations required by NewDTO mapping after transaction ends
            newsItem.getNewEvents().size();
            if (newsItem.getNewImage() != null) {
                newsItem.getNewImage().getId();
            }

            User creator = newsItem.getNewCreator();
            if (creator != null) {
                creator.getUserId();

                creator.getUserNews().remove(newsItem);
                userRepository.save(creator);
            }

            repository.deleteById(id);
        }
        return newOpt;
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

    public New addImageToNew(long id, Image image) {
        New n = repository.findById(id).orElseThrow();
        n.setNewImage(image);
        repository.save(n);

        return n;
    }

    public New removeImageFromNew(long newId, long imageId) {
        New n = repository.findById(newId).orElseThrow();
        if (n.getNewImage() == null || n.getNewImage().getId() != imageId) {
            throw new IllegalArgumentException("Image does not belong to this post");
        }
        n.setNewImage(null);
        repository.save(n);

        return n;
    }

    public NewDTO toDTO(New n) {
        return mapper.toFullDTO(n);
    }

    public New toDomain(NewDTO newDTO) {
        return mapper.toDomainFromFullDTO(newDTO);
    }
}
