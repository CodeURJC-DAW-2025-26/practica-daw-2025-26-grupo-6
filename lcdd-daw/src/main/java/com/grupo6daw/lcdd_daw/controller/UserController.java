package com.grupo6daw.lcdd_daw.controller;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/profile")
	public String profile(Model model, HttpServletRequest request) {
		
		String userId = request.getUserPrincipal().getName();
		User user = userService.getUser(Long.parseLong(userId)).orElseThrow();
		model.addAttribute("user", user);
		return "profile";
	}

	// Get image for users, if they have no image it returns the default image
	@GetMapping("/user/{id}/image")
	public ResponseEntity<Resource> getUserImage(@PathVariable long id) throws SQLException {

		Resource imageFile = userService.getImageFile(id);

		MediaType mediaType = MediaTypeFactory
				.getMediaType(imageFile)
				.orElse(MediaType.IMAGE_JPEG);

		return ResponseEntity
				.ok()
				.contentType(mediaType)
				.body(imageFile);
	}
}