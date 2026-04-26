import { Link, useNavigate } from "react-router";
import type { Route } from "./+types/profile";
import { deleteProfile, getUser, updateProfile, updateUserImage } from "~/services/user-service";
import type UserDTO from "~/dtos/UserDTO";
import { useUserStore } from "~/stores/user-store";
import { Button, Col, Container, Form, FormControl, FormLabel, Image, Row, Spinner } from "react-bootstrap";
import {
    CalendarEvent,
    CalendarPlus,
    CalendarPlusFill,
    CameraFill,
    Controller,
    Dice5,
    ExclamationOctagon,
    ExclamationTriangleFill,
    HourglassSplit,
    JournalText,
    Newspaper,
    PencilSquare,
    Save,
    SuitHeartFill,
    TicketPerforated,
    TicketPerforatedFill
} from "react-bootstrap-icons";
import { useState, useRef, useActionState } from "react";

const API_URL = "https://localhost:8443/api/v1";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    return await getUser(Number(params.id));
}

export default function Profile({ loaderData }: Route.ComponentProps) {

    let { user, updateLastImageChange, logoutUser } = useUserStore();
    let lastImageChange = useUserStore((state) => state.lastImageChange);

    const [isEditing, setIsEditing] = useState(false);
    const [imagePreview, setImagePreview] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [tabSelected, setTabSelected] = useState(0);

    const formRef = useRef<HTMLFormElement>(null);

    const [wasValidated, setWasValidated] = useState(false)
    const [updateErrors, setUpdateErrors] = useState<string[]>([])

    const [profile, setUser] = useState<UserDTO>(loaderData);

    const navigate = useNavigate()

    const [state, formAction, isPending] = useActionState(
        updateProfileAction,
        { userEmail: profile.userEmail, password: "", confirmPassword: "", userName: profile.userName, userSurname: profile.userSurname, userNickname: profile.userNickname, userInterests: profile.userInterests }
    )

    if (!user) {
        return;
    }

    if (!profile || (user.userId !== profile.userId && !user.userRoles.includes("ADMIN"))) {
        return (<>
            <Container data-aos="fade-up">
                <div className="form-intro text-center mb-4 mt-2">
                    <h1 style={{ fontWeight: 700, color: '#212529', textTransform: 'uppercase' }}>Error</h1>
                    <p className="text-muted">No tienes permiso para acceder a esta página</p>
                    <hr style={{ width: '50px', margin: '10px auto 20px auto', borderTop: '3px solid #a71b12', opacity: 1 }} />
                </div>
            </Container>
        </>);
    }

    const isOwner = user.userId === profile.userId;

    const toggleEdit = () => {
        setIsEditing(!isEditing);
        if (isEditing) {
            setImagePreview(null);
        }
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setImagePreview(reader.result as string);
            };
            reader.readAsDataURL(file);
        }
    };

    async function updateProfileAction(prevState: {}, formData: FormData) {
        const userEmail = (formData.get("userEmail") as string) ?? "";
        const password = (formData.get("password") as string) ?? "";
        const confirmPassword = (formData.get("confirmPassword") as string) ?? "";
        const userName = (formData.get("userName") as string) ?? "";
        const userSurname = (formData.get("userSurname") as string) ?? "";
        const userNickname = (formData.get("userNickname") as string) ?? "";
        const userInterests = (formData.get("userInterests") as string) ?? "";
        const userImage = formData.get("userImage") as File || null;

        const form = formRef.current;

        setWasValidated(true)

        if (form && form.checkValidity()) {
            if (password !== confirmPassword) {
                setUpdateErrors(["Las contraseñas no coinciden."]);
                return { userEmail, password, confirmPassword, userName, userSurname, userNickname, userInterests }
            }

            try {
                const errors = await updateProfile(profile.userId, userEmail, password, confirmPassword, userName, userSurname, userNickname, userInterests);

                if (errors.length > 0) {
                    setUpdateErrors(errors);
                } else {
                    setUser({
                        ...profile,
                        userEmail,
                        userName,
                        userSurname,
                        userNickname,
                        userInterests
                    })
                    if (userImage && userImage.size > 0) {
                        await updateUserImage(profile.userId, userImage);
                    }
                    updateLastImageChange();
                    toggleEdit();
                    setUpdateErrors([]);
                }
            } catch (error) {
                setUpdateErrors(["Error al actualizar el perfil."]);
            }
        }

        return { userEmail, password, confirmPassword, userName, userSurname, userNickname, userInterests }
    }

    return (

        <main className="main">
            <section id="user-profile" className="section pt-4 mb-5">
                <Container data-aos="fade-up">
                    <div className="form-intro text-center mb-4 mt-2">
                        <h1 style={{ fontWeight: 700, color: '#212529', textTransform: 'uppercase' }}>Perfil de Usuario</h1>
                        <p className="text-muted">Gestiona tu cuenta, tus eventos y tus juegos favoritos</p>
                        <hr style={{ width: '50px', margin: '10px auto 20px auto', borderTop: '3px solid #a71b12', opacity: 1 }} />
                    </div>
                    <Row className="gy-4 align-items-start">
                        <Col lg={4}>
                            <div className="card shadow-sm border-0 rounded-4">
                                <div className="card-body p-4 text-center">
                                    <Form ref={formRef} id="update-form" encType="multipart/form-data" action={formAction} method="post" className={`needs-validation ${wasValidated && "was-validated"}`} noValidate>
                                        <div className="position-relative d-inline-block mb-2">
                                            <div className="rounded-circle overflow-hidden shadow-sm" style={{ width: '150px', height: '150px', margin: '0 auto', border: '3px solid #f8f9fa' }}>
                                                <Image
                                                    src={imagePreview || `${API_URL}/users/${profile.userId}/image?v=${lastImageChange}`}
                                                    className="w-100 h-100"
                                                    alt={profile.userNickname}
                                                    id="profile-img-preview"
                                                    style={{
                                                        objectFit: 'cover'
                                                    }}
                                                />
                                            </div>
                                            <div id="image-upload-wrapper" className={`position-absolute bottom-0 end-0 ${isEditing ? '' : 'd-none'}`}>
                                                <FormLabel htmlFor="image" className="btn btn-sm btn-danger rounded-circle shadow" style={{ cursor: 'pointer', width: '35px', height: '35px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                                    <CameraFill />
                                                </FormLabel>
                                                <input ref={fileInputRef} className="d-none" type="file" name="userImage" id="image" accept="image/*" onChange={handleFileChange} />
                                            </div>
                                        </div>
                                        <h3 id="nickname-display" className="fw-bold mt-2 mb-0" style={{ color: '#a71b12' }}>
                                            {profile.userNickname}</h3>
                                        <p className="text-muted small mb-4">{profile.userName} {profile.userSurname}</p>
                                        <div className="user-details text-start mt-2 border-top pt-3">
                                            {updateErrors.length > 0 && (
                                                <Row className="justify-content-center">
                                                    <Col lg={12}>
                                                        <div className="alert alert-danger alert-dismissible fade show mb-4 shadow-sm" role="alert" style={{ borderLeft: '5px solid #890f00' }}>
                                                            <strong><ExclamationTriangleFill className="me-2" /> Ups! Hay errores:</strong>
                                                            <ul className="mt-2 mb-0">
                                                                {updateErrors.map((error, index) => (
                                                                    <li key={index}>{error}</li>
                                                                ))}
                                                            </ul>
                                                            <Button type="button" className="btn-close" onClick={() => setUpdateErrors([])} aria-label="Close" />
                                                        </div>
                                                    </Col>
                                                </Row>
                                            )}
                                            <div className={`mb-3 ${isEditing ? '' : 'd-none'}`} id="nickname-input-group">
                                                <FormLabel className="text-muted small text-uppercase fw-bold mb-1 required-label">Apodo</FormLabel>
                                                <FormControl type="text" className="profile-input-edit" name="userNickname" defaultValue={profile.userNickname} minLength={3} required />
                                                <div className="invalid-feedback">El apodo debe tener al menos 3 caracteres.</div>
                                            </div>
                                            <div className="mb-3">
                                                <FormLabel className="text-muted small text-uppercase fw-bold mb-1 required-label">Correo
                                                    Electrónico</FormLabel>
                                                <FormControl type="email" className={`${isEditing ? '' : 'form-control-plaintext'} profile-input`} name="userEmail" defaultValue={profile.userEmail} readOnly={!isEditing} required />
                                                <div className="invalid-feedback">Introduce un correo electrónico válido.</div>
                                            </div>
                                            <Row className={`g-2 mb-3 ${isEditing ? '' : 'd-none'} password-edit-group`}>
                                                <Col xs={6}>
                                                    <FormLabel className="text-muted small text-uppercase fw-bold mb-1">Nueva
                                                        Contraseña</FormLabel>
                                                    <FormControl type="password" id="new-password" name="password" minLength={4} placeholder="Opcional" />
                                                    <div className="form-text mt-1" style={{ fontSize: '0.75rem', lineHeight: '1.1' }}>
                                                        Déjalo en blanco si no
                                                        quieres cambiarla.</div>
                                                    <div className="invalid-feedback">Mínimo 4 caracteres.</div>
                                                </Col>
                                                <Col xs={6}>
                                                    <FormLabel className="text-muted small text-uppercase fw-bold mb-1">Confirmar</FormLabel>
                                                    <FormControl type="password" id="confirm-password-edit" name="confirmPassword" placeholder="Repite la contraseña" />
                                                    <div className="invalid-feedback">Las contraseñas no coinciden.</div>
                                                </Col>
                                            </Row>
                                            <Row className="g-2 mb-3">
                                                <Col xs={6}>
                                                    <FormLabel className="text-muted small text-uppercase fw-bold mb-1 required-label">Nombre</FormLabel>
                                                    <FormControl type="text" className={`${isEditing ? '' : 'form-control-plaintext'} profile-input`} name="userName" defaultValue={profile.userName} readOnly={!isEditing} required />
                                                    <div className="invalid-feedback">El nombre es obligatorio.</div>
                                                </Col>
                                                <Col xs={6}>
                                                    <FormLabel className="text-muted small text-uppercase fw-bold mb-1 required-label">Apellidos</FormLabel>
                                                    <FormControl type="text" className={`${isEditing ? '' : 'form-control-plaintext'} profile-input`} name="userSurname" defaultValue={profile.userSurname} readOnly={!isEditing} required />
                                                    <div className="invalid-feedback">Los apellidos son obligatorios.</div>
                                                </Col>
                                            </Row>
                                            <div className="mb-4">
                                                <FormLabel className="text-muted small text-uppercase fw-bold mb-1">Sobre mí
                                                    / Intereses</FormLabel>
                                                <FormControl as="textarea" className={`${isEditing ? '' : 'form-control-plaintext'} profile-input`} name="userInterests" rows={3} readOnly={!isEditing} style={{ resize: 'none' }} defaultValue={profile.userInterests} />
                                            </div>
                                        </div>
                                        {profile.userId === user.userId && (
                                            <div className="d-grid gap-2 mt-2">
                                                <Button variant="outline-dark" type="button" id="btn-edit-profile" className={`rounded-pill ${isEditing ? 'd-none' : ''}`} onClick={toggleEdit}>
                                                    <PencilSquare className="me-2" /> Editar Perfil
                                                </Button>
                                                <div id="edit-actions" className={`d-grid gap-2 ${isEditing ? '' : 'd-none'}`}>
                                                    <Button type="submit" className="btn-danger rounded-pill shadow-sm" disabled={isPending}>
                                                        {isPending ? (
                                                            <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" className="me-2" />
                                                        ) : (
                                                            <Save className="me-2" />
                                                        )}
                                                        {isPending ? 'Guardando...' : 'Guardar Cambios'}
                                                    </Button>
                                                    <Button type="button" id="btn-cancel-edit" className="btn-light rounded-pill border" onClick={toggleEdit} disabled={isPending}>
                                                        Cancelar
                                                    </Button>
                                                </div>
                                                <hr className="my-3 text-muted" />
                                                <Button variant="link" type="button" className="btn text-danger text-decoration-none small p-0" onClick={() => {
                                                    if (confirm('¿Estás seguro de que deseas eliminar tu cuenta permanentemente?')) {
                                                        deleteProfile(profile.userId);
                                                        logoutUser();
                                                        navigate("/new");
                                                    }
                                                }}>
                                                    <ExclamationOctagon className="me-1" /> Eliminar Cuenta
                                                </Button>
                                            </div>
                                        )}
                                    </Form>
                                </div>
                            </div>
                        </Col>
                        <Col lg={8}>
                            <div className="card shadow-sm border-0 rounded-4">
                                <div className="card-body p-4">
                                    <ul className="nav nav-pills nav-fill mb-4 border-bottom pb-3 gap-2" id="profileTabs" role="tablist">
                                        <li className="nav-item" role="presentation">
                                            <Button variant="link" className={`nav-link ${tabSelected === 0 ? 'active' : ''} rounded-pill fw-bold`} id="created-events-tab" onClick={() => setTabSelected(0)} type="button" role="tab">
                                                <CalendarPlusFill className="me-2" />Mis Eventos
                                            </Button>
                                        </li>
                                        <li className="nav-item" role="presentation">
                                            <Button variant="link" className={`nav-link ${tabSelected === 1 ? 'active' : ''} rounded-pill fw-bold`} id="my-news-tab" onClick={() => setTabSelected(1)} type="button" role="tab">
                                                <Newspaper className="me-2" />Mis Noticias
                                            </Button>
                                        </li>
                                        <li className="nav-item" role="presentation">
                                            <Button variant="link" className={`nav-link ${tabSelected === 2 ? 'active' : ''} rounded-pill fw-bold`} id="joined-events-tab" onClick={() => setTabSelected(2)} type="button" role="tab">
                                                <TicketPerforatedFill className="me-2" />Mis Inscripciones
                                            </Button>
                                        </li>
                                        <li className="nav-item" role="presentation">
                                            <Button variant="link" className={`nav-link ${tabSelected === 3 ? 'active' : ''} rounded-pill fw-bold`} id="fav-games-tab" onClick={() => setTabSelected(3)} type="button" role="tab">
                                                <SuitHeartFill className="me-2" />Juegos Favoritos
                                            </Button>
                                        </li>
                                    </ul>
                                    <div className="tab-content" id="profileTabsContent">
                                        <div className={`tab-pane fade ${tabSelected === 0 ? 'show active' : ''}`} id="created-events" role="tabpanel" aria-labelledby="created-events-tab">
                                            <Row className="gy-3">
                                                {profile.userOwnEvents.map(event => (
                                                    <Col md={6} key={event.eventId} data-aos="fade-up">
                                                        <Link to={`/new/events/${event.eventId}`} className="card text-decoration-none text-dark h-100 border hover-shadow transition-all">
                                                            <Row className="g-0 h-100">
                                                                <Col xs={4}>
                                                                    {event.eventImage ? (
                                                                        <Image
                                                                            src={`${API_URL}/images/${event.eventImage.id}/media`}
                                                                            className="img-fluid rounded-start h-100 w-100"
                                                                            alt={event.eventName}
                                                                            style={{
                                                                                objectFit: 'cover'
                                                                            }}
                                                                        />
                                                                    ) : (
                                                                        <div className="bg-secondary h-100 w-100 d-flex align-items-center justify-content-center text-white rounded-start">
                                                                            <CalendarEvent />
                                                                        </div>
                                                                    )}
                                                                </Col>
                                                                <Col xs={8}>
                                                                    <div className="card-body py-2 px-3 d-flex flex-column h-100 justify-content-center">
                                                                        <div className="d-flex gap-2 mb-1">
                                                                            {!event.validated && (
                                                                                <span className="badge bg-warning text-dark align-self-start" style={{ fontSize: '0.75rem' }}>
                                                                                    <HourglassSplit className="me-1" />Pendiente
                                                                                    de Validación
                                                                                </span>
                                                                            )}
                                                                        </div>
                                                                        <h6 className="card-title fw-bold mb-1 text-truncate">
                                                                            {event.eventName}</h6>
                                                                    </div>
                                                                </Col>
                                                            </Row>
                                                        </Link>
                                                    </Col>
                                                ))}
                                                {profile.userOwnEvents.length === 0 && (
                                                    isOwner ? (
                                                        <Col xs={12} className="text-center py-5">
                                                            <CalendarPlus className="fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">Aún no has organizado ningún evento.</p>
                                                            <Link to="/event_form" className="btn btn-sm btn-outline-dark rounded-pill">Crear
                                                                mi primer
                                                                evento
                                                            </Link>
                                                        </Col>
                                                    ) : (
                                                        <Col xs={12} className="text-center py-5">
                                                            <CalendarPlus className="fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">El usuario no ha organizado ningún evento.</p>
                                                        </Col>
                                                    )
                                                )}
                                            </Row>
                                        </div>
                                        <div className={`tab-pane fade ${tabSelected === 2 ? 'show active' : ''}`} id="joined-events" role="tabpanel" aria-labelledby="joined-events-tab">
                                            <Row className="gy-3">
                                                {profile.userRegisteredEvents.map(event => (
                                                    <Col md={6} key={event.eventId} data-aos="fade-up">
                                                        <Link to={`/new/events/${event.eventId}`} className="card text-decoration-none text-dark h-100 border hover-shadow transition-all">
                                                            <Row className="g-0 h-100">
                                                                <Col xs={4}>
                                                                    {event.eventImage ? (
                                                                        <Image
                                                                            src={`${API_URL}/images/${event.eventImage.id}/media`}
                                                                            className="img-fluid rounded-start h-100 w-100"
                                                                            alt={event.eventName}
                                                                            style={{
                                                                                objectFit: 'cover'
                                                                            }}
                                                                        />
                                                                    ) : (
                                                                        <div className="bg-secondary h-100 w-100 d-flex align-items-center justify-content-center text-white rounded-start">
                                                                            <CalendarEvent />
                                                                        </div>
                                                                    )}
                                                                </Col>
                                                                <Col xs={8}>
                                                                    <div className="card-body py-2 px-3 d-flex flex-column h-100 justify-content-center">
                                                                        <span className="badge bg-warning text-dark align-self-start mb-1"><TicketPerforated className="me-1" />Inscrito</span>
                                                                        <h6 className="card-title fw-bold mb-1 text-truncate">
                                                                            {event.eventName}</h6>
                                                                    </div>
                                                                </Col>
                                                            </Row>
                                                        </Link>
                                                    </Col>
                                                ))}
                                                {profile.userRegisteredEvents.length === 0 && (
                                                    isOwner ? (
                                                        <Col xs={12} className="text-center py-5">
                                                            <TicketPerforated className="fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">Aún no te has inscrito a ningún evento.</p>
                                                            <Link to="/new/events" className="btn btn-sm btn-outline-dark rounded-pill">Explorar
                                                                eventos</Link>
                                                        </Col>
                                                    ) : (
                                                        <Col xs={12} className="text-center py-5">
                                                            <TicketPerforated className="fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">El usuario no se ha inscrito a ningún evento.</p>
                                                        </Col>
                                                    )
                                                )}
                                            </Row>
                                        </div>
                                        <div className={`tab-pane fade ${tabSelected === 3 ? 'show active' : ''}`} id="fav-games" role="tabpanel" aria-labelledby="fav-games-tab">
                                            <Row className="gy-3">
                                                {profile.userFavGames.map(game => (
                                                    <Col md={6} key={game.gameId} data-aos="fade-up">
                                                        <Link to={`/new/games/${game.gameId}`} className="card text-decoration-none text-dark h-100 border hover-shadow transition-all">
                                                            <Row className="g-0 h-100">
                                                                <Col xs={4}>
                                                                    {game.gameImage ? (
                                                                        <Image
                                                                            src={`${API_URL}/images/${game.gameImage.id}/media`}
                                                                            className="img-fluid rounded-start h-100 w-100"
                                                                            alt={game.gameName}
                                                                            style={{
                                                                                objectFit: 'cover'
                                                                            }}
                                                                        />
                                                                    ) : (
                                                                        <div className="bg-secondary h-100 w-100 d-flex align-items-center justify-content-center text-white rounded-start">
                                                                            <Dice5 />
                                                                        </div>
                                                                    )}
                                                                </Col>
                                                                <Col xs={8}>
                                                                    <div className="card-body py-2 px-3 d-flex flex-column h-100 justify-content-center">
                                                                        <span className="badge bg-success bg-opacity-10 text-success align-self-start mb-1">{game.genre}</span>
                                                                        <h6 className="card-title fw-bold mb-1 text-truncate">
                                                                            {game.gameName}</h6>
                                                                    </div>
                                                                </Col>
                                                            </Row>
                                                        </Link>
                                                    </Col>
                                                ))}
                                                {profile.userFavGames.length === 0 && (
                                                    isOwner ? (
                                                        <Col xs={12} className="text-center py-5">
                                                            <Controller className="fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">No tienes ningún juego marcado como favorito.</p>
                                                            <Link to="/new/games" className="btn btn-sm btn-outline-dark rounded-pill">Ir a la
                                                                Ludoteca</Link>
                                                        </Col>
                                                    ) : (
                                                        <Col xs={12} className="text-center py-5">
                                                            <Controller className="fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">El usuario no tiene ningún juego marcado como
                                                                favorito.</p>
                                                        </Col>
                                                    )
                                                )}
                                            </Row>
                                        </div>
                                        <div className={`tab-pane fade ${tabSelected === 1 ? 'show active' : ''}`} id="my-news" role="tabpanel" aria-labelledby="my-news-tab">
                                            <Row className="gy-3">
                                                {profile.userNews.map(item => (
                                                    <Col md={6} key={item.newId} data-aos="fade-up">
                                                        <Link to={`/new/news/${item.newId}`} className="card text-decoration-none text-dark h-100 border hover-shadow transition-all">
                                                            <Row className="g-0 h-100">
                                                                <Col xs={4}>
                                                                    {item.newImage ? (
                                                                        <Image
                                                                            src={`${API_URL}/images/${item.newImage.id}/media`}
                                                                            className="img-fluid rounded-start h-100 w-100"
                                                                            alt={item.newName}
                                                                            style={{
                                                                                objectFit: 'cover'
                                                                            }}
                                                                        />
                                                                    ) : (
                                                                        <div className="bg-secondary h-100 w-100 d-flex align-items-center justify-content-center text-white rounded-start">
                                                                            <Newspaper />
                                                                        </div>
                                                                    )}
                                                                </Col>
                                                                <Col xs={8}>
                                                                    <div className="card-body py-2 px-3 d-flex flex-column h-100 justify-content-center">
                                                                        <div className="d-flex gap-2 mb-1">
                                                                            {!item.validated && (
                                                                                <span className="badge bg-warning text-dark align-self-start" style={{ fontSize: '0.75rem' }}>
                                                                                    <HourglassSplit className="me-1" />Pendiente
                                                                                    de Validación
                                                                                </span>
                                                                            )}
                                                                        </div>
                                                                        <h6 className="card-title fw-bold mb-1 text-truncate">
                                                                            {item.newName}</h6>
                                                                    </div>
                                                                </Col>
                                                            </Row>
                                                        </Link>
                                                    </Col>
                                                ))}
                                                {profile.userNews.length === 0 && (
                                                    isOwner ? (
                                                        <Col xs={12} className="text-center py-5">
                                                            <JournalText className="fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">Aún no has redactado ninguna noticia.</p>
                                                            <Link to="/new/new_form" className="btn btn-sm btn-outline-dark rounded-pill">Redactar noticia</Link>
                                                        </Col>
                                                    ) : (
                                                        <Col xs={12} className="text-center py-5">
                                                            <JournalText className="fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">El usuario no ha redactado ninguna noticia.</p>
                                                        </Col>
                                                    )
                                                )}
                                            </Row>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </Col>
                    </Row>
                </Container>
            </section>
        </main >
    );
}