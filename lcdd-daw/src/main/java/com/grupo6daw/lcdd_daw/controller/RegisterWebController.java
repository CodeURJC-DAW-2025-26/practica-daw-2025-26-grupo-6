package com.grupo6daw.lcdd_daw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.grupo6daw.lcdd_daw.dto.UserRegistrationDto;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class RegisterWebController {

	@Autowired
	UserService userService;

	Logger logger = LoggerFactory.getLogger(RegisterWebController.class);


	@GetMapping("/userExists")
	@ResponseBody
	public boolean getMethodName(@RequestParam String email) {
		return userService.existsByUserEmail(email);
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("email", "");
		model.addAttribute("password", "");
		model.addAttribute("name", "");
		model.addAttribute("surnames", "");
		model.addAttribute("nickname", "");
		model.addAttribute("invalidEmail", false);
		return "register";
	}

	@PostMapping("/register")
	public String postMethodName(Model model, @Valid UserRegistrationDto dto, HttpServletRequest request) {
		
		if (userService.existsByUserEmail(dto.getEmail())) {
			model.addAttribute("email", dto.getEmail());
			model.addAttribute("password", dto.getPassword());
			model.addAttribute("name", dto.getName());
			model.addAttribute("surnames", dto.getSurnames());
			model.addAttribute("nickname", dto.getNickname());
			model.addAttribute("invalidEmail", true);
			return "register";
		}

		userService.register(dto);

		try {
			request.login(dto.getEmail(), dto.getPassword());
		} catch (ServletException e) {
			return "redirect:/login";
		}
		
		return "redirect:/";
	}
}