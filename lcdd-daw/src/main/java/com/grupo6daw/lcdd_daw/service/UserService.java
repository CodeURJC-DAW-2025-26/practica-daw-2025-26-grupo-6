package com.grupo6daw.lcdd_daw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.dto.UserRegistrationDto;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsByUserEmail(String email) {
        return userRepository.existsByUserEmail(email);
    }

    public void register(UserRegistrationDto dto) {
        User user = new User(
                dto.getName(),
                dto.getSurnames(),
                dto.getNickname(),
                "",
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                "REGISTERED_USER"
        );

        userRepository.save(user);
    }
}
