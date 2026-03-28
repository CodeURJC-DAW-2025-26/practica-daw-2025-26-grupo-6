package com.grupo6daw.lcdd_daw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.service.GameService;
import com.grupo6daw.lcdd_daw.service.ImageService;
import com.grupo6daw.lcdd_daw.service.ImageValidationService;
import com.grupo6daw.lcdd_daw.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GamesController {

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageValidationService imageValidationService;

    @GetMapping("/games")
    public String games(Model model,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Integer players,
            @RequestParam(required = false) Integer duration,
            @RequestParam(defaultValue = "0") int page) {

        Page<Game> gamesPage = gameService.findByFilter(name, tag, players, duration,
                PageRequest.of(page, 10, Sort.by("gameId").descending()));

        model.addAttribute("game", gamesPage.getContent());
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("tag", tag == null ? "" : tag);
        model.addAttribute("hasNext", gamesPage.hasNext());
        model.addAttribute("nextPage", page + 1);

        return "games";
    }

    @GetMapping("/game/{id}")
    public String gameDetail(@PathVariable long id, Model model, HttpServletRequest request) {

        Game game = gameService.findById(id);
        if (game != null) {
            model.addAttribute("game", game);

            boolean hasEditPermission = false;
            boolean isFavorited = false;

            if (request.getUserPrincipal() != null) {
                hasEditPermission = request.isUserInRole("ADMIN");
                String principalName = request.getUserPrincipal().getName();
                User user = userService.findById(Long.valueOf(principalName));

                if (user != null) {
                    isFavorited = user.getUserFavGames().contains(game);
                }

            }
            model.addAttribute("hasEditPermission", hasEditPermission);
            model.addAttribute("isFavorited", isFavorited);

            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("token", csrfToken.getToken());
            }

        }
        return "detail_game_page";
    }

    @GetMapping("/game_form")
    public String showGameForm(HttpServletRequest request, Model model) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("token", csrfToken.getToken());
        }

        Game game = new Game();
        game.setGameName("");
        game.setGameDescription("");
        game.setGenre("");

        model.addAttribute("game", game);

        model.addAttribute("hasErrors", false);
        model.addAttribute("allErrors", new ArrayList<String>());

        return "game_form";
    }

    @GetMapping("/game_form/{id}")
    public String editGameForm(@PathVariable long id, Model model, HttpServletRequest request) {
        Game game = gameService.findById(id);
        if (game != null) {
            model.addAttribute("game", game);
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("token", csrfToken.getToken());
            }
            model.addAttribute("hasErrors", false);
            model.addAttribute("allErrors", new ArrayList<String>());
            return "game_form";
        }
        return "redirect:/games";
    }

    @PostMapping("/game_form")
    public String saveGame(HttpServletRequest request, Game game,
            @RequestParam("imageField") MultipartFile imageField,
            Model model) throws IOException {

        List<String> errorMessages = new ArrayList<>();
        boolean isNewGame = (game.getGameId() == null);

        errorMessages.addAll(gameService.validateContent(game));

        // img validation
        imageValidationService.validate(imageField, errorMessages, isNewGame);

        if (!errorMessages.isEmpty()) {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("token", csrfToken.getToken());
            }

            model.addAttribute("hasErrors", true);
            model.addAttribute("allErrors", errorMessages);
            model.addAttribute("game", game);

            return "game_form";
        }

        // saving game and image
        if (!imageField.isEmpty()) {
            Image image = imageService.createImage(imageField.getInputStream());
            game.setGameImage(image);
        }

        gameService.save(game);
        return "redirect:/games";
    }

    @PostMapping("/removeGame/{id}")
    public String removeGame(@PathVariable long id, HttpServletRequest request) {

        if (request.isUserInRole("ADMIN")) {
            gameService.delete(id);
        }
        return "redirect:/games";
    }

    @PostMapping("/game/{id}/toggle-fav")
    public String toggleFavorite(@PathVariable long id, HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return "redirect:/login";
        }

        Game game = gameService.findById(id);
        if (game == null) {
            return "redirect:/games";
        }

        String principalName = request.getUserPrincipal().getName();
        User user = null;

        if (principalName != null && principalName.matches("\\d+")) {
            user = userService.findById(Long.valueOf(principalName));
        }

        if (user == null) {
            user = userService.findByUserEmail(principalName).orElse(null);
        }

        if (user == null) {
            return "redirect:/login";
        }

        if (user.getUserFavGames().contains(game)) {
            user.removeFavoriteGame(game);
        } else {
            user.addFavoriteGame(game);
        }

        userService.save(user);

        return "redirect:/game/" + id;
    }

}
