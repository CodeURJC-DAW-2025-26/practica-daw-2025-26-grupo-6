import { useEffect, useState } from "react";
import * as AdminService from "../services/admin-service";
import type { PendingContent } from "../services/admin-service";
import type UserDTO from "~/dtos/UserDTO";
import AdminChart from "../components/AdminCharts";
import { CalendarCheck, ClipboardCheck, Controller, GraphUp, Newspaper, People } from "react-bootstrap-icons";
import { Breadcrumb, BreadcrumbItem, Button, Image, Modal, Spinner } from "react-bootstrap";
import { Link } from "react-router";
import type { Route } from "./+types/admin";
import { requireAdminUser } from "~/services/route-guards";

export async function clientLoader(_: Route.ClientLoaderArgs) {
    await requireAdminUser();
    return null;
}

export default function Admin() {
    const [pending, setPending] = useState<PendingContent>({ news: [], events: [] });
    const [users, setUsers] = useState<UserDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({ games: [], events: [] });
    const [isRejectModalOpen, setRejectModalOpen] = useState(false);
    const [itemToReject, setItemToReject] = useState<{ id: number, type: 'event' | 'news', name: string } | null>(null);


    const [processingAction, setProcessingAction] = useState<string | null>(null);

    useEffect(() => {
        const loadAllData = async () => {
            try {
                const [pendingRes, usersRes, gamesRes, eventsRes] = await Promise.all([
                    AdminService.getPendingContent(),
                    AdminService.getAllUsers(),
                    AdminService.getTopGames(),
                    AdminService.getTopEvents()
                ]);
                setPending(pendingRes);
                setUsers(usersRes);
                setStats({ games: gamesRes, events: eventsRes });
            } catch (error) {
                console.error("Error al cargar datos de administración:", error);
            } finally {
                setLoading(false);
            }
        };
        loadAllData();
    }, []);

    const handleApproveEvent = async (id: number) => {
        setProcessingAction(`approve-event-${id}`);
        if (await AdminService.approveEvent(id)) {
            setPending(prev => ({ ...prev, events: prev.events.filter(e => e.eventId !== id) }));
        }
        setProcessingAction(null);
    };

    const handleApproveNews = async (id: number) => {
        setProcessingAction(`approve-news-${id}`);
        if (await AdminService.approveNews(id)) {
            setPending(prev => ({ ...prev, news: prev.news.filter(n => n.newId !== id) }));
        }
        setProcessingAction(null);

    }; const prepareRejectEvent = (id: number, name: string) => {
        setItemToReject({ id, type: 'event', name });
        setRejectModalOpen(true);
    };

    const prepareRejectNews = (id: number, name: string) => {
        setItemToReject({ id, type: 'news', name });
        setRejectModalOpen(true);
    };

    const handleConfirmReject = async () => {
        if (!itemToReject) return;

        setProcessingAction(`reject-${itemToReject.type}-${itemToReject.id}`);

        const success = itemToReject.type === 'event'
            ? await AdminService.rejectEvent(itemToReject.id)
            : await AdminService.rejectNews(itemToReject.id);

        if (success) {
            setPending(prev => ({
                ...prev,
                [itemToReject.type === 'event' ? 'events' : 'news']:
                    (itemToReject.type === 'event' ? prev.events : prev.news).filter(item =>
                        (itemToReject.type === 'event' ? (item as any).eventId : (item as any).newId) !== itemToReject.id
                    )
            }));
        }

        setProcessingAction(null);
        setRejectModalOpen(false);
    };

    return (
        <>
            <main className="main">
                <section id="admin-dashboard" className="quote section">
                    <div className="container">

                        <div className="form-intro text-center mb-5">
                            <h1 style={{ fontWeight: 700, color: "#212529" }}>Panel de Administración</h1>
                            <nav aria-label="breadcrumb">
                                <Breadcrumb className="breadcrumb justify-content-center">
                                    <BreadcrumbItem className="breadcrumb-item"><Link to="/" className="text-decoration-none text-muted">Inicio</Link></BreadcrumbItem>
                                    <BreadcrumbItem className="breadcrumb-item active" style={{ color: "#a71b12", fontWeight: 600 }}>Administración</BreadcrumbItem>
                                </Breadcrumb>
                            </nav>
                        </div>

                        <div className="admin-section mb-5">
                            <div className="d-flex align-items-center mb-4 border-bottom pb-2">
                                <ClipboardCheck className="me-2" />
                                <h3 className="mb-0" style={{ fontWeight: 700 }}>Solicitudes Pendientes</h3>
                            </div>

                            <div className="row gy-4">
                                <h6 className="fw-bold mb-3">Eventos</h6>
                                {pending.events.length > 0 ? pending.events.map(event => (
                                    <div key={event.eventId} className="col-lg-4 col-md-6">
                                        <div className="project-card shadow-sm border-0 h-100">
                                            <Link className="d-block text-decoration-none" to={`/events/${event.eventId}`}>
                                                <div className="project-image" style={{ height: "180px", overflow: "hidden" }}>
                                                    <Image
                                                        src={event.eventImage ? `/api/v1/images/${event.eventImage.id}/media` : "/img/placeholder.png"}
                                                        alt="Evento"
                                                        className="img-fluid w-100 h-100"
                                                        style={{ objectFit: "cover" }}
                                                    />
                                                </div>
                                                <div className="project-info p-3">
                                                    <h4 className="project-title text-dark">{event.eventName}</h4>
                                                </div>
                                            </Link>
                                            <div className="project-image" style={{ height: "180px", overflow: "hidden" }}>
                                                <Image
                                                    src={event.eventImage ? `/api/v1/images/${event.eventImage.id}/media` : "/img/placeholder.png"}
                                                    alt="Evento"
                                                    className="img-fluid w-100 h-100"
                                                    style={{ objectFit: "cover" }}
                                                />
                                            </div>
                                            <div className="project-info p-3">
                                                <h4 className="project-title text-dark">{event.eventName}</h4>
                                            </div>

                                            <div className="p-3 pt-0 d-flex gap-2">
                                                <Button
                                                    onClick={() => handleApproveEvent(event.eventId)}
                                                    className="btn btn-success w-100 rounded-pill btn-sm d-flex justify-content-center align-items-center"
                                                    disabled={processingAction !== null}
                                                >
                                                    {processingAction === `approve-event-${event.eventId}` ? <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" /> : "Aprobar"}
                                                </Button>
                                                <Button
                                                    onClick={() => prepareRejectEvent(event.eventId, event.eventName)}
                                                    className="btn btn-danger w-100 rounded-pill btn-sm..."
                                                >
                                                    Rechazar
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                )) : (
                                    <div className="col-12 text-center py-4 bg-light rounded-3 border border-dashed mb-4">
                                        <p className="mb-0 text-muted">No hay eventos pendientes.</p>
                                    </div>
                                )}

                                <h6 className="fw-bold mb-3">Noticias</h6>
                                {pending.news.length > 0 ? pending.news.map(n => (
                                    <div key={n.newId} className="col-lg-4 col-md-6">
                                        <div className="project-card shadow-sm border-0 h-100">
                                            <Link className="d-block text-decoration-none" to={`/news/${n.newId}`}>
                                                <div className="project-image" style={{ height: "180px", overflow: "hidden" }}>
                                                    {n.newImage ? (
                                                        <img
                                                            src={n.newImage ? `/api/v1/images/${n.newImage.id}/media` : "/img/placeholder.png"}
                                                            alt="Noticia"
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
                                                    <h4 className="project-title text-dark">{n.newName}</h4>
                                                </div>
                                            </Link>
                                            <div className="p-3 pt-0 d-flex gap-2">
                                                <Button
                                                    onClick={() => handleApproveNews(n.newId)}
                                                    className="btn btn-success w-100 rounded-pill btn-sm d-flex justify-content-center align-items-center"
                                                    disabled={processingAction !== null}
                                                >
                                                    {processingAction === `approve-news-${n.newId}` ? <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" /> : "Aprobar"}
                                                </Button>
                                                <Button
                                                    onClick={() => prepareRejectNews(n.newId, n.newName)}
                                                    className="btn btn-danger w-100 rounded-pill btn-sm..."
                                                >
                                                    Rechazar
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                )) : (
                                    <div className="col-12 text-center py-4 bg-light rounded-3 border border-dashed">
                                        <p className="mb-0 text-muted">No hay noticias pendientes.</p>
                                    </div>
                                )}
                            </div>
                        </div>

                        <div className="admin-section mb-5">
                            <div className="d-flex align-items-center mb-4 border-bottom pb-2">
                                <People className="me-2" />
                                <h3 className="mb-0" style={{ fontWeight: 700 }}>Usuarios Registrados</h3>
                            </div>
                            <div className="row gy-3">
                                {users.length > 0 ? users.map(user => (
                                    <div key={user.userId} className="col-xl-3 col-lg-4 col-md-6">
                                        <Link to={`/users/${user.userId}`} className="d-flex align-items-center p-2 shadow-sm border rounded text-decoration-none bg-white h-100">
                                            <div className="flex-shrink-0" style={{ width: "50px", height: "50px" }}>
                                                <Image
                                                    src={user.userImage ? `/api/v1/users/${user.userId}/image` : "/img/person/Portrait_Placeholder.png"}
                                                    className="rounded-circle w-100 h-100"
                                                    style={{ objectFit: "cover" }}
                                                />
                                            </div>
                                            <div className="flex-grow-1 ms-3 overflow-hidden">
                                                <h6 className="mb-0 text-dark fw-bold text-truncate">{user.userNickname}</h6>
                                                <small className="text-muted text-truncate d-block">{user.userEmail}</small>
                                            </div>
                                        </Link>
                                    </div>
                                )) : (
                                    <div className="col-12 text-center py-3 text-muted">No hay usuarios para mostrar.</div>
                                )}
                            </div>
                        </div>

                        <div className="admin-section">
                            <div className="d-flex align-items-center mb-4 border-bottom pb-2">
                                <GraphUp className="me-2" />
                                <h3 className="mb-0" style={{ fontWeight: 700 }}>Estadísticas</h3>
                            </div>
                            <div className="row gy-4">
                                <div className="col-lg-6">
                                    <div className="card border-0 shadow-sm p-3">
                                        <h6 className="fw-bold mb-3"><Controller className="me-2" />Juegos Favoritos</h6>
                                        <AdminChart
                                            title="Juegos Favoritos"
                                            data={stats.games}
                                            labelKey="gameName"
                                            valueKey="favCount"
                                            color="#a71b12"
                                            canvasId="gamesFavChart"
                                        />
                                    </div>
                                </div>
                                <div className="col-lg-6">
                                    <div className="card border-0 shadow-sm p-3">
                                        <h6 className="fw-bold mb-3"><CalendarCheck className="me-2" />Participación en Eventos</h6>
                                        <AdminChart
                                            title="Participantes"
                                            data={stats.events}
                                            labelKey="eventName"
                                            valueKey="participantCount"
                                            color="#28a745"
                                            canvasId="eventsParticipantsChart"
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </section>
            </main >
            <Modal show={isRejectModalOpen} onHide={() => !processingAction && setRejectModalOpen(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Rechazar Solicitud</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>
                        ¿Estás seguro de que quieres rechazar {itemToReject?.type === 'event' ? 'el evento' : 'la noticia'}:
                        <b> "{itemToReject?.name}"</b>?
                    </p>
                    <p className="text-muted">Esta acción eliminará la solicitud permanentemente.</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setRejectModalOpen(false)} disabled={processingAction !== null}>
                        Cancelar
                    </Button>
                    <Button variant="danger" onClick={handleConfirmReject} disabled={processingAction !== null}>
                        {processingAction ? (
                            <Spinner as="span" animation="border" size="sm" />
                        ) : (
                            "Confirmar Rechazo"
                        )}
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}