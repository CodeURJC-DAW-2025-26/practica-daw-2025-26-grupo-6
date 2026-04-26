import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { Alert, Image, Button, Modal, ListGroup } from "react-bootstrap";
import type { Route } from "./+types/events-detail";
import { getEvent, removeEvent } from "~/services/events-service";
import { useUserStore } from "~/stores/user-store";
import { BoxArrowUpRight, CalendarEvent, CardChecklist, EmojiFrown, EnvelopeAt, EyeFill, Newspaper, PencilSquare, People, PersonBadge, PersonDashFill, PersonPlusFill, SlashCircle, TagFill, Trash } from "react-bootstrap-icons";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    return await getEvent(Number(params.id));
}

export default function EventsDetail({ loaderData }: Route.ComponentProps) {
    let { user } = useUserStore();
    const event = loaderData;
    const navigate = useNavigate();

    const [deleteError, setDeleteError] = useState<string | null>(null);
    const [isPendingDelete, setPendingDelete] = useState(false);
    const [isDeleteDialogOpen, setDeleteDialogOpen] = useState(false);

    const formattedDate = event.eventDate
        ? new Intl.DateTimeFormat("es-ES", { day: "2-digit", month: "2-digit", year: "numeric" }).format(new Date(event.eventDate))
        : "";

    const participantsCount = event.participants?.length ?? event.participantCount ?? 0;

    async function handleDelete() {
        setPendingDelete(true);
        setDeleteError(null);
        try {
            await removeEvent(event.eventId);
            navigate("/new/events");
        } catch (err) {
            console.error(err);
            setDeleteError("Hubo un error al borrar el evento.");
            setPendingDelete(false);
        }
    }

    function handleOpenDeleteDialog() {
        setDeleteDialogOpen(true);
    }

    function handleCloseDeleteDialog() {
        if (isPendingDelete) {
            return;
        }
        setDeleteDialogOpen(false);
        setDeleteError(null);
    }

    const [isListDialogOpen, setListDialogOpen] = useState(false);
    function handleShowListModal() {
        setListDialogOpen(true);
    }

    function handleCloseListModal() {
        setListDialogOpen(false);
    }

    return (
        <>
            <main className="main">
                <section id="project-details" className="project-details section">
                    <div className="container" data-aos="fade-up" data-aos-delay="100">
                        <div className="row align-items-start">
                            <div className="col-lg-5 col-md-12">
                                <div
                                    className="project-hero-container"
                                    style={{
                                        backgroundColor: "#f4f4f4",
                                        borderRadius: "12px",
                                        overflow: "hidden",
                                        display: "flex",
                                        justifyContent: "center",
                                        alignItems: "center",
                                        minHeight: "450px",
                                        maxHeight: "650px",
                                        width: "100%",
                                        position: "relative",
                                    }}
                                >
                                    {event.eventImage ? (
                                        <Image
                                            src={`/api/v1/images/${event.eventImage.id}/media`}
                                            className="img-fluid"
                                            alt={event.eventName}
                                            style={{
                                                maxWidth: "100%",
                                                maxHeight: "650px",
                                                width: "auto",
                                                height: "auto",
                                                display: "block",
                                                objectFit: "contain",
                                            }}
                                        />
                                    ) : (
                                        <div className="bg-secondary w-100 h-100 d-flex align-items-center justify-content-center text-white">
                                            <Newspaper />
                                        </div>
                                    )}

                                    <div style={{ position: "absolute", top: "15px", right: "15px", zIndex: 10 }}>
                                        <span
                                            className="badge rounded-pill px-3 py-2 shadow"
                                            style={{
                                                backgroundColor: "#a71b12",
                                                color: "white",
                                                fontWeight: 500,
                                                fontSize: "0.85rem",
                                                border: "1px solid rgba(255,255,255,0.2)",
                                            }}
                                        >
                                            <TagFill className="me-2" /> {event.eventTag}
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <div className="col-lg-7 col-md-12 ps-lg-5 mt-4 mt-lg-0">
                                <div className="project-info p-4 bg-white rounded-4 shadow-sm border">
                                    <h2 className="fw-bold mb-3" style={{ color: "#212529" }}>
                                        {event.eventName}
                                    </h2>

                                    <div className="d-flex flex-wrap gap-4 mb-4 pb-3 border-bottom text-muted" style={{ fontSize: "0.95rem" }}>
                                        <div className="d-flex align-items-center">
                                            <CalendarEvent className="me-2" />
                                            <span className="fw-medium text-dark">{formattedDate}</span>
                                        </div>

                                        <div className="d-flex align-items-center">
                                            <People className="me-2" />
                                            <span className="fw-medium text-dark">
                                                Apuntados: <span className="text-danger fw-bold">{participantsCount}</span>
                                                {event.maxParticipants && (
                                                    <span> / {event.maxParticipants}</span>
                                                )}
                                                {!event.maxParticipants && (
                                                    <span> (Aforo ilimitado)</span>
                                                )}
                                            </span>

                                            {(user?.userRoles?.includes("ADMIN") || user?.userOwnEvents?.some(e => e.eventId === event.eventId)) && (
                                                <Button
                                                    type="button"
                                                    className="btn btn-sm rounded-pill ms-3 py-1 px-3 shadow-sm"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#attendeesModal"
                                                    onClick={handleShowListModal}
                                                    style={{
                                                        backgroundColor: "#ffffff",
                                                        border: "2px solid #a71b12",
                                                        color: "#a71b12",
                                                        transition: "all 0.2s ease-in-out",
                                                    }}
                                                    onMouseOver={(e) => {
                                                        e.currentTarget.style.backgroundColor = "#a71b12";
                                                        e.currentTarget.style.borderColor = "#a71b12";
                                                        e.currentTarget.style.color = "#ffffff";
                                                    }}
                                                    onMouseOut={(e) => {
                                                        e.currentTarget.style.backgroundColor = "#ffffff";
                                                        e.currentTarget.style.borderColor = "#a71b12";
                                                        e.currentTarget.style.color = "#a71b12";
                                                    }}
                                                >
                                                    <EyeFill className="me-2" /> Ver lista
                                                </Button>
                                            )}

                                        </div>
                                    </div>

                                    <div className="project-description mb-4">
                                        <p className="text-secondary mb-0" style={{ fontSize: "1.05rem", lineHeight: 1.6, overflowWrap: "break-word", wordWrap: "break-word" }}>
                                            {event.eventDescription}
                                        </p>
                                    </div>

                                    <div className="d-flex flex-wrap align-items-center justify-content-between mt-5 pt-3 border-top gap-3">
                                        <div className="d-flex flex-wrap align-items-center gap-2">
                                            {event.requiresRegistration && (
                                                <a href={event.link} target="_blank" rel="noopener noreferrer"
                                                    className="btn btn-danger rounded-pill px-4 shadow-sm" style={{ backgroundColor: "#a71b12", border: "none" }}>
                                                    <BoxArrowUpRight className="me-2" /> Página de registro
                                                </a>
                                            )}

                                            <form method="post" className="m-0">
                                                {user?.userRegisteredEvents.some(e => e.eventId === event.eventId) && (
                                                    <button type="submit" className="btn btn-outline-dark rounded-pill px-4 shadow-sm">
                                                        <PersonDashFill className="me-2" /> Desapuntarme
                                                    </button>
                                                )}

                                                {user && !user?.userRegisteredEvents.some(e => e.eventId === event.eventId) && event.maxParticipants === participantsCount && (
                                                    <button type="button" className="btn btn-secondary rounded-pill px-4 shadow-sm" disabled>
                                                        <SlashCircle /> Aforo completo
                                                    </button>
                                                )}

                                                {user && !user?.userRegisteredEvents.some(e => e.eventId === event.eventId) && event.maxParticipants !== participantsCount && (
                                                    <button type="submit" className="btn btn-dark rounded-pill px-4 shadow-sm">
                                                        <PersonPlusFill className="me-2" /> Apuntarme
                                                    </button>
                                                )}
                                            </form>

                                            {(user?.userRoles?.includes("ADMIN") || user?.userOwnEvents?.some(e => e.eventId === event.eventId)) && (
                                                <div className="d-flex flex-wrap align-items-center gap-3">
                                                    <Link
                                                        className="btn btn-link text-primary text-decoration-none p-0"
                                                        to={`/new/events-edit/${event.eventId}`}
                                                    >
                                                        <PencilSquare className="me-2" /> Modificar evento
                                                    </Link>

                                                    <Button
                                                        type="button"
                                                        className="btn btn-link text-danger text-decoration-none p-0"
                                                        style={{ boxShadow: "none" }}
                                                        onClick={handleOpenDeleteDialog}
                                                        disabled={isPendingDelete}
                                                    >
                                                        <Trash /> Eliminar
                                                    </Button>
                                                </div>
                                            )}
                                        </div>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </main >
            <Modal show={isListDialogOpen} onHide={handleCloseListModal}>
                <Modal.Header closeButton>
                    <CardChecklist className="me-2" />Lista de Asistentes
                </Modal.Header>
                <Modal.Body>
                    <div className="modal-body p-0">
                        <ListGroup className="list-group list-group-flush">
                            {event.participants?.map((participant) => (
                                <ListGroup.Item className="d-flex justify-content-between align-items-start p-3" key={participant.userId}>
                                    <div className="ms-2 me-auto">
                                        <div className="fw-bold" style={{ fontSize: '1.1rem' }}>
                                            <Link to={`new/user/${participant.userId}`} className="text-decoration-none"
                                                style={{ color: '#a71b12', transition: 'color 0.2s ease-in-out' }} onMouseOver={(e) => e.currentTarget.style.color = '#890f00'}
                                                onMouseOut={(e) => e.currentTarget.style.color = '#a71b12'}>
                                                <PersonBadge className="me-2" /> {participant.userNickname}
                                            </Link>
                                        </div>
                                        <div className="text-muted" style={{ fontSize: '0.9rem' }}>
                                            <EnvelopeAt className="me-2" />
                                            <Link to={`mailto:${participant.userEmail}`} className="text-decoration-none text-secondary">
                                                {participant.userEmail}
                                            </Link>
                                        </div>
                                    </div>
                                </ListGroup.Item>
                            ))}
                            {event.participants?.length === 0 && (
                                <ListGroup.Item className="p-4 text-center text-muted bg-light">
                                    <EmojiFrown className="me-2" />
                                    Aún no hay nadie inscrito en este evento.
                                </ListGroup.Item>
                            )}
                        </ListGroup>
                    </div>
                </Modal.Body>
            </Modal>
            <Modal show={isDeleteDialogOpen} onHide={handleCloseDeleteDialog}>
                <Modal.Header closeButton>
                    <Modal.Title>Eliminar Evento</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>
                        ¿Estás seguro de que quieres eliminar <b>"{event.eventName}"</b>?
                    </p>
                    <p className="text-muted">Esta acción no se puede deshacer.</p>
                    {deleteError && <Alert variant="danger">{deleteError}</Alert>}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseDeleteDialog} disabled={isPendingDelete}>
                        Cancelar
                    </Button>
                    <Button variant="danger" onClick={handleDelete} disabled={isPendingDelete}>
                        {isPendingDelete ? "Deleting..." : "Borrar"}
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}
