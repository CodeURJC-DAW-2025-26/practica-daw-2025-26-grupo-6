package com.grupo6daw.lcdd_daw.controller;

import java.util.NoSuchElementException;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.UserService;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        Long userId;
        try {
            userId = Long.valueOf(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(
                "El principal no es un ID numérico. Ajusta la carga del usuario (email/nick) según tu UserDetails."
            );
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

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
}