package com.grupo6daw.lcdd_daw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdministrationController {
	
	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}
}