import { Link } from "react-router";
import { Carousel, Container, Row, Col, Button, Image } from "react-bootstrap";
import type { Route } from "./+types/index";
import { getGames } from "~/services/games-service";
import { getNews } from "~/services/news-service";
import { getEvents } from "~/services/events-service";
import { ArrowRight, Envelope, HourglassSplit, InfoCircle, Instagram, Newspaper, TwitterX } from "react-bootstrap-icons";

export async function clientLoader() {

    const [gamesRes, newsRes, eventsRes] = await Promise.all([
        getGames("", "", 0, 0, 0),
        getNews("", "", 0),
        getEvents("", "", 0),
    ]);

    return {
        featuredGames: gamesRes.items.slice(0, 3),
        latestNews: newsRes.items.slice(0, 3),
        nextEvents: eventsRes.items.slice(0, 3),
    };
}

export default function Index({ loaderData }: Route.ComponentProps) {
    const { featuredGames, latestNews, nextEvents } = loaderData;

    return (
        <main className="main">
            {nextEvents.length > 0 && (
                <section id="next-events" className="next-events section light-background">
                    <Carousel
                        id="next-events-carousel"
                        variant="dark"
                        indicators={false}
                        controls={nextEvents.length > 1}
                    >
                        {nextEvents.map((event) => (
                            <Carousel.Item key={event.eventId}>
                                <Container className="project-details py-5">
                                    <Row className="align-items-center">
                                        <Col lg={5}>
                                            <div className="project-hero">
                                                <Image
                                                    src={`/api/v1/images/${event.eventImage?.id}/media`}
                                                    className="img-fluid rounded shadow"
                                                    alt={event.eventName}
                                                />
                                            </div>
                                        </Col>
                                        <Col lg={7}>
                                            <div className="project-info px-lg-4">
                                                <h1 className="project-title fw-bold">{event.eventName}</h1>

                                                <div className="d-inline-block bg-danger text-white rounded-pill px-3 py-1 mb-3 fw-bold">
                                                    <HourglassSplit className="me-2" />
                                                    {new Date(event.eventDate).toLocaleDateString()}
                                                </div>

                                                <div
                                                    className="project-description mb-3 text-muted"
                                                    style={{ display: "-webkit-box", WebkitLineClamp: 3, WebkitBoxOrient: "vertical", overflow: "hidden" }}
                                                >
                                                    {event.eventDescription}
                                                </div>

                                                <Link to={`/new/events/${event.eventId}`} className="btn btn-outline-dark rounded-pill shadow-sm">
                                                    <InfoCircle className="me-2" /> Ver detalles
                                                </Link>
                                            </div>
                                        </Col>
                                    </Row>
                                </Container>
                            </Carousel.Item>
                        ))}
                    </Carousel>
                </section>
            )}

            <section id="latest-news" className="projects section py-5">
                <Container>
                    <div className="section-header text-center mb-5">
                        <h2 className="fw-bold">Últimas Noticias</h2>
                        <p>Mantente al día de lo que ocurre en la asociación</p>
                    </div>

                    <Row className="gy-4">
                        {latestNews.length > 0 ? (
                            latestNews.map((news) => (
                                <Col lg={4} md={6} key={news.newId}>
                                    <div className="project-card h-100 shadow-sm border-0 card">
                                        <div className="project-image" style={{ height: "200px", overflow: "hidden" }}>
                                            {news.newImage ? (
                                                <Image src={`/api/v1/images/${news.newImage.id}/media`} className="img-fluid w-100 h-100" style={{ objectFit: "cover" }} />
                                            ) : (
                                                <div className="bg-secondary w-100 h-100 d-flex align-items-center justify-content-center text-white">
                                                    <Newspaper />
                                                </div>
                                            )}
                                        </div>
                                        <div className="project-info p-3">
                                            <div className="project-category mb-2 text-danger fw-bold small">{news.newTag}</div>
                                            <h4 className="project-title h5">{news.newName}</h4>
                                            <p className="text-muted small" style={{ display: "-webkit-box", WebkitLineClamp: 2, WebkitBoxOrient: "vertical", overflow: "hidden" }}>
                                                {news.newDescription}
                                            </p>
                                            <Link to={`/new/news/${news.newId}`} className="btn btn-outline-danger rounded-pill btn-sm mt-2">
                                                Leer más <ArrowRight />
                                            </Link>
                                        </div>
                                    </div>
                                </Col>
                            ))
                        ) : (
                            <Col lg={4} md={6}>
                                <div className="project-card shadow-sm border-0 p-4 text-center text-muted" style={{ border: "2px dashed #ccc !important" }}>
                                    <Newspaper />
                                    <h4>Próxima noticia</h4>
                                    <p>Estamos preparando novedades.</p>
                                </div>
                            </Col>
                        )}
                    </Row>

                    <div className="text-center mt-5">
                        <Link to="/new/news" className="btn btn-outline-dark rounded-pill px-4">
                            Todas las noticias <ArrowRight />
                        </Link>
                    </div>
                </Container>
            </section>

            <section id="featured-games" className="projects section light-background py-5">
                <Container>
                    <div className="section-header text-center mb-5">
                        <h2 className="fw-bold">Juegos Destacados</h2>
                        <p>Los últimos añadidos a la ludoteca</p>
                    </div>

                    <Row className="gy-4">
                        {featuredGames.map((game) => (
                            <Col lg={4} md={6} key={game.gameId}>
                                <div className="project-card h-100 shadow-sm border-0 card">
                                    <div className="project-image" style={{ height: "200px", overflow: "hidden" }}>
                                        <Image
                                            src={game.gameImage ? `/api/v1/images/${game.gameImage.id}/media` : "/images/default-game.png"}
                                            className="img-fluid w-100 h-100"
                                            style={{ objectFit: "cover" }}
                                        />
                                    </div>
                                    <div className="project-info p-3">
                                        <div className="project-category mb-2 text-danger fw-bold small">{game.genre}</div>
                                        <h4 className="project-title h5">{game.gameName}</h4>
                                        <p className="text-muted small" style={{ display: "-webkit-box", WebkitLineClamp: 2, WebkitBoxOrient: "vertical", overflow: "hidden" }}>
                                            {game.gameDescription}
                                        </p>
                                        <Link to={`/new/games/${game.gameId}`} className="btn btn-outline-danger rounded-pill btn-sm mt-2">
                                            Ver Detalles <ArrowRight />
                                        </Link>
                                    </div>
                                </div>
                            </Col>
                        ))}
                    </Row>

                    <div className="text-center mt-5">
                        <Link to="/new/games" className="btn btn-outline-dark rounded-pill px-4">
                            Ver ludoteca completa <ArrowRight />
                        </Link>
                    </div>
                </Container>
            </section>

            <section id="about" className="about section py-5">
                <Container>
                    <Row className="justify-content-between align-items-center">
                        <Col lg={7} className="order-2 order-lg-1">
                            <div className="content">
                                <h2 className="fw-bold mb-4">¿Quiénes somos?</h2>
                                <p className="lead-text mb-4" style={{ textAlign: "justify" }}>
                                    La Caverna del Dragón es el punto de encuentro del ocio alternativo en la URJC: somos una comunidad de
                                    estudiantes apasionados por el rol, los juegos de mesa y los videojuegos.
                                    <br></br>
                                    Organizamos todo tipo de eventos: desde nuestras famosas Jornadas Anuales de Introducción al
                                    Rol, hasta tardes de juegos de mesa, torneos competitivos y mucho más. ¡Ven a conocernos sin compromiso!
                                </p>

                                <p className="description-text mb-3" style={{ textAlign: "justify" }}>
                                    Puedes encontrarnos de lunes a viernes en el campus de Móstoles,
                                    Laboratorios Polivalentes II, Sótano 008.
                                </p>

                                <p className="description-text mb-2">Para más información, no dudes en escribirnos por correo o cualquiera de
                                    nuestras redes sociales:</p>

                                <div className="contact-info mt-4">
                                    <p><Envelope /> <a href="mailto:lcddoficial@gmail.com">lcddoficial@gmail.com</a></p>
                                    <p><TwitterX /> <a href="https://x.com/lcdd_urjc">@lcdd_urjc</a></p>
                                    <p><Instagram /> <a href="https://www.instagram.com/lcdd_urjc">@lcdd_urjc</a></p>
                                </div>
                            </div>
                        </Col>
                        <Col lg={5} className="order-1 order-lg-2 text-center">
                            <Image src="/public/poster.jpg" alt="La Caverna Del Dragón" className="img-fluid rounded shadow-lg" style={{ maxWidth: "85%" }} />
                        </Col>
                    </Row>
                </Container>
            </section>
        </main>
    );
}