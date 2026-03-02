package com.grupo6daw.lcdd_daw.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.dto.UserRegistrationDto;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.ImageService;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class RegisterWebController {

	@Autowired
	UserService userService;

	@Autowired
	ImageService imageService;

	Logger logger = LoggerFactory.getLogger(RegisterWebController.class);

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

		CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrfToken != null) {
			model.addAttribute("token", csrfToken.getToken());
		}

		return "register";
	}

	@PostMapping("/register")
	public String register(Model model, @Valid UserRegistrationDto dto, BindingResult bindingResult,
			@RequestParam("confirm-password") String confirmPassword,
			@RequestParam(value = "imageField", required = false) MultipartFile imageField,
			HttpServletRequest request) {

		List<String> errorMessages = new ArrayList<>();

		
		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
		}

		// Same password validation
		if (!dto.getPassword().equals(confirmPassword)) {
			errorMessages.add("Las contraseñas no coinciden.");
		}

		// unique email validation
		if (userService.existsByUserEmail(dto.getEmail())) {
			errorMessages.add("Ya existe un usuario con ese correo electrónico.");
		}

		// unique nickname validation
		if (userService.existsByUserNickname(dto.getNickname())) {
			errorMessages.add("El apodo '" + dto.getNickname() + "' ya está siendo usado por otro aventurero.");
		}

		// if there are errors, we return to the register page with the error messages and the data already written (except password)
		if (!errorMessages.isEmpty()) {
			model.addAttribute("hasErrors", true);
			model.addAttribute("allErrors", errorMessages);

			// keep the data already written, except password
			model.addAttribute("email", dto.getEmail());
			model.addAttribute("name", dto.getName());
			model.addAttribute("surnames", dto.getSurnames());
			model.addAttribute("nickname", dto.getNickname());

			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null)
				model.addAttribute("token", csrfToken.getToken());

			return "register";
		}

		userService.register(dto);

		if (imageField != null && !imageField.isEmpty()) {
			try {
				Image profileImage = imageService.createImage(imageField.getInputStream());
				User newlyRegisteredUser = userService.findByUserEmail(dto.getEmail()).orElseThrow();
				newlyRegisteredUser.setUserImage(profileImage);
				userService.save(newlyRegisteredUser);
			} catch (Exception e) {
				logger.error("Error al guardar la imagen", e);
			}
		}

		try {
			request.login(dto.getEmail(), dto.getPassword());
		} catch (ServletException e) {
			return "redirect:/login";
		}

		return "redirect:/";
	}
}