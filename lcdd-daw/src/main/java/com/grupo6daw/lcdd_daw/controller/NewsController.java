package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.service.NewService;
import com.grupo6daw.lcdd_daw.service.ImageService;

@Controller
public class NewsController {

	@Autowired
	private NewService newService;

	@Autowired
	private ImageService imageService;
	
	@GetMapping("/news")
	public String news(Model model,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String tag) {

		model.addAttribute("new", newService.findByFilter(name, tag));
		model.addAttribute("name", name == null ? "" : name);
		model.addAttribute("tag", tag == null ? "" : tag);
		return "news";
	}

  @GetMapping("/new/{id}")
	public String newDetail(@PathVariable long id, Model model) {
		Optional<New> newPost = newService.findById(id);
		if (newPost.isPresent()) {
			model.addAttribute("new", newPost.get());
		}
		return "detail_new_page";
	}

  @GetMapping("/new_form")
	public String new_form() {
		return "new_form";
	}

	@PostMapping("/new_form")
	public String newNewProcess(Model model, New newPost, MultipartFile imageField) throws IOException {
		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			newPost.setNewImage(image);
		}

		newService.save(newPost);

		return "redirect:news";
	}

	@PostMapping("/removeNew/{id}")
	public String removeNew(Model model, @PathVariable long id) {

		Optional<New> newPost = newService.findById(id);
		if (newPost.isPresent()) {
			newService.delete(id);
			model.addAttribute("new", newPost.get());
		}

		return "redirect:/news";
	}
}
