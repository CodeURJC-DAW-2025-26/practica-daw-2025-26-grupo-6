package com.grupo6daw.lcdd_daw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NewsController {
	
	@GetMapping("/news")
	public String news() {
		return "news";
	}

  @GetMapping("/detail_new_page")
	public String detail_new_page() {
		return "detail_new_page";
	}

  @GetMapping("/new_form")
	public String new_form() {
		return "new_form";
	}
}
