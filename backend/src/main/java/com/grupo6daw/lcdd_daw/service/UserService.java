package com.grupo6daw.lcdd_daw.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.dto.ProfileUpdateDTO;
import com.grupo6daw.lcdd_daw.dto.UserRegistrationDto;
import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MailService mailService;

    @Autowired
    EventService eventService;

    @Autowired
    NewService newService;

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
                "REGISTERED_USER");

        userRepository.save(user);
        mailService.sendRegisterEmail(user);
        
    }

    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    public Resource getImageFile(long id) throws SQLException {

        Optional<Image> imageOptional = userRepository.findImageByUserId(id);

        if (imageOptional.isPresent() && imageOptional.get().getImageFile() != null) {
            return new InputStreamResource(imageOptional.get().getImageFile().getBinaryStream());
        } else {
            return new ClassPathResource("static/img/person/Portrait_Placeholder.png");
        }
    }

    public boolean updateProfile(long id, ProfileUpdateDTO dto) throws IOException {
        Exception error = null;
        User user = userRepository.findById(id).orElseThrow();
        boolean credentialsChanged = false;

   
        if (dto.getEmail() != null && !dto.getEmail().isEmpty() && !user.getUserEmail().equals(dto.getEmail())) {
            user.setUserEmail(dto.getEmail());
            credentialsChanged = true;
        }

    
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setUserEncodedPassword(passwordEncoder.encode(dto.getPassword()));
            credentialsChanged = true;
        }

        
        user.setUserNickname(dto.getNickname());
        user.setUserName(dto.getName());
        user.setUserSurname(dto.getSurnames());
        user.setUserInterests(dto.getInterests());

      
        MultipartFile file = dto.getImage();
        if (file != null && !file.isEmpty()) {
            try {
                user.setUserImage(new Image(new SerialBlob(file.getBytes())));
            } catch (Exception e) {
                error = e;
            }
        }

       
        userRepository.save(user);

      
        if (error != null) {
            throw new IOException("Failed to create image blob", error);
        }

    
        return credentialsChanged;
    }

    public void deleteUser(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            List<Event> eventsToDelete = new ArrayList<>(user.get().getUserOwnEvents());
            for (Event event : eventsToDelete) {
                eventService.delete(event.getEventId());
            }
            List<New> newsToDelete = new ArrayList<>(user.get().getUserNews());
            for (New userNew : newsToDelete) {
                newService.delete(userNew.getNewId());
            }
            forceLogoutUser(id);
            userRepository.deleteById(id);
        }
    }

    private void forceLogoutUser(long id) {
        // 1. Get all "principals" (logged-in users)
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        for (Object principal : allPrincipals) {
            if (principal instanceof UserDetails userDetails) {
                long userId = Long.parseLong(userDetails.getUsername());
                // 2. Check if this is the user we want to boot
                if (id == userId) {
                    // 3. Get all active sessions for this user
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);

                    for (SessionInformation session : sessions) {
                        // 4. Mark the session as expired
                        session.expireNow();
                    }
                    return;
                }
            }
        }
    }

    // Searching user by email, needed for the RegisterWebController to assign the
    // profile image after registration
    public Optional<User> findByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    // Saving user after assigning the profile image, needed for the
    // RegisterWebController
    public void save(User user) {
        userRepository.save(user);
    }

    // New method to check if a nickname already exists, needed for the
    // RegisterWebController
    public boolean existsByUserNickname(String nickname) {
        return userRepository.existsByUserNickname(nickname);
    }
}
