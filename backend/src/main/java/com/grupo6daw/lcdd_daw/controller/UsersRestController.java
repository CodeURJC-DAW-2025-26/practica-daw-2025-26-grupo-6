package com.grupo6daw.lcdd_daw.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.grupo6daw.lcdd_daw.dto.UserDTO;
import com.grupo6daw.lcdd_daw.dto.UserMapper;
import com.grupo6daw.lcdd_daw.dto.UserDetailsDTO;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/users")
public class UsersRestController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @PostMapping("/")
    public ResponseEntity<UserDTO> register(UserDetailsDTO dto) {

        List<String> errorMessages = new ArrayList<>();

        User user = userService.register(dto, errorMessages);

        if (!errorMessages.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserDTO userDTO = userMapper.toFullDTO(user);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getUserId()).toUri();

        return ResponseEntity.created(location).body(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateProfile(UserDetailsDTO dto,
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication, @PathVariable long id,
            RedirectAttributes redirectAttributes) throws IOException {

        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

        Long userId = Long.parseLong(authentication.getName());

        if (!admin && userId != id) {
            throw new AccessDeniedException("No tienes permiso para cambiar ese usuario");
        }

        User user = userService.findById(id).orElseThrow();

        List<String> errors = new ArrayList<>();

        user = userService.updateProfile(user, dto, errors);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserDTO userDTO = userMapper.toFullDTO(user);
        return ResponseEntity.ok(userDTO);
    }
}
