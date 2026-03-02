package com.grupo6daw.lcdd_daw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class LoginWebController {

	@GetMapping("/login")
	public String login(Model model, @RequestParam(required = false) String error) {
		if (error != null) {
			model.addAttribute("loginError", true);
			model.addAttribute("errorMessage", "Correo electrónico o contraseña incorrectos.");
		}
		return "login";
	}

	@GetMapping("/loginerror")
	public String loginerror() {
		return "/";
	}

	@GetMapping("/logout")
	public String logout() {
		return "/";
	}
}