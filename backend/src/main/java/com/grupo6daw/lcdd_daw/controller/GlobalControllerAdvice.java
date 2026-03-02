package com.grupo6daw.lcdd_daw.controller;

import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerAdvice {
    
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();
        
		if (principal != null) {

			model.addAttribute("logged", true);
			model.addAttribute("userId", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
	}
}
