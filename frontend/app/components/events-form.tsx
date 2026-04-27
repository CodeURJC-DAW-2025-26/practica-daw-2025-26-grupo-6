import type { FormEvent } from "react";
import { useState } from "react";
import { Button, Form, Spinner } from "react-bootstrap";
import type EventDTO from "~/dtos/EventDTO";
import {
    ExclamationTriangleFill,
    CalendarEvent,
    People,
    TicketPerforated,
    Link45deg,
    ArrowLeft,
    Check2Circle
} from 'react-bootstrap-icons';

interface EventsFormProps {
    event?: Partial<EventDTO>;
    actionState: [
        { success: boolean; error: string | null } | null,
        (formData: FormData) => void,
        boolean,
    ];
    onCancel: () => void;
}

export default function EventsForm({
    event,
    actionState: [state, formAction, isPending],
    onCancel,
}: EventsFormProps) {
    const isEditing = event?.eventId;
    const [requiresRegistration, setRequiresRegistration] = useState(event?.requiresRegistration ?? false);

    const today = new Date().toISOString().split("T")[0];

    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        const form = event.currentTarget;

        if (!form.checkValidity()) {
            event.stopPropagation();
            form.classList.add("was-validated");
        } else {
            form.classList.add("was-validated");
            formAction(new FormData(form));
        }
    }

    return (
        <main className="main">
            <section id="quote" className="quote section pt-0">
                <div className="container" data-aos="fade-up" data-aos-delay="100">
                    <div className="row justify-content-center">
                        <div className="col-lg-10" data-aos="fade-left" data-aos-delay="300">
                            <div className="quote-form">
                                <div className="form-intro">
                                    <h3>
                                        {isEditing ? "Modificar" : "Añadir"} un evento
                                    </h3>
                                    <p>
                                        {isEditing
                                            ? "Modifica los datos del evento seleccionado."
                                            : "Rellena los campos para publicar un nuevo evento."}
                                    </p>
                                </div>

                                {state?.error && (
                                    <div
                                        className="alert alert-danger alert-dismissible fade show mb-4"
                                        role="alert"
                                        style={{ borderLeft: "5px solid #890f00" }}
                                    >
                                        <strong>
                                            <ExclamationTriangleFill className="me-2" /> Ups! Hay errores:
                                        </strong>
                                        <ul className="mt-2 mb-0">
                                            <li>{state.error}</li>
                                        </ul>
                                        <Button
                                            type="button"
                                            className="btn-close"
                                            aria-label="Close"
                                            onClick={onCancel}
                                        ></Button>
                                    </div>
                                )}

                                <Form
                                    className="custom-login-form needs-validation"
                                    action={formAction}
                                    onSubmit={handleSubmit}
                                    noValidate
                                >
                                    <div className="row g-4">
                                        {isEditing && (<input type="hidden" name="eventId" value={event?.eventId ?? ""} />)}

                                        <div className="col-md-8">
                                            <div className="form-field">
                                                <Form.Label htmlFor="eventName" className="required-label fw-bold mb-2">Nombre del evento</Form.Label>
                                                <Form.Control type="text" className="form-control shadow-sm" name="eventName" id="eventName" defaultValue={event?.eventName ?? ""}
                                                    placeholder="Ej: Torneo de Catán" minLength={5} maxLength={100} required />
                                                <div className="invalid-feedback">El nombre debe tener entre 5 y 100 caracteres.</div>
                                            </div>
                                        </div>
                                        <div className="col-md-4">
                                            <div className="form-field">
                                                <Form.Label htmlFor="eventTag" className="fw-bold mb-2">Categoría (Tag)</Form.Label>
                                                <Form.Control type="text" className="form-control shadow-sm" name="eventTag" id="eventTag" defaultValue={event?.eventTag ?? ""}
                                                    placeholder="Ej: Rol, Cartas..." />
                                            </div>
                                        </div>
                                        <div className="col-12 mt-4">
                                            <div className="registration-box p-4 border rounded-4 bg-light shadow-sm">
                                                <label className="fw-bold mb-3 d-block text-dark"><CalendarEvent className="me-2" />Fecha y
                                                    Aforo</label>

                                                <div className="row g-4 align-items-start">

                                                    <div className="col-md-6">
                                                        <Form.Label htmlFor="eventDate" className="required-label fw-bold mb-2 small text-muted">Día del
                                                            evento</Form.Label>
                                                        <Form.Control type="date" className="form-control shadow-sm" name="eventDate" id="eventDate" defaultValue={event?.eventDate ?? ""} min={today} disabled={isPending}
                                                            required />
                                                        <div className="invalid-feedback">Selecciona una fecha válida.</div>
                                                    </div>

                                                    <div className="col-md-6">
                                                        <Form.Label htmlFor="maxParticipants" className="fw-bold mb-2 small text-muted">Aforo Máximo <People className="ms-1" />
                                                        </Form.Label>
                                                        <div className="input-group shadow-sm">
                                                            <Form.Control type="number" className="form-control" name="maxParticipants" id="maxParticipants"
                                                                defaultValue={event?.maxParticipants ?? ""} placeholder="Ilimitado" min="1" />
                                                            <div className="invalid-feedback">
                                                                Número de participantes incorrecto. Déjalo vacío para aforo ilimitado o introduce un número mayor o igual a
                                                                1.
                                                            </div>
                                                        </div>
                                                    </div>

                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-12 mt-4">
                                            <div className="form-field p-3 border rounded-3 bg-light h-100 d-flex flex-column justify-content-center">
                                                <Form.Label htmlFor="imageField" className="required-label">
                                                    Imagen de portada:
                                                </Form.Label>
                                                <Form.Control
                                                    type="file"
                                                    name="imageField"
                                                    id="imageField"
                                                    className="form-control"
                                                    accept=".jpg,.jpeg,.png,.webp"
                                                    required={!isEditing}
                                                    disabled={isPending}
                                                />
                                                <div className="invalid-feedback">
                                                    Debes subir una imagen válida (JPG, PNG o WebP).
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-12">
                                            <div className="form-field">
                                                <Form.Label htmlFor="eventDescription" className="required-label fw-bold mb-2">Descripción del evento</Form.Label>
                                                <textarea name="eventDescription" id="eventDescription" rows={5} className="form-control shadow-sm"
                                                    placeholder="Cuéntanos de qué trata el evento..." minLength={50} maxLength={3000} defaultValue={event?.eventDescription ?? ""}
                                                    required></textarea>
                                                <div className="invalid-feedback">La descripción debe tener entre 50 y 3000 caracteres.</div>
                                            </div>
                                        </div>
                                        <div className="col-12">
                                            <div className="registration-box p-4 border rounded-4 bg-white shadow-sm"
                                                style={{ borderLeft: "5px solid #a71b12" }}>
                                                <Form.Label className="fw-bold mb-3 d-block"><TicketPerforated className="me-2" />Gestión de
                                                    Inscripciones</Form.Label>
                                                <div className="row align-items-center">
                                                    <div className="col-md-5">
                                                        <p className="small text-muted mb-3 mb-md-0">¿Es necesaria una inscripción externa?</p>
                                                    </div>
                                                    <div className="col-md-7">
                                                        <div className="btn-group w-100" role="group">
                                                            <Form.Control
                                                                type="radio"
                                                                className="btn-check radio-registro"
                                                                name="requiresRegistration"
                                                                id="req-si"
                                                                value="true"
                                                                checked={requiresRegistration}
                                                                onChange={() => setRequiresRegistration(true)}
                                                            />
                                                            <Form.Label className="btn btn-outline-danger py-2" htmlFor="req-si">Sí, requiere registro externo</Form.Label>
                                                            <Form.Control
                                                                type="radio"
                                                                className="btn-check radio-registro"
                                                                name="requiresRegistration"
                                                                id="req-no"
                                                                value="false"
                                                                checked={!requiresRegistration}
                                                                onChange={() => setRequiresRegistration(false)}
                                                            />
                                                            <Form.Label className="btn btn-outline-secondary py-2" htmlFor="req-no">No, inscripción interna</Form.Label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div id="link-container" className={`mt-4 ${requiresRegistration ? "" : "d-none"}`}>
                                                    <Form.Label htmlFor="link" className="required-label fw-bold mb-2">Enlace de registro oficial</Form.Label>
                                                    <div className="input-group has-validation">
                                                        <span className="input-group-text bg-white"><Link45deg /></span>
                                                        <Form.Control
                                                            type="url"
                                                            name="link"
                                                            id="link"
                                                            defaultValue={event?.link ?? ""}
                                                            className="form-control"
                                                            placeholder="https://forms.gle/..."
                                                            required={requiresRegistration}
                                                        />
                                                        <div className="invalid-feedback">Por favor, introduce una URL válida (ej: https://...).</div>
                                                    </div>
                                                    <small className="text-muted mt-2 d-block">Introduce la URL del formulario o plataforma
                                                        externa.</small>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="d-flex flex-column flex-md-row justify-content-center gap-3 mt-5">
                                            <Button
                                                type="button"
                                                className="btn btn-lg btn-light rounded-pill px-5 border shadow-sm text-decoration-none d-flex align-items-center justify-content-center"
                                                onClick={onCancel}
                                                disabled={isPending}
                                            >
                                                <ArrowLeft className="me-2" />Atrás
                                            </Button>


                                            <Button
                                                type="submit"
                                                className="btn btn-lg btn-danger rounded-pill px-5 shadow d-flex align-items-center justify-content-center"
                                                style={{
                                                    backgroundColor: "#a71b12",
                                                    border: "none",
                                                }}
                                                disabled={isPending}
                                            >
                                                {isPending ? (
                                                    <>
                                                        <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" className="me-2" />
                                                        Guardando...
                                                    </>
                                                ) : (
                                                    <>
                                                        <span className="me-2">
                                                            {isEditing ? "Actualizar Evento" : "Guardar Evento"}
                                                        </span>
                                                        <Check2Circle size={20} />
                                                    </>
                                                )}
                                            </Button>
                                        </div>
                                    </div>
                                </Form>
                            </div>
                        </div>
                    </div>
                </div>
            </section >
        </main >
    );
}
