package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.grupo6daw.lcdd_daw.dto.ProfileUpdateDTO;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

	@GetMapping("/profile")
	public String profile(Model model) {
		Long id = Long.parseLong((String) model.getAttribute("userId"));
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
	public String updateProfile(Model model, @Valid ProfileUpdateDTO dto, HttpServletRequest request, @PathVariable long id) throws IOException {
		boolean admin = (boolean) model.getAttribute("admin");
        Long userId = Long.parseLong((String) model.getAttribute("userId"));
        
        if (!admin && userId != id) {
            throw new AccessDeniedException("No tienes permiso para cambiar ese usuario");
        }

		userService.updateProfile(id, dto);
		
		return "redirect:/user/" + id;
	}

    @PostMapping("/user/{id}/delete")
	public String deleteProfile(Model model, HttpServletRequest request, @PathVariable long id) {
		boolean admin = (boolean) model.getAttribute("admin");
        Long userId = Long.parseLong((String) model.getAttribute("userId"));
        
        if (!admin && userId != id) {
            return "redirect:/error";
        }

		userService.deleteUser(id);
		return "redirect:/logout";
	}
}