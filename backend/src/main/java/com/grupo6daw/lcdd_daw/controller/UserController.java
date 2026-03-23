package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.grupo6daw.lcdd_daw.dto.UserDetailsDTO;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {

        if (request.getUserPrincipal() == null) {
            throw new AccessDeniedException("No hay usuario autenticado");
        }

        Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
        User user = userService.getUser(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        boolean isAdmin = request.isUserInRole("ADMIN");
        boolean hasEditPermission = true;

        model.addAttribute("user", user);
        model.addAttribute("hasEditPermission", hasEditPermission);
        model.addAttribute("isOwner", true);
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("userId", String.valueOf(currentUserId));
        model.addAttribute("admin", isAdmin);

        return "profile";
    }

    @GetMapping("/user/{id}")
    public String userProfile(@PathVariable Long id, Model model, HttpServletRequest request) {
        User user = userService.findById(id);
        model.addAttribute("user", user);

        boolean hasEditPermission = false;
        boolean isOwner = false;
        boolean isAdmin = false;

        if (request.getUserPrincipal() != null) {
            Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
            isOwner = user.getUserId() != null && user.getUserId().equals(currentUserId);
            isAdmin = request.isUserInRole("ADMIN");
            hasEditPermission = isOwner || isAdmin;

            model.addAttribute("userId", String.valueOf(currentUserId));
            model.addAttribute("admin", isAdmin);
        }

        model.addAttribute("hasEditPermission", hasEditPermission);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAdmin", isAdmin);

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
    public String updateProfile(Model model, UserDetailsDTO dto,
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication, @PathVariable long id,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "userImage", required = false) MultipartFile userImage) {

        boolean admin = (boolean) model.getAttribute("admin");
        Long userId = Long.valueOf((String) model.getAttribute("userId"));

        if (!admin && userId != id) {
            throw new AccessDeniedException("No tienes permiso para cambiar ese usuario");
        }

        User user = userService.findById(id);
        String oldEmail = user.getUserEmail();

        List<String> errors = new ArrayList<>();

        user = userService.updateProfile(user, dto, errors);

        if (!errors.isEmpty()) {
            model.addAttribute("user", user);
            model.addAttribute("hasErrors", true);
            model.addAttribute("allErrors", errors);
            return "profile";
        }

        // if no errors, set image
        if (!userImage.isEmpty()) {
            userService.setUserImage(user.getUserId(), userImage);
        }

        boolean credentialsChanged = !dto.userEmail().equals(oldEmail) || (dto.password() != null
                && !dto.password().isEmpty());

        if (credentialsChanged && authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            redirectAttributes.addFlashAttribute("loginMessage",
                    "Tus credenciales han cambiado correctamente. Por favor, inicia sesión con tus nuevos datos.");
            return "redirect:/login";
        }
        return "redirect:/user/" + id;
    }

    @PostMapping("/user/{id}/delete")
    public String deleteProfile(Model model, HttpServletRequest request, @PathVariable long id) {
        boolean admin = (boolean) model.getAttribute("admin");

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
