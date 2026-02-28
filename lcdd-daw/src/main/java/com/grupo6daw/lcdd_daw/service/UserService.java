package com.grupo6daw.lcdd_daw.service;

import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.dto.UserRegistrationDto;
import com.grupo6daw.lcdd_daw.model.Image;
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

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public Resource getImageFile(long id) throws SQLException {

        Optional<Image> imageOptional = userRepository.findImageByUserId(id);

        if (imageOptional.isPresent() && imageOptional.get().getImageFile() != null) {
            return new InputStreamResource(imageOptional.get().getImageFile().getBinaryStream());
        } else {
            return new ClassPathResource("static/img/default_profile.png");
        }
    }
}
