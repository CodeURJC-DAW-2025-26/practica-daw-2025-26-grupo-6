package com.grupo6daw.lcdd_daw.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.grupo6daw.lcdd_daw.dto.ImageDTO;
import com.grupo6daw.lcdd_daw.dto.UserDTO;
import com.grupo6daw.lcdd_daw.dto.UserMapper;
import com.grupo6daw.lcdd_daw.dto.UserDetailsDTO;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/users")
public class UsersRestController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @GetMapping("/")
    public Page<UserDTO> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable long id) {
        return userMapper.toFullDTO(userService.findById(id));
    }

    @PostMapping("/")
    public ResponseEntity<Object> register(@RequestBody UserDetailsDTO dto) {

        List<String> errorMessages = new ArrayList<>();

        User user = userService.register(dto, errorMessages);

        if (!errorMessages.isEmpty()) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("errors", errorMessages);
            return ResponseEntity.badRequest().body(body);
        }

        UserDTO userDTO = userMapper.toFullDTO(user);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getUserId()).toUri();

        return ResponseEntity.created(location).body(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDetailsDTO dto,
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication, @PathVariable long id,
            RedirectAttributes redirectAttributes) throws IOException {

        if (authentication == null) {
            throw new AccessDeniedException("Autenticación necesaria");
        }

        boolean admin = request.isUserInRole("ADMIN");

        Long userId = Long.parseLong(authentication.getName());

        if (!admin && userId != id) {
            throw new AccessDeniedException("No tienes permiso para cambiar ese usuario");
        }

        User user = userService.findById(id);

        List<String> errors = new ArrayList<>();

        user = userService.updateProfile(user, dto, errors);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserDTO userDTO = userMapper.toFullDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProfile(@PathVariable long id, Authentication authentication,
            HttpServletRequest request) {

        if (authentication == null) {
            throw new AccessDeniedException("Autenticación necesaria");
        }

        boolean admin = request.isUserInRole("ADMIN");

        Long userId = Long.parseLong(authentication.getName());

        if (!admin && userId != id) {
            throw new AccessDeniedException("No tienes permiso para cambiar ese usuario");
        }

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getUserImage(@PathVariable long id) throws SQLException {
        Resource imageFile = userService.getImageFile(id);
        MediaType mediaType = MediaTypeFactory.getMediaType(imageFile)
                .orElse(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok().contentType(mediaType).body(imageFile);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<ImageDTO> putImage(@RequestParam MultipartFile userImage, @PathVariable long id,
            Authentication authentication, HttpServletRequest request) {

        if (authentication == null) {
            throw new AccessDeniedException("Autenticación necesaria");
        }

        boolean admin = request.isUserInRole("ADMIN");

        Long userId = Long.parseLong(authentication.getName());

        if (!admin && userId != id) {
            throw new AccessDeniedException("No tienes permiso para cambiar ese usuario");
        }

        return ResponseEntity.ok(userService.setUserImage(userId, userImage));
    }

}
