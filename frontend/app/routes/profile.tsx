import { Link, useNavigate } from "react-router";
import type { Route } from "./+types/profile";
import { getUser, updateProfile, updateUserImage } from "~/services/user-service";
import type UserDTO from "~/dtos/UserDTO";
import { useUserStore } from "~/stores/user-store";
import { Image } from "react-bootstrap";
import { useState, useRef, useActionState } from "react";

const API_URL = "https://localhost:8443/api/v1";

const userStore = useUserStore;

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    if (Number(params.id) === userStore.getState().user?.userId) {
        await userStore.getState().loadLoggedUser();
        const user = userStore.getState().user
        if (user) {
            return user;
        }
    }
    return await getUser(Number(params.id));
}

export default function Profile({ loaderData }: Route.ComponentProps) {

    const loggedUser = useUserStore(state => state.user);

    const [isEditing, setIsEditing] = useState(false);
    const [imagePreview, setImagePreview] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [tabSelected, setTabSelected] = useState(0);

    const formRef = useRef<HTMLFormElement>(null);

    const [wasValidated, setWasValidated] = useState(false)
    const [updateErrors, setUpdateErrors] = useState<string[]>([])

    const [user, setUser] = useState<UserDTO>(loaderData);

    const navigate = useNavigate()

    const [state, formAction, isPending] = useActionState(
        updateProfileAction,
        { userEmail: user.userEmail, password: "", confirmPassword: "", userName: user.userName, userSurname: user.userSurname, userNickname: user.userNickname, userInterests: user.userInterests }
    )

    if (!loggedUser) {
        navigate("/new/login")
        return;
    }

    if (!user || (loggedUser.userId !== user.userId && !loggedUser.userRoles.includes("ADMIN"))) {
        return (<>
            <div className="container" data-aos="fade-up">
                <div className="form-intro text-center mb-4 mt-2">
                    <h1 style={{ fontWeight: 700, color: '#212529', textTransform: 'uppercase' }}>Error</h1>
                    <p className="text-muted">No tienes permiso para acceder a esta página</p>
                    <hr style={{ width: '50px', margin: '10px auto 20px auto', borderTop: '3px solid #a71b12', opacity: 1 }} />
                </div>
            </div>
        </>);
    }

    const isOwner = loggedUser.userId === user.userId;

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

        const errors: string[] = []

        setWasValidated(true)

        if (form && form.checkValidity()) {
            if (password !== confirmPassword) {
                setUpdateErrors(["Las contraseñas no coinciden."]);
                return { userEmail, password, confirmPassword, userName, userSurname, userNickname }
            }

            try {
                const errors = await updateProfile(user.userId, userEmail, password, confirmPassword, userName, userSurname, userNickname, userInterests);

                if (errors.length > 0) {
                    setUpdateErrors(errors);
                } else {
                    setUser({
                        ...user,
                        userEmail,
                        userName,
                        userSurname,
                        userNickname,
                        userInterests
                    })
                    if (userImage && userImage.size > 0) {
                        await updateUserImage(user.userId, userImage);
                    }
                    toggleEdit();
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
                <div className="container" data-aos="fade-up">
                    <div className="form-intro text-center mb-4 mt-2">
                        <h1 style={{ fontWeight: 700, color: '#212529', textTransform: 'uppercase' }}>Perfil de Usuario</h1>
                        <p className="text-muted">Gestiona tu cuenta, tus eventos y tus juegos favoritos</p>
                        <hr style={{ width: '50px', margin: '10px auto 20px auto', borderTop: '3px solid #a71b12', opacity: 1 }} />
                    </div>
                    <div className="row gy-4 align-items-start">
                        <div className="col-lg-4">
                            <div className="card shadow-sm border-0 rounded-4">
                                <div className="card-body p-4 text-center">
                                    <form ref={formRef} id="update-form" encType="multipart/form-data" action={formAction} method="post" className="needs-validation" noValidate>
                                        <div className="position-relative d-inline-block mb-2">
                                            <div className="rounded-circle overflow-hidden shadow-sm" style={{ width: '150px', height: '150px', margin: '0 auto', border: '3px solid #f8f9fa' }}>
                                                <Image
                                                    src={imagePreview || `${API_URL}/users/${user.userId}/image`}
                                                    className="w-100 h-100"
                                                    alt={user.userNickname}
                                                    id="profile-img-preview"
                                                    style={{
                                                        objectFit: 'cover'
                                                    }}
                                                />
                                            </div>
                                            <div id="image-upload-wrapper" className={`position-absolute bottom-0 end-0 ${isEditing ? '' : 'd-none'}`}>
                                                <label htmlFor="image" className="btn btn-sm btn-danger rounded-circle shadow" style={{ cursor: 'pointer', width: '35px', height: '35px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                                    <i className="bi bi-camera-fill" />
                                                </label>
                                                <input ref={fileInputRef} className="d-none" type="file" name="userImage" id="image" accept="image/*" onChange={handleFileChange} />
                                            </div>
                                        </div>
                                        <h3 id="nickname-display" className="fw-bold mt-2 mb-0" style={{ color: '#a71b12' }}>
                                            {user.userNickname}</h3>
                                        <p className="text-muted small mb-4">{user.userName} {user.userSurname}</p>
                                        <div className="user-details text-start mt-2 border-top pt-3">
                                            {updateErrors.length > 0 && (
                                                <div className="row justify-content-center">
                                                    <div className="col-lg-8">
                                                        <div className="alert alert-danger alert-dismissible fade show mb-4 shadow-sm" role="alert" style={{ borderLeft: '5px solid #890f00' }}>
                                                            <strong><i className="bi bi-exclamation-triangle-fill me-2" /> Ups! Hay errores:</strong>
                                                            <ul className="mt-2 mb-0">
                                                                {updateErrors.map((error, index) => (
                                                                    <li key={index}>{error}</li>
                                                                ))}
                                                            </ul>
                                                            <button type="button" className="btn-close" onClick={() => setUpdateErrors([])} aria-label="Close" />
                                                        </div>
                                                    </div>
                                                </div>
                                            )}
                                            <div className={`mb-3 ${isEditing ? '' : 'd-none'}`} id="nickname-input-group">
                                                <label className="form-label text-muted small text-uppercase fw-bold mb-1 required-label">Apodo</label>
                                                <input type="text" className="form-control profile-input-edit" name="userNickname" defaultValue={user.userNickname} minLength={3} required />
                                                <div className="invalid-feedback">El apodo debe tener al menos 3 caracteres.</div>
                                            </div>
                                            <div className="mb-3">
                                                <label className="form-label text-muted small text-uppercase fw-bold mb-1 required-label">Correo
                                                    Electrónico</label>
                                                <input type="email" className={`form-control ${isEditing ? '' : 'form-control-plaintext'} profile-input`} name="userEmail" defaultValue={user.userEmail} readOnly={!isEditing} required />
                                                <div className="invalid-feedback">Introduce un correo electrónico válido.</div>
                                            </div>
                                            <div className={`row g-2 mb-3 ${isEditing ? '' : 'd-none'} password-edit-group`}>
                                                <div className="col-6">
                                                    <label className="form-label text-muted small text-uppercase fw-bold mb-1">Nueva
                                                        Contraseña</label>
                                                    <input type="password" className="form-control profile-input-edit" id="new-password" name="password" minLength={4} placeholder="Opcional" />
                                                    <div className="form-text mt-1" style={{ fontSize: '0.75rem', lineHeight: '1.1' }}>
                                                        Déjalo en blanco si no
                                                        quieres cambiarla.</div>
                                                    <div className="invalid-feedback">Mínimo 4 caracteres.</div>
                                                </div>
                                                <div className="col-6">
                                                    <label className="form-label text-muted small text-uppercase fw-bold mb-1">Confirmar</label>
                                                    <input type="password" className="form-control profile-input-edit" id="confirm-password-edit" name="confirmPassword" placeholder="Repite la contraseña" />
                                                    <div className="invalid-feedback">Las contraseñas no coinciden.</div>
                                                </div>
                                            </div>
                                            <div className="row g-2 mb-3">
                                                <div className="col-6">
                                                    <label className="form-label text-muted small text-uppercase fw-bold mb-1 required-label">Nombre</label>
                                                    <input type="text" className={`form-control ${isEditing ? '' : 'form-control-plaintext'} profile-input`} name="userName" defaultValue={user.userName} readOnly={!isEditing} required />
                                                    <div className="invalid-feedback">El nombre es obligatorio.</div>
                                                </div>
                                                <div className="col-6">
                                                    <label className="form-label text-muted small text-uppercase fw-bold mb-1 required-label">Apellidos</label>
                                                    <input type="text" className={`form-control ${isEditing ? '' : 'form-control-plaintext'} profile-input`} name="userSurname" defaultValue={user.userSurname} readOnly={!isEditing} required />
                                                    <div className="invalid-feedback">Los apellidos son obligatorios.</div>
                                                </div>
                                            </div>
                                            <div className="mb-4">
                                                <label className="form-label text-muted small text-uppercase fw-bold mb-1">Sobre mí
                                                    / Intereses</label>
                                                <textarea className={`form-control ${isEditing ? '' : 'form-control-plaintext'} profile-input`} name="userInterests" rows={3} readOnly={!isEditing} style={{ resize: 'none' }} defaultValue={user.userInterests} />
                                            </div>
                                        </div>
                                        {user.userId === loggedUser.userId && (
                                            <div className="d-grid gap-2 mt-2">
                                                <button type="button" id="btn-edit-profile" className={`btn btn-outline-dark rounded-pill ${isEditing ? 'd-none' : ''}`} onClick={toggleEdit}>
                                                    <i className="bi bi-pencil-square me-2" /> Editar Perfil
                                                </button>
                                                <div id="edit-actions" className={`d-grid gap-2 ${isEditing ? '' : 'd-none'}`}>
                                                    <button type="submit" className="btn btn-danger rounded-pill shadow-sm" disabled={isPending}>
                                                        {isPending ? (
                                                            <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true" />
                                                        ) : (
                                                            <i className="bi bi-save me-2" />
                                                        )}
                                                        {isPending ? 'Guardando...' : 'Guardar Cambios'}
                                                    </button>
                                                    <button type="button" id="btn-cancel-edit" className="btn btn-light rounded-pill border" onClick={toggleEdit} disabled={isPending}>
                                                        Cancelar
                                                    </button>
                                                </div>
                                                <hr className="my-3 text-muted" />
                                                <button form="delete-form" type="submit" className="btn btn-link text-danger text-decoration-none small p-0" onClick={() => confirm('¿Estás seguro de que deseas eliminar tu cuenta permanentemente?')}>
                                                    <i className="bi bi-exclamation-octagon me-1" /> Eliminar Cuenta
                                                </button>
                                            </div>
                                        )}
                                    </form>
                                </div>
                            </div>
                        </div>
                        <div className="col-lg-8">
                            <div className="card shadow-sm border-0 rounded-4">
                                <div className="card-body p-4">
                                    <ul className="nav nav-pills nav-fill mb-4 border-bottom pb-3 gap-2" id="profileTabs" role="tablist">
                                        <li className="nav-item" role="presentation">
                                            <button className={`nav-link ${tabSelected === 0 ? 'active' : ''} rounded-pill fw-bold`} id="created-events-tab" onClick={() => setTabSelected(0)} data-bs-toggle="tab" data-bs-target="#created-events" type="button" role="tab">
                                                <i className="bi bi-calendar-plus-fill me-2" />Mis Eventos
                                            </button>
                                        </li>
                                        <li className="nav-item" role="presentation">
                                            <button className={`nav-link ${tabSelected === 1 ? 'active' : ''} rounded-pill fw-bold`} id="my-news-tab" onClick={() => setTabSelected(1)} data-bs-toggle="tab" data-bs-target="#my-news" type="button" role="tab">
                                                <i className="bi bi-newspaper me-2" />Mis Noticias
                                            </button>
                                        </li>
                                        <li className="nav-item" role="presentation">
                                            <button className={`nav-link ${tabSelected === 2 ? 'active' : ''} rounded-pill fw-bold`} id="joined-events-tab" onClick={() => setTabSelected(2)} data-bs-toggle="tab" data-bs-target="#joined-events" type="button" role="tab">
                                                <i className="bi bi-ticket-perforated-fill me-2" />Mis Inscripciones
                                            </button>
                                        </li>
                                        <li className="nav-item" role="presentation">
                                            <button className={`nav-link ${tabSelected === 3 ? 'active' : ''} rounded-pill fw-bold`} id="fav-games-tab" onClick={() => setTabSelected(3)} data-bs-toggle="tab" data-bs-target="#fav-games" type="button" role="tab">
                                                <i className="bi bi-suit-heart-fill me-2" />Juegos Favoritos
                                            </button>
                                        </li>
                                    </ul>
                                    <div className="tab-content" id="profileTabsContent">
                                        <div className={`tab-pane fade ${tabSelected === 0 ? 'show active' : ''}`} id="created-events" role="tabpanel" aria-labelledby="created-events-tab">
                                            <div className="row gy-3">
                                                {user.userOwnEvents.map(event => (
                                                    <div className="col-md-6" key={event.eventId} data-aos="fade-up">
                                                        <Link to={`/new/events/${event.eventId}`} className="card text-decoration-none text-dark h-100 border hover-shadow transition-all">
                                                            <div className="row g-0 h-100">
                                                                <div className="col-4">
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
                                                                            <i className="bi bi-calendar-event" />
                                                                        </div>
                                                                    )}
                                                                </div>
                                                                <div className="col-8">
                                                                    <div className="card-body py-2 px-3 d-flex flex-column h-100 justify-content-center">
                                                                        <div className="d-flex gap-2 mb-1">
                                                                            {!event.validated && (
                                                                                <span className="badge bg-warning text-dark align-self-start" style={{ fontSize: '0.75rem' }}>
                                                                                    <i className="bi bi-hourglass-split me-1" />Pendiente
                                                                                    de Validación
                                                                                </span>
                                                                            )}
                                                                        </div>
                                                                        <h6 className="card-title fw-bold mb-1 text-truncate">
                                                                            {event.eventName}</h6>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </Link>
                                                    </div>
                                                ))}
                                                {user.userOwnEvents.length === 0 && (
                                                    isOwner ? (
                                                        <div className="col-12 text-center py-5">
                                                            <i className="bi bi-calendar-plus fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">Aún no has organizado ningún evento.</p>
                                                            <Link to="/event_form" className="btn btn-sm btn-outline-dark rounded-pill">Crear
                                                                mi primer
                                                                evento
                                                            </Link>
                                                        </div>
                                                    ) : (
                                                        <div className="col-12 text-center py-5">
                                                            <i className="bi bi-calendar-plus fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">El usuario no ha organizado ningún evento.</p>
                                                        </div>
                                                    )
                                                )}
                                            </div>
                                        </div>
                                        <div className={`tab-pane fade ${tabSelected === 2 ? 'show active' : ''}`} id="joined-events" role="tabpanel" aria-labelledby="joined-events-tab">
                                            <div className="row gy-3">
                                                {user.userRegisteredEvents.map(event => (
                                                    <div className="col-md-6" key={event.eventId} data-aos="fade-up">
                                                        <Link to={`/new/events/${event.eventId}`} className="card text-decoration-none text-dark h-100 border hover-shadow transition-all">
                                                            <div className="row g-0 h-100">
                                                                <div className="col-4">
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
                                                                            <i className="bi bi-calendar-event" />
                                                                        </div>
                                                                    )}
                                                                </div>
                                                                <div className="col-8">
                                                                    <div className="card-body py-2 px-3 d-flex flex-column h-100 justify-content-center">
                                                                        <span className="badge bg-warning text-dark align-self-start mb-1"><i className="bi bi-ticket-perforated me-1" />Inscrito</span>
                                                                        <h6 className="card-title fw-bold mb-1 text-truncate">
                                                                            {event.eventName}</h6>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </Link>
                                                    </div>
                                                ))}
                                                {user.userRegisteredEvents.length === 0 && (
                                                    isOwner ? (
                                                        <div className="col-12 text-center py-5">
                                                            <i className="bi bi-ticket-detailed fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">Aún no te has inscrito a ningún evento.</p>
                                                            <Link to="/new/events" className="btn btn-sm btn-outline-dark rounded-pill">Explorar
                                                                eventos</Link>
                                                        </div>
                                                    ) : (
                                                        <div className="col-12 text-center py-5">
                                                            <i className="bi bi-ticket-detailed fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">El usuario no se ha inscrito a ningún evento.</p>
                                                        </div>
                                                    )
                                                )}
                                            </div>
                                        </div>
                                        <div className={`tab-pane fade ${tabSelected === 3 ? 'show active' : ''}`} id="fav-games" role="tabpanel" aria-labelledby="fav-games-tab">
                                            <div className="row gy-3">
                                                {user.userFavGames.map(game => (
                                                    <div className="col-md-6" key={game.gameId} data-aos="fade-up">
                                                        <Link to={`/new/games/${game.gameId}`} className="card text-decoration-none text-dark h-100 border hover-shadow transition-all">
                                                            <div className="row g-0 h-100">
                                                                <div className="col-4">
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
                                                                            <i className="bi bi-dice-5" />
                                                                        </div>
                                                                    )}
                                                                </div>
                                                                <div className="col-8">
                                                                    <div className="card-body py-2 px-3 d-flex flex-column h-100 justify-content-center">
                                                                        <span className="badge bg-success bg-opacity-10 text-success align-self-start mb-1">{game.genre}</span>
                                                                        <h6 className="card-title fw-bold mb-1 text-truncate">
                                                                            {game.gameName}</h6>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </Link>
                                                    </div>
                                                ))}
                                                {user.userFavGames.length === 0 && (
                                                    isOwner ? (
                                                        <div className="col-12 text-center py-5">
                                                            <i className="bi bi-controller fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">No tienes ningún juego marcado como favorito.</p>
                                                            <Link to="/new/games" className="btn btn-sm btn-outline-dark rounded-pill">Ir a la
                                                                Ludoteca</Link>
                                                        </div>
                                                    ) : (
                                                        <div className="col-12 text-center py-5">
                                                            <i className="bi bi-controller fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">El usuario no tiene ningún juego marcado como
                                                                favorito.</p>
                                                        </div>
                                                    )
                                                )}
                                            </div>
                                        </div>
                                        <div className={`tab-pane fade ${tabSelected === 1 ? 'show active' : ''}`} id="my-news" role="tabpanel" aria-labelledby="my-news-tab">
                                            <div className="row gy-3">
                                                {user.userNews.map(item => (
                                                    <div className="col-md-6" key={item.newId} data-aos="fade-up">
                                                        <Link to={`/new/news/${item.newId}`} className="card text-decoration-none text-dark h-100 border hover-shadow transition-all">
                                                            <div className="row g-0 h-100">
                                                                <div className="col-4">
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
                                                                            <i className="bi bi-newspaper" />
                                                                        </div>
                                                                    )}
                                                                </div>
                                                                <div className="col-8">
                                                                    <div className="card-body py-2 px-3 d-flex flex-column h-100 justify-content-center">
                                                                        <div className="d-flex gap-2 mb-1">
                                                                            {!item.validated && (
                                                                                <span className="badge bg-warning text-dark align-self-start" style={{ fontSize: '0.75rem' }}>
                                                                                    <i className="bi bi-hourglass-split me-1" />Pendiente
                                                                                    de Validación
                                                                                </span>
                                                                            )}
                                                                        </div>
                                                                        <h6 className="card-title fw-bold mb-1 text-truncate">
                                                                            {item.newName}</h6>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </Link>
                                                    </div>
                                                ))}
                                                {user.userNews.length === 0 && (
                                                    isOwner ? (
                                                        <div className="col-12 text-center py-5">
                                                            <i className="bi bi-journal-text fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">Aún no has redactado ninguna noticia.</p>
                                                            <Link to="/new/new_form" className="btn btn-sm btn-outline-dark rounded-pill">Redactar noticia</Link>
                                                        </div>
                                                    ) : (
                                                        <div className="col-12 text-center py-5">
                                                            <i className="bi bi-journal-text fs-1 text-muted opacity-50" />
                                                            <p className="mt-3 text-muted">El usuario no ha redactado ninguna noticia.</p>
                                                        </div>
                                                    )
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </main >
    );
}