package com.grupo6daw.lcdd_daw.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.grupo6daw.lcdd_daw.controller.RegisterWebController;
import com.grupo6daw.lcdd_daw.dto.ImageDTO;
import com.grupo6daw.lcdd_daw.dto.ImageMapper;
import com.grupo6daw.lcdd_daw.dto.UserDTO;
import com.grupo6daw.lcdd_daw.dto.UserDetailsDTO;
import com.grupo6daw.lcdd_daw.dto.UserMapper;
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

    @Autowired
    ImageService imageService;

    @Autowired
    ImageMapper imageMapper;

    @Autowired
    UserMapper userMapper;

    Logger logger = LoggerFactory.getLogger(RegisterWebController.class);

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDTO);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public boolean existsByUserEmail(String email) {
        return userRepository.existsByUserEmail(email);
    }

    public User register(UserDetailsDTO dto, List<String> errorMessages) {

        if (!validRegister(dto, errorMessages)) {
            return null;
        }

        User user = new User(
                dto.userName(),
                dto.userSurname(),
                dto.userNickname(),
                "",
                dto.userEmail(),
                passwordEncoder.encode(dto.password()),
                "REGISTERED_USER");

        userRepository.save(user);
        mailService.sendRegisterEmail(user);

        return user;
    }

    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    public ImageDTO setUserImage(long id, MultipartFile file) {
        try {

            User user = getUser(id).orElseThrow();

            Image image;
            if (user.getUserImage() == null) {
                image = imageService.createImage(file.getInputStream());
            } else {
                image = imageService.replaceImageFile(user.getUserImage().getId(), file.getInputStream());
            }

            user.setUserImage(image);

            userRepository.save(user);

            return imageMapper.toDTO(image);
        } catch (IOException e) {
            logger.error("IOException when getting InputStream in setUserImage");
            return null;
        }
    };

    public Resource getImageFile(long id) throws SQLException {

        Optional<Image> imageOptional = userRepository.findImageByUserId(id);

        if (imageOptional.isPresent() && imageOptional.get().getImageFile() != null) {
            return new InputStreamResource(imageOptional.get().getImageFile().getBinaryStream());
        } else {
            return new ClassPathResource("static/img/person/Portrait_Placeholder.png");
        }
    }

    public User updateProfile(User user, UserDetailsDTO dto, List<String> errorMessages) {

        if (!validUpdate(user, dto, errorMessages)) {
            return null;
        }

        if (dto.password() != null && !dto.password().isEmpty()) {
            user.setUserEncodedPassword(passwordEncoder.encode(dto.password()));
        }

        user.setUserEmail(dto.userEmail());
        user.setUserNickname(dto.userNickname());
        user.setUserName(dto.userName());
        user.setUserSurname(dto.userSurname());
        user.setUserInterests(dto.userInterests());

        userRepository.save(user);

        return user;
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

    // Return if the registerDto is valid or not and adds any errors to
    // errorMessages list.
    // errorMessages must be empty
    private boolean validDTO(UserDetailsDTO dto, List<String> errorMessages) {

        if (errorMessages == null) {
            throw new IllegalArgumentException("errorMessages can't be null");
        }

        if (!errorMessages.isEmpty()) {
            throw new IllegalArgumentException("errorMessages list must be empty");
        }

        if (dto.userEmail() == null || dto.userEmail().isEmpty()) {
            errorMessages.add("El email es obligatorio.");
        } else if (!dto.userEmail().matches(EMAIL_REGEX)) {
            errorMessages.add("Email inválido.");
        }

        if (dto.userName() == null || dto.userName().isEmpty()) {
            errorMessages.add("El nombre es obligatorio.");
        }

        if (dto.userSurname() == null || dto.userSurname().isEmpty()) {
            errorMessages.add("Los apellidos son obligatorios.");
        }

        if (dto.userNickname() == null || dto.userNickname().isEmpty()) {
            errorMessages.add("El nickname es obligatorio.");
        }

        if (!(dto.password() == null && dto.confirmPassword() == null)
                && (dto.password() != null && !dto.password().equals(dto.confirmPassword()))) {
            errorMessages.add("Las contraseñas no coinciden.");
        }

        return errorMessages.isEmpty();
    }

    private boolean validRegister(UserDetailsDTO dto, List<String> errorMessages) {

        validDTO(dto, errorMessages);

        if (dto.password() == null || dto.password().length() < 4) {
            errorMessages.add("La contraseña debe tener por lo menos 4 caracteres.");
        }

        if (dto.userEmail() != null && existsByUserEmail(dto.userEmail())) {
            errorMessages.add("Ya existe un usuario con ese correo electrónico.");
        }

        if (dto.userNickname() != null && existsByUserNickname(dto.userNickname())) {
            errorMessages.add("El apodo '" + dto.userNickname() + "' ya está siendo usado por otro aventurero.");
        }

        return errorMessages.isEmpty();
    }

    private boolean validUpdate(User user, UserDetailsDTO dto, List<String> errorMessages) {

        validDTO(dto, errorMessages);

        if (dto.userNickname() != null && !dto.userNickname().equals(user.getUserNickname())
                && existsByUserNickname(dto.userNickname())) {
            errorMessages.add("El apodo '" + dto.userNickname() + "' ya está siendo usado por otro aventurero.");
        }

        if (dto.userEmail() != null && !dto.userEmail().equals(user.getUserEmail())
                && existsByUserEmail(dto.userEmail())) {
            errorMessages.add("Ya existe un usuario con ese correo electrónico.");
        }

        if (dto.password() != null && !dto.password().isEmpty() && dto.password().length() < 4) {
            errorMessages.add("La contraseña debe tener por lo menos 4 caracteres.");
        }

        return errorMessages.isEmpty();
    }

    private UserDTO toDTO(User user) {
        return userMapper.toFullDTO(user);
    }
}
