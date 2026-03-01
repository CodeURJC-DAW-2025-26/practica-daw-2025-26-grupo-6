package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.ImageService;
import com.grupo6daw.lcdd_daw.service.ImageValidationService;
import com.grupo6daw.lcdd_daw.service.NewService;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class NewsController {

	@Autowired
	private NewService newService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserService userService;

	@Autowired
	private ImageValidationService imageValidationService;

	@GetMapping("/news")
	public String news(Model model,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String tag,
			@RequestParam(defaultValue = "0") int page) {

		Page<New> newsPage = newService.findValidatedByFilter(name, tag, PageRequest.of(page, 10));

		model.addAttribute("new", newsPage.getContent());
		model.addAttribute("name", name == null ? "" : name);
		model.addAttribute("tag", tag == null ? "" : tag);
		model.addAttribute("hasNext", newsPage.hasNext());
		model.addAttribute("nextPage", page + 1);
		
		return "news";
	}

	@GetMapping("/new/{id}")
	public String newDetail(@PathVariable long id, Model model, HttpServletRequest request) {
		Optional<New> newPost = newService.findById(id);
		if (newPost.isPresent()) {
			model.addAttribute("new", newPost.get());

			boolean hasEditPermission = false;
			if (request.getUserPrincipal() != null) {
				Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
				boolean isOwner = newPost.get().getNewCreator() != null
						&& newPost.get().getNewCreator().getUserId().equals(currentUserId);
				boolean isAdmin = request.isUserInRole("ADMIN");
				hasEditPermission = isOwner || isAdmin;
			}
			model.addAttribute("hasEditPermission", hasEditPermission);
		}
		return "detail_new_page";
	}

	@GetMapping("/new_form")
	public String showNewForm(Model model, HttpServletRequest request) {

	
		New newPost = new New();
		newPost.setNewName("");
		newPost.setNewDescription("");
		newPost.setNewTag("");

		
		model.addAttribute("newPost", newPost);

		CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrfToken != null) {
			model.addAttribute("token", csrfToken.getToken());
		}

		model.addAttribute("hasErrors", false);
		model.addAttribute("allErrors", new ArrayList<String>());

		return "new_form";
	}

	@GetMapping("/new_form/{id}")
	public String editNewForm(@PathVariable long id, HttpServletRequest request, Model model) {
		Optional<New> newPost = newService.findById(id);
		if (newPost.isPresent()) {
			New n = newPost.get();

			boolean isAdmin = request.isUserInRole("ADMIN");
			Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
			boolean isOwner = n.getNewCreator() != null && n.getNewCreator().getUserId().equals(currentUserId);

			if (isAdmin || isOwner) {
				model.addAttribute("newPost", n);
				CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if (csrfToken != null)
					model.addAttribute("token", csrfToken.getToken());
				return "new_form";
			}
		}
		return "redirect:/news";
	}

	@PostMapping("/new_form")
	public String newNewProcess(Model model, @Valid New newPost, BindingResult bindingResult,
			@RequestParam("imageField") MultipartFile imageField,
			HttpServletRequest request) throws IOException {

		List<String> errorMessages = new ArrayList<>();
		boolean isNewPost = (newPost.getNewId() == null);

		Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
		User currentUser = userService.getUser(currentUserId).orElseThrow();

		if (bindingResult.hasErrors()) {
			if (bindingResult.hasFieldErrors("newName")) {
				errorMessages.add(bindingResult.getFieldError("newName").getDefaultMessage());
			}
			if (bindingResult.hasFieldErrors("newDescription")) {
				errorMessages.add(bindingResult.getFieldError("newDescription").getDefaultMessage());
			}
			if (bindingResult.hasFieldErrors("newTag")) {
				errorMessages.add(bindingResult.getFieldError("newTag").getDefaultMessage());
			}
		}

		imageValidationService.validate(imageField, errorMessages, isNewPost);

		if (!errorMessages.isEmpty()) {
			CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
			if (csrfToken != null) {
				model.addAttribute("token", csrfToken.getToken());
			}
			model.addAttribute("hasErrors", true);
			model.addAttribute("allErrors", errorMessages);
			model.addAttribute("newPost", newPost);
			return "new_form";
		}

		if (!isNewPost) {
			New existingNew = newService.findById(newPost.getNewId()).get();
			boolean isOwner = existingNew.getNewCreator() != null
					&& existingNew.getNewCreator().getUserId().equals(currentUserId);
			boolean isAdmin = request.isUserInRole("ADMIN");

			if (!isOwner && !isAdmin) {
				return "redirect:/news?error=unauthorized";
			}

			newPost.setNewCreator(existingNew.getNewCreator());
		} else {

			newPost.setNewCreator(currentUser);
		}

		if (!imageField.isEmpty()) {
			Image image = imageService.createImage(imageField.getInputStream());
			newPost.setNewImage(image);
		} else if (!isNewPost) {
			New oldNew = newService.findById(newPost.getNewId()).get();
			newPost.setNewImage(oldNew.getNewImage());
		}

		newService.save(newPost);

		if (isNewPost) {
        currentUser.getUserNews().add(newPost);
        userService.save(currentUser); 
    }
		return "redirect:/news";
	}

	@PostMapping("/removeNew/{id}")
	public String removeNew(@PathVariable long id, HttpServletRequest request) {
		Optional<New> newPost = newService.findById(id);
		if (newPost.isPresent()) {
			New n = newPost.get();
			boolean isAdmin = request.isUserInRole("ADMIN");
			Long currentUserId = Long.valueOf(request.getUserPrincipal().getName());
			boolean isOwner = n.getNewCreator() != null && n.getNewCreator().getUserId().equals(currentUserId);

			if (isAdmin || isOwner) {
				newService.delete(id);
			}
		}
		return "redirect:/news";
	}
}
