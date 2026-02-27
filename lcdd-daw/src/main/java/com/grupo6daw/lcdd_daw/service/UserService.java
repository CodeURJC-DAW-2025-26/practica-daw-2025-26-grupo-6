package com.grupo6daw.lcdd_daw.service;

import org.springframework.beans.factory.annotation.Autowired;
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
