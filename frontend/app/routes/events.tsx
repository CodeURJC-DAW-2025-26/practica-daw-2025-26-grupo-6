import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router";
import type { Route } from "./+types/events";
import { getEvents } from "~/services/events-service";
import { Button, Form, Spinner } from "react-bootstrap";
import { useUserStore } from "~/stores/user-store";
import { JournalX, Newspaper, PlusCircle, Search } from "react-bootstrap-icons";

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
    const url = new URL(request.url);
    const name = url.searchParams.get("name") ?? "";
    const tag = url.searchParams.get("tag") ?? "";
    const page = Number(url.searchParams.get("page") ?? "0");
    const result = await getEvents(name, tag, page);

    return { name, tag, items: result.items, hasNext: result.hasNext };
}

export default function Events({ loaderData }: Route.ComponentProps) {
    const [events, setEvents] = useState(loaderData.items);
    const [hasNext, setHasNext] = useState(loaderData.hasNext);
    const [currentPage, setCurrentPage] = useState(1);
    const [isLoadingMore, setIsLoadingMore] = useState(false);
    const [loadMoreError, setLoadMoreError] = useState(false);

    useEffect(() => {
        setEvents(loaderData.items);
        setHasNext(loaderData.hasNext);
        setCurrentPage(1);
        setIsLoadingMore(false);
        setLoadMoreError(false);
    }, [loaderData]);

    const handleLoadMore = async () => {
        if (isLoadingMore || !hasNext) {
            return;
        }

        setIsLoadingMore(true);
        setLoadMoreError(false);

        try {
            const result = await getEvents(loaderData.name, loaderData.tag, currentPage);
            setEvents((previousEvents) => [...previousEvents, ...result.items]);
            setHasNext(result.hasNext);

            if (result.hasNext) {
                setCurrentPage((previousPage) => previousPage + 1);
            }
        } catch {
            setLoadMoreError(true);
        } finally {
            setIsLoadingMore(false);
        }
    };

    let { user } = useUserStore();

    const navigate = useNavigate();

    return (
        <main className="main">
            <section id="projects" className="projects section">
                <div className="container" data-aos="fade-up">
                    <div className="form-intro text-center mb-5 mt-4">
                        <h1
                            style={{
                                fontWeight: 700,
                                color: "#212529",
                                textTransform: "uppercase",
                            }}
                        >
                            Próximos Eventos
                        </h1>
                        <p className="text-muted">
                            Participa en nuestras partidas, torneos y quedadas
                        </p>
                        <hr
                            style={{
                                width: "50px",
                                margin: "20px auto",
                                borderTop: "3px solid #a71b12",
                                opacity: 1,
                            }}
                        />
                    </div>

                    <div className="card shadow-sm border-0 p-3 bg-light mb-5">
                        <Form method="get" action="/events">
                            <div className="row g-2 align-items-center">
                                <div className="col-lg-6 col-md-5">
                                    <Form.Control
                                        name="name"
                                        className="form-control"
                                        type="search"
                                        placeholder="Buscar por titulo..."
                                        defaultValue={loaderData.name}
                                    />
                                </div>
                                <div className="col-lg-2 col-md-3">
                                    <Form.Select name="tag" className="form-select" defaultValue={loaderData.tag}>
                                        <option value="">Categoría</option>
                                        <option value="Juegos de mesa">Juegos de mesa</option>
                                        <option value="Rol">Rol</option>
                                        <option value="Cartas">Cartas</option>
                                    </Form.Select>
                                </div>

                                <div className="col-lg-2 col-md-2">
                                    <Button
                                        className="btn_lcdd w-100 d-flex align-items-center justify-content-center"
                                        type="submit"
                                        style={{
                                            height: "38px",
                                            border: "none",
                                            backgroundColor: "#a71b12",
                                        }}
                                    >
                                        <Search className="me-2" /> Buscar
                                    </Button>
                                </div>

                                {user && (
                                    <div className="col-lg-2 col-md-2">
                                        <Button
                                            onClick={() => navigate("/events-create")}
                                            className="btn_lcdd_black w-100 text-decoration-none d-flex align-items-center justify-content-center"
                                            style={{ height: "38px" }}
                                        >
                                            <PlusCircle className="me-2" /> Crear evento
                                        </Button>
                                    </div>
                                )}

                            </div>
                        </Form>
                    </div>

                    <div id="news-container" className="row gy-4">
                        {events.length > 0 ? (
                            events.map((item) => (
                                <div key={item.eventId} className="col-lg-4 col-md-6 news-item">
                                    <div className="project-card h-100 shadow-sm border-0">
                                        <Link to={`/events/${item.eventId}`} className="text-decoration-none">
                                            <div className="project-image" style={{ height: "220px", overflow: "hidden" }}>
                                                {item.eventImage ? (
                                                    <img
                                                        src={`/api/v1/images/${item.eventImage.id}/media`}
                                                        alt={item.eventName}
                                                        className="img-fluid w-100 h-100"
                                                        style={{ objectFit: "cover" }}
                                                    />
                                                ) : (
                                                    <div className="bg-secondary w-100 h-100 d-flex align-items-center justify-content-center text-white">
                                                        <Newspaper />
                                                    </div>
                                                )}
                                            </div>
                                            <div className="project-info p-3">
                                                <div className="project-category mb-2 fw-bold" style={{ color: "#a71b12", fontSize: "0.8rem" }}>
                                                    {item.eventTag}
                                                </div>
                                                <h4 className="project-title text-dark">{item.eventName}</h4>
                                                <p
                                                    className="project-description text-muted small"
                                                    style={{
                                                        display: "-webkit-box",
                                                        WebkitLineClamp: 3,
                                                        WebkitBoxOrient: "vertical",
                                                        overflow: "hidden",
                                                    }}
                                                >
                                                    {item.eventDescription}
                                                </p>
                                            </div>
                                        </Link>
                                    </div>
                                </div>
                            ))
                        ) : (
                            <div className="col-12 text-center py-5">
                                <JournalX />
                                <p className="mt-3 text-muted">
                                    No hay eventos que coincidan con tu busqueda.
                                </p>
                                <Link to="/events" className="btn btn-link text-danger">
                                    Ver todos los eventos
                                </Link>
                            </div>
                        )}
                    </div>

                    {events.length > 0 && hasNext && (
                        <div className="text-center mt-5 mb-5">
                            <Button
                                id="load-more-events-btn"
                                className="btn-load-more"
                                onClick={handleLoadMore}
                                disabled={isLoadingMore}
                                type="button"
                            >
                                {isLoadingMore ? (
                                    <>
                                        <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" className="me-2" />
                                        Cargando...
                                    </>
                                ) : loadMoreError ? (
                                    "Error. Reintentar"
                                ) : (
                                    "Cargar más eventos"
                                )}
                            </Button>
                        </div>
                    )}
                </div>
            </section>
        </main>
    );
}
