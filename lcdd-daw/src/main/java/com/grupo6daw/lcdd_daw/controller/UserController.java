package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.grupo6daw.lcdd_daw.dto.ProfileUpdateDTO;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // Usamos el valueOf de tu compañero
        Long id = Long.valueOf((String) model.getAttribute("userId"));
        User user = userService.getUser(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/user/{id}")
    public String userProfile(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/user/{id}/image")
    public ResponseEntity<Resource> getUserImage(@PathVariable long id) throws SQLException {
        Resource imageFile = userService.getImageFile(id);
        MediaType mediaType = MediaTypeFactory.getMediaType(imageFile)
                .orElse(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok().contentType(mediaType).body(imageFile);
    }

    @PostMapping("/user/{id}/update")
    public String updateProfile(Model model,
            @Valid ProfileUpdateDTO dto,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            @PathVariable long id) throws IOException {

        boolean admin = (boolean) model.getAttribute("admin");
        // Usamos el valueOf de tu compañero
        Long userId = Long.valueOf((String) model.getAttribute("userId"));

        if (!admin && userId != id) {
            throw new AccessDeniedException("No tienes permiso para cambiar ese usuario");
        }

        User currentUser = userService.findById(id).orElseThrow();

        if (dto.getEmail() != null && !currentUser.getUserEmail().equals(dto.getEmail())) {
            if (userService.existsByUserEmail(dto.getEmail())) {
                bindingResult.rejectValue("email", "error.email", "Ese correo electrónico ya está registrado.");
            }
        }

        if (dto.getNickname() != null && !currentUser.getUserNickname().equals(dto.getNickname())) {
            if (userService.existsByUserNickname(dto.getNickname())) {
                bindingResult.rejectValue("nickname", "error.nickname", "Ese apodo ya está en uso. Elige otro.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUser);
            model.addAttribute("hasErrors", true);

            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("allErrors", errors);

            return "profile";
        }

        boolean credentialsChanged = userService.updateProfile(id, dto);

        if (credentialsChanged && authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/login?logout";
        }

        return "redirect:/user/" + id;
    }

    @PostMapping("/user/{id}/delete")
    public String deleteProfile(Model model, HttpServletRequest request, @PathVariable long id) {
        boolean admin = (boolean) model.getAttribute("admin");
        // Usamos el valueOf de tu compañero
        Long userId = Long.valueOf((String) model.getAttribute("userId"));

        if (!admin && userId != id) {
            return "redirect:/error";
        }

        userService.deleteUser(id);
        return "redirect:/userDeleted";
    }

    @GetMapping("/userDeleted")
    public String userDeleted(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

}