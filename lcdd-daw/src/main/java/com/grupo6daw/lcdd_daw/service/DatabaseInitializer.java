package com.grupo6daw.lcdd_daw.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo6daw.lcdd_daw.model.Event;
import com.grupo6daw.lcdd_daw.model.Game;
import com.grupo6daw.lcdd_daw.model.Image;
import com.grupo6daw.lcdd_daw.model.New;
import com.grupo6daw.lcdd_daw.model.User;
import com.grupo6daw.lcdd_daw.repository.EventRepository;
import com.grupo6daw.lcdd_daw.repository.GameRepository;
import com.grupo6daw.lcdd_daw.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class DatabaseInitializer {

  @Autowired
  private EventRepository eventRepository;

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
  private GameRepository gameRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostConstruct
  public void init() throws IOException, URISyntaxException {

    // Sample News
    New new1 = new New("Vuelven las jornadas de rol a la URJC",
        "El campus de Móstoles acogerá la cuarta edición de 'Dangers & Dragons | Jornadas de Rol', un evento pensado tanto para aquellos que desean descubrir el emocionante mundo del rol como para los jugadores experimentados.",
        "Rol", true);
    setNewImage(new1, "/sample_images/news/newsImg.jpg");
    newService.save(new1);

    New new2 = new New("Gran Torneo de Magic: The Gathering",
        "La Caverna organiza su torneo trimestral de MTG en formato Commander. ¡Ven a demostrar quién manda en la mesa!",
        "Evento", true);
    setNewImage(new2, "/sample_images/news/magicTournament.jpg");
    newService.save(new2);

    New new3 = new New("Cambio de canal de reuniones: Nuevo canal de comunicación para los socios",
        "Atención socios: a partir de la próxima semana, las reuniones de administradores se realizarán en el canal de Discord 'REUNIONES ADMIN'",
        "Cambios", true);
    setNewImage(new3, "/sample_images/news/cambio_discord.jpg");
    newService.save(new3);

    New new4 = new New("Jornadas de Puertas Abiertas en Móstoles",
        "¿Aún no conoces la asociación? Ven a conocernos y prueba cualquiera de nuestros juegos de forma gratuita este viernes.",
        "Evento", true);
    setNewImage(new4, "/sample_images/news/poster.jpg");
    newService.save(new4);

    New new5 = new New("Actualización en el sistema de cuotas de socio",
        "Hemos simplificado el proceso de pago para el segundo cuatrimestre. Consulta los nuevos plazos y beneficios en la web.",
        "Cambios", true);
    setNewImage(new5, "/sample_images/news/cuotas.jpg");
    newService.save(new5);

    New new6 = new New("Resultados de las elecciones a la Junta",
        "Damos la bienvenida a los nuevos miembros de la directiva de La Caverna del Dragón para el curso actual.",
        "Cambios", true);
    setNewImage(new6, "/sample_images/news/junta.jpg");
    newService.save(new6);

    New new7 = new New("Maratón 12 Horas de Juegos de Mesa",
        "Un evento sin descanso donde sacaremos los juegos más largos y complejos de nuestra ludoteca.",
        "Evento", true);
    setNewImage(new7, "/sample_images/news/maraton.jpg");
    newService.save(new7);

    New new8 = new New("Nuevas adquisiciones para la ludoteca",
        "Hemos renovado nuestro catálogo con más de 10 títulos nuevos. ¡Pásate a probar las últimas novedades!",
        "Cambios", true);
    setNewImage(new8, "/sample_images/news/ludoteca.jpg");
    newService.save(new8);

    New new9 = new New("Especial Halloween: Noche de Pesadilla",
        "Prepárate para una velada de juegos de mesa de terror y roles ocultos. Habrá premios para los mejores.",
        "Evento", true);
    setNewImage(new9, "/sample_images/news/halloween.jpg");
    newService.save(new9);

    New new10 = new New("Clasificatorio para el Torneo Nacional de Catan",
        "¿Eres el rey del comercio en la URJC? Participa en nuestro torneo oficial y consigue una plaza para la final nacional.",
        "Evento", true);
    setNewImage(new10, "/sample_images/news/catan.jpg");
    newService.save(new10);

    New new11 = new New("Mejoras en la Sala de Juegos",
        "Hemos renovado las mesas y sillas de nuestra sede para que vuestras partidas de 8 horas sean mucho más cómodas.",
        "Cambios", true);
    setNewImage(new11, "/sample_images/news/sede_mejora.jpg");
    newService.save(new11);

    New new12 = new New("Playtesting: Probamos el sistema propio de La Caverna",
        "Varios socios han diseñado un sistema de rol ligero. Ven a probarlo y ayúdanos a pulir las reglas antes de su publicación.",
        "Rol", true);
    setNewImage(new12, "/sample_images/news/playtesting.jpg");
    newService.save(new12);

    // Sample Games
    if (gameRepository.findByGameName("MAGIC: THE GATHERING").isEmpty()) {
      Game game1 = new Game("MAGIC: THE GATHERING",
          "Juego de cartas coleccionables en el que cada jugador debe derrotar a su enemigo usando sus poderes: criaturas mágicas, artefactos y encantamientos, extrayedno el poder o maná de sus tierras, algunas también con sis propios poderes",
          2L, 4L, 25L, 60L, "Cartas");
      setGameImage(game1, "/sample_images/games/magic.jpg");
      gameService.save(game1);
    }

    if (gameRepository.findByGameName("CATAN").isEmpty()) {
      Game game2 = new Game("CATAN",
          "Los jugadores intentan ser la fuerza dominante en la isla de Catan construyendo pueblos, ciudades y carreteras. En cada turno se tiran dados para determinar qué recursos produce la isla.",
          3L, 4L, 10L, 90L, "Estrategia");
      setGameImage(game2, "/sample_images/games/catan.jpg");
      gameService.save(game2);
    }

    if (gameRepository.findByGameName("CARCASONNE").isEmpty()) {
      Game game3 = new Game("CARCASONNE",
          "Un juego de colocar losetas donde los jugadores crean un paisaje medieval. Puntuas colocando a tus seguidores (meeples) en ciudades, caminos, monasterios y campos.",
          2L, 5L, 7L, 45L, "Estrategia");
      setGameImage(game3, "/sample_images/games/carcassonne.jpg");
      gameService.save(game3);
    }

    if (gameRepository.findByGameName("VIRUS!").isEmpty()) {
      Game game4 = new Game("VIRUS!",
          "Un juego de cartas adictivo donde tu objetivo es formar un cuerpo sano mientras saboteas a tus rivales infectando sus órganos. Sé el primero en conseguir cuatro órganos sanos de distinto color para ganar.",
          2L, 6L, 8L, 20L, "Cartas");
      setGameImage(game4, "/sample_images/games/virus.jpg");
      gameService.save(game4);
    }

    if (gameRepository.findByGameName("TACO GATO CABRA QUESO PIZZA").isEmpty()) {
      Game game5 = new Game("TACO GATO CABRA QUESO PIZZA",
          "Juego de reflejos frenético. Los jugadores dicen una palabra de la secuencia mientras sueltan una carta; si la carta coincide con la palabra, ¡corre a palmear el centro! El último en hacerlo se queda con el mazo.",
          2L, 8L, 8L, 10L, "Cartas");
      setGameImage(game5, "/sample_images/games/tacogato.jpg");
      gameService.save(game5);
    }

    if (gameRepository.findByGameName("BANG!").isEmpty()) {
      Game game6 = new Game("BANG!",
          "El Salvaje Oeste cobra vida en este juego de roles ocultos. El Sheriff, sus Alguaciles, los Forajidos y el Renegado se enfrentan en un duelo de cartas donde nadie sabe con certeza quién es quién.",
          4L, 7L, 12L, 40L, "Cartas");
      setGameImage(game6, "/sample_images/games/bang.jpg");
      gameService.save(game6);
    }

    if (gameRepository.findByGameName("UNO").isEmpty()) {
      Game game7 = new Game("UNO",
          "El clásico juego de cartas de colores y números. Sé el primero en quedarte sin cartas, pero no olvides gritar '¡UNO!' cuando te quede solo una en la mano.",
          2L, 10L, 6L, 30L, "Cartas");
      setGameImage(game7, "/sample_images/games/uno.jpg");
      gameService.save(game7);
    }

    if (gameRepository.findByGameName("WORLD OF WARCRAFT").isEmpty()) {
      Game game8 = new Game("WORLD OF WARCRAFT",
          "Basado en el legendario MMORPG. Los jugadores eligen entre la Alianza o la Horda y viajan por Lordaeron completando misiones, derrotando criaturas y subiendo de nivel para enfrentarse al Señor de la Guerra final.",
          2L, 6L, 12L, 180L, "Estrategia");
      setGameImage(game8, "/sample_images/games/wow_boardgame.jpg");
      gameService.save(game8);
    }

    if (gameRepository.findByGameName("POKÉMON TCG").isEmpty()) {
      Game game9 = new Game("POKÉMON TCG",
          "Conviértete en un Entrenador Pokémon construyendo mazos alrededor de tus Pokémon favoritos. Combina energías, objetos y evoluciones para derrotar a los Pokémon del rival y hacerte con todas las Cartas de Premio.",
          2L, 2L, 6L, 30L, "Cartas");
      setGameImage(game9, "/sample_images/games/pokemon_tcg.jpg");
      gameService.save(game9);
    }

    if (gameRepository.findByGameName("SILENCE: ZOMBIE CITY").isEmpty()) {
      Game game10 = new Game("SILENCE: ZOMBIE CITY",
          "En una ciudad devastada, el ruido es tu peor enemigo. Los jugadores deben gestionar sus cartas de supervivientes y recursos intentando mantener el nivel de ruido al mínimo para no atraer a la horda. Un juego de estrategia y supervivencia con un arte espectacular.",
          2L, 4L, 10L, 30L, "Cartas");
      setGameImage(game10, "/sample_images/games/silence_zombie.jpg");
      gameService.save(game10);
    }

    if (gameRepository.findByGameName("SLAY THE SPIRE").isEmpty()) {
      Game game11 = new Game("SLAY THE SPIRE",
          "Un 'roguelike' cooperativo de construcción de mazos. Elige a tu héroe, crea un mazo único, enfréntate a criaturas extrañas y descubre reliquias de inmenso poder mientras intentas llegar a la cima de la aguja para derrotar al corazón.",
          1L, 4L, 12L, 90L, "Estrategia");
      setGameImage(game11, "/sample_images/games/slay_the_spire.jpg");
      gameService.save(game11);
    }

    // Sample Events

    if (eventRepository.findByEventName("Noche de Estrategia: Clásicos Modernos").isEmpty()) {
      Event e1 = new Event("Noche de Estrategia: Clásicos Modernos",
          "¿Eres más de construir carreteras en Catan o de conquistar ciudades en Carcassonne? Una velada dedicada a los juegos que definieron nuestra afición.",
          "Estrategia", true, LocalDate.now().plusDays(5), 20);
      setEventImage(e1, "/sample_images/events/event.jpg");
      eventService.save(e1);
    }

    if (eventRepository.findByEventName("Torneo Relámpago de Virus!").isEmpty()) {
      Event e2 = new Event("Torneo Relámpago de Virus!",
          "¡La epidemia no descansa! Ven a participar en este torneo rápido de eliminatorias directas. Partidas de 15 minutos donde el caos está asegurado.",
          "Cartas", true, LocalDate.now().plusDays(12), 32);
      setEventImage(e2, "/sample_images/events/virus_torneo.jpg");
      eventService.save(e2);
    }

    if (eventRepository.findByEventName("Introducción a Dungeons & Dragons").isEmpty()) {
      Event e3 = new Event("Introducción a Dungeons & Dragons",
          "¿Quieres empezar a rolear pero no sabes por dónde? Te ayudamos a crear tu primer personaje y jugaremos una aventura introductoria corta.",
          "Rol", true, LocalDate.now().plusDays(20), 10);
      setEventImage(e3, "/sample_images/events/beginner_role.jpg");
      eventService.save(e3);
    }

    if (eventRepository.findByEventName("Liga de Cartas: Magic & Pokémon").isEmpty()) {
      Event e4 = new Event("Liga de Cartas: Magic & Pokémon",
          "Primera jornada de nuestra liga inter-juegos. Trae tu mazo de Commander o tu baraja de Pokémon TCG y suma puntos para el ranking semestral.",
          "Cartas", true, LocalDate.now().plusWeeks(4), 40);
      setEventImage(e4, "/sample_images/events/pokemon_cup.jpg");
      eventService.save(e4);
    }

    if (eventRepository.findByEventName("Maratón Nocturna: Juegos Épicos").isEmpty()) {
      Event e5 = new Event("Maratón Nocturna: Juegos Épicos",
          "Sacamos la artillería pesada: Twilight Imperium, Nemesis y Scythe. Solo para valientes dispuestos a pasar una noche entera entre tableros.",
          "Estrategia", true, LocalDate.now().plusMonths(2), 15);
      setEventImage(e5, "/sample_images/events/maraton.jpg");
      eventService.save(e5);
    }

    if (eventRepository.findByEventName("Party Games Jam").isEmpty()) {
      Event e6 = new Event("Party Games Jam",
          "Tarde dedicada a juegos ligeros como Taco Gato, Exploding Kittens y Jungle Speed. Ideal para conocer gente nueva en la asociación.",
          "Juego de mesa", true, LocalDate.now().plusDays(3), 0);
      setEventImage(e6, "/sample_images/events/filler_afternoon.jpg");
      eventService.save(e6);
    }

    if (eventRepository.findByEventName("Torneo de Bang! El Regreso del Forajido").isEmpty()) {
      Event e7 = new Event("Torneo de Bang! El Regreso del Forajido",
          "Prepara tus balas. Nuevo torneo de roles ocultos en el Salvaje Oeste. Habrá premios especiales para el Sheriff y el Renegado que más duren.",
          "Cartas", true, LocalDate.now().plusDays(45), 28);
      setEventImage(e7, "/sample_images/events/bang_tournament.jpg");
      eventService.save(e7);
    }

    if (eventRepository.findByEventName("Campeonato de Carcassonne URJC").isEmpty()) {
      Event e8 = new Event("Campeonato de Carcassonne URJC",
          "Demuestra que eres el mejor arquitecto medieval en este torneo de Carcassonne con todas sus expansiones. Clasificatorio interno.",
          "Estrategia", true, LocalDate.now().plusDays(60), 24);
      setEventImage(e8, "/sample_images/events/carcassonne_tourney.jpg");
      eventService.save(e8);
    }

    if (eventRepository.findByEventName("Retorno al Pasado: Yu-Gi-Oh! Retro").isEmpty()) {
      Event e9 = new Event("Retorno al Pasado: Yu-Gi-Oh! Retro",
          "¿Echas de menos las invocaciones por sacrificio y las fusiones clásicas? Torneo especial con lista de cartas de la era dorada.",
          "Cartas", true, LocalDate.now().plusDays(15), 16);
      setEventImage(e9, "/sample_images/events/ygo_goat.jpg");
      eventService.save(e9);
    }

    if (eventRepository.findByEventName("Gymkana Navideña").isEmpty()) {
      Event e10 = new Event("Gymkana Navideña",
          "Pruebas de habilidad, memoria y rapidez mental usando componentes de juegos de mesa. ¡Ven con tu equipo y gana la cesta de juegos!",
          "Juego de mesa", true, LocalDate.of(2026, 12, 20), 50);
      setEventImage(e10, "/sample_images/events/gymkana_event.jpg");
      eventService.save(e10);
    }

    if (eventRepository.findByEventName("Torneo de Catan Universitario").isEmpty()) {
      Event e11 = new Event("Torneo de Catan Universitario",
          "Consigue madera, ovejas y ladrillos. Conviértete en el mejor colono de la universidad en nuestro torneo de Catan con plazas limitadas.",
          "Estrategia", true, LocalDate.now().plusDays(25), 16);
      setEventImage(e11, "/sample_images/news/catan.jpg");
      eventService.save(e11);
    }


    // Sample users
    userRepository.save(new User("user", "user", "user", "interests", "user@user.com",
        passwordEncoder.encode("pass"), "REGISTERED_USER"));
    userRepository.save(new User("admin", "admin", "admin", "interests", "admin@admin.com",
        passwordEncoder.encode("adminpass"), "REGISTERED_USER", "ADMIN"));
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
