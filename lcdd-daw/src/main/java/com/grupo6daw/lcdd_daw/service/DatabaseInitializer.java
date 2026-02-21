package com.grupo6daw.lcdd_daw.service;

import java.io.IOException;
import java.net.URISyntaxException;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

@Service
public class DatabaseInitializer {

	@Autowired
	private NewService newService;

	@Autowired
	private GameService gameService;

	@Autowired
	private EventService eventService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserRepository userRepository;

		@Autowired
	private PasswordEncoder passwordEncoder;


	@PostConstruct
	public void init() throws IOException, URISyntaxException {

		// Sample News
		New new1 = new New("Vuelven las jornadas de rol a la URJC",
				"El campus de Móstoles acogerá la cuarta edición de 'Dangers & Dragons | Jornadas de Rol', un evento pensado tanto para aquellos que desean descubrir el emocionante mundo del rol como para los jugadores experimentados.",
				"Rol");
		setNewImage(new1, "/sample_images/news/newsImg.jpg");
		newService.save(new1);

		// Sample Games
		Game game1 = new Game("MAGIC: THE GATHERING",
				"Juego de cartas coleccionables en el que cada jugador debe derrotar a su enemigo usando sus poderes: criaturas mágicas, artefactos y encantamientos, extrayedno el poder o maná de sus tierras, algunas también con sis propios poderes",
				2L, 4L, 25L, 60L, "Cartas");
		setGameImage(game1, "/sample_images/games/magic.jpg");
		gameService.save(game1);

		// Sample Events
		Event event1 = new Event("Evento de juegos de mesa",
				"🎲🧩♟ ¿Estáis preparados una vez más para el evento de juegos de mesa de la LCDD? 🎲🧩♟\nLa asociación “La Caverna del Dragón” en colaboración con la URJC organiza otra edición del evento de juegos de mesa para que venga a jugar y divertirse todo el mundo.", "Cartas");
		setEventImage(event1, "/sample_images/events/event.jpg");
		eventService.save(event1);

		// Sample users
		userRepository.save(new User("user", "user", "user", "interests", "user@user.com", passwordEncoder.encode("pass"), "REGISTERED_USER"));
		userRepository.save(new User("admin", "admin", "admin", "interests", "admin@admin.com", passwordEncoder.encode("adminpass"), "REGISTERED_USER", "ADMIN"));
	}

	public void setNewImage(New sampleNew, String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);
		Image createdImage = imageService.createImage(image.getInputStream());
		sampleNew.setNewImage(createdImage);
	}

	public void setGameImage(Game sampleGame, String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);
		Image createdImage = imageService.createImage(image.getInputStream());
		sampleGame.setGameImage(createdImage);
	}

	public void setEventImage(Event sampleEvent, String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);
		Image createdImage = imageService.createImage(image.getInputStream());
		sampleEvent.setEventImage(createdImage);
	}
}
