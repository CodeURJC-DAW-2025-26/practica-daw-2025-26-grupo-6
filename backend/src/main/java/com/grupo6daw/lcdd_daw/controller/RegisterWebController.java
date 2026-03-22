package com.grupo6daw.lcdd_daw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grupo6daw.lcdd_daw.dto.UserDetailsDTO;
import com.grupo6daw.lcdd_daw.service.ImageService;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RegisterWebController {

    @Autowired
    UserService userService;

    @Autowired
    ImageService imageService;

    @GetMapping("/userExists")
    @ResponseBody
    public boolean userExists(@RequestParam String email) {
        return userService.existsByUserEmail(email);
    }

    @GetMapping("/register")
    public String registerDisplay(Model model, HttpServletRequest request) {
        model.addAttribute("email", "");
        model.addAttribute("password", "");
        model.addAttribute("name", "");
        model.addAttribute("surnames", "");
        model.addAttribute("nickname", "");
        model.addAttribute("invalidEmail", false);
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, UserDetailsDTO dto, HttpServletRequest request) {

        List<String> errorMessages = new ArrayList<>();

        userService.register(dto, errorMessages);

        // if there are errors, we return to the register page with the error messages
        // and the data already written (except password)
        if (!errorMessages.isEmpty()) {
            model.addAttribute("hasErrors", true);
            model.addAttribute("allErrors", errorMessages);

            // keep the data already written, except password
            model.addAttribute("email", dto.userEmail());
            model.addAttribute("name", dto.userName());
            model.addAttribute("surnames", dto.userSurname());
            model.addAttribute("nickname", dto.userNickname());
            return "register";
        }

        // login to registered user
        try {
            request.login(dto.userEmail(), dto.password());
        } catch (ServletException e) {
            return "redirect:/login";
        }

        return "redirect:/";
    }
}