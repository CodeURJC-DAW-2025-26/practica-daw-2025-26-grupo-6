package com.grupo6daw.lcdd_daw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

    public List<String> validateContent(New newPost) {
        final int MIN_NAME_LENGTH = 10;
        final int MAX_NAME_LENGTH = 100;
        final int MIN_DESCRIPTION_LENGTH = 50;
        final int MAX_DESCRIPTION_LENGTH = 3000;

        if (newPost == null) {
            return List.of("La noticia es obligatoria");
        }

        List<String> errors = new ArrayList<>();

        String name = newPost.getNewName();
        if (name == null || name.isBlank()) {
            errors.add("El nombre/título de la noticia es obligatorio");
        } else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            errors.add("El título debe tener entre 10 y 100 caracteres");
        }

        String description = newPost.getNewDescription();
        if (description == null || description.isBlank()) {
            errors.add("La descripción no puede estar vacía");
        } else if (description.length() < MIN_DESCRIPTION_LENGTH || description.length() > MAX_DESCRIPTION_LENGTH) {
            errors.add("La descripción debe tener entre 50 y 3000 caracteres");
        }

        String tag = newPost.getNewTag();
        if (tag == null || tag.isBlank()) {
            errors.add("La etiqueta de la noticia es obligatoria");
        }

        return errors;
    }

    public boolean hasEditPermission(New newPost, Long currentUserId, boolean isAdmin) {
        return isAdmin || (newPost.getNewCreator() != null
                && newPost.getNewCreator().getUserId().equals(currentUserId));
    }

    public New save(New newPost, Long currentUserId) {
        List<String> errors = validateContent(newPost);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" | ", errors));
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isNewPost = (newPost.getNewId() == null);

        if (!isNewPost) {
            New existingNew = findById(newPost.getNewId());
            if (newPost.getNewImage() == null) {
                newPost.setNewImage(existingNew.getNewImage());
            }
        }

        if (checkPermissions(newPost, currentUser, isNewPost)) {
            New savedNew = save(newPost);

            if (isNewPost) {
                if (currentUser.getUserNews() == null) {
                    currentUser.setUserNews(new ArrayList<>());
                }
                currentUser.getUserNews().add(savedNew);
                userRepository.save(currentUser);
            }

            return savedNew;
        }

        return null;
    }

    @Transactional
    public New delete(long id) {
        New newOpt = repository.findById(id).orElseThrow();

        if (newOpt != null) {
            New newsItem = newOpt;

            // Initialize lazy associations required by NewDTO mapping after transaction
            // ends
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

    public New deleteAuthorized(Long id, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        New newPost = findById(id);

        if (checkPermissions(newPost, currentUser, false)) {
            return delete(id);
        }

        return null;
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

    public New addImageToNew(long id, Image image, Long currentUserId) {
        New n = repository.findById(id).orElseThrow();

        if (checkPermissions(n, userRepository.findById(currentUserId).orElseThrow(), (n.getNewId() == null))) {
            n.setNewImage(image);
            repository.save(n);
        } else {
            throw new AccessDeniedException("No tienes permiso para modificar esta noticia");
        }

        return n;
    }

    public New removeImageFromNew(long newId, long imageId, Long currentUserId) {
        New n = repository.findById(newId).orElseThrow();

        if (n.getNewImage() == null || n.getNewImage().getId() != imageId) {
            throw new IllegalArgumentException("Image does not belong to this post");
        }

        if (checkPermissions(n, userRepository.findById(currentUserId).orElseThrow(), (n.getNewId() == null))) {
            n.setNewImage(null);
            repository.save(n);
        } else {
            throw new AccessDeniedException("No tienes permiso para eliminar contenido de esta noticia");
        }

        return n;
    }

    public NewDTO toDTO(New n) {
        return mapper.toFullDTO(n);
    }

    public New toDomain(NewDTO newDTO) {
        return mapper.toDomainFromFullDTO(newDTO);
    }

    public boolean checkPermissions(New newPost, User currentUser, boolean isNewPost) {
        if (!isNewPost) {
            New existingNew = findById(newPost.getNewId());
            boolean isOwner = existingNew.getNewCreator() != null
                    && existingNew.getNewCreator().getUserId().equals(currentUser.getUserId());
            boolean isAdmin = currentUser.getUserRoles().contains("ADMIN");

            if (!isOwner && !isAdmin) {
                return false;
            }

            newPost.setNewCreator(existingNew.getNewCreator());
        } else {
            newPost.setNewCreator(currentUser);
        }

        return true;
    }
}
