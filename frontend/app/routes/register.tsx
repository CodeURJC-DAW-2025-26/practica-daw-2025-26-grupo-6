import { useActionState, useRef, useState, startTransition } from "react";
import type { FormEvent } from "react";
import { Form, Link, useNavigate } from "react-router";
import { useUserStore } from "~/stores/user-store";
import { register } from "~/services/user-service";
import { ArrowLeft, BoxArrowInRight, ExclamationTriangleFill, PersonPlusFill } from "react-bootstrap-icons";
import { Button, FormControl, FormLabel, ListGroup, ListGroupItem, Spinner } from "react-bootstrap";

export default function Register() {

    const userStore = useUserStore()
    const navigate = useNavigate()

    const [wasValidated, setWasValidated] = useState(false)
    const formRef = useRef<HTMLFormElement>(null);
    const [registerErrors, setRegisterErrors] = useState<string[]>([])


    async function registerAction(prevState: {}, formData: FormData) {
        const userEmail = (formData.get("userEmail") as string) ?? "";
        const password = (formData.get("password") as string) ?? "";
        const confirmPassword = (formData.get("confirmPassword") as string) ?? "";
        const userName = (formData.get("userName") as string) ?? "";
        const userSurname = (formData.get("userSurname") as string) ?? "";
        const userNickname = (formData.get("userNickname") as string) ?? "";

        const form = formRef.current;
        const errors: string[] = []

        setWasValidated(true)

        if (form && form.checkValidity()) {
            if (password !== confirmPassword) {
                setRegisterErrors(["Las contraseñas no coinciden."]);
                return { userEmail, password, confirmPassword, userName, userSurname, userNickname }
            }

            try {
                const errors = await register(userEmail, password, confirmPassword, userName, userSurname, userNickname);

                if (errors.length > 0) {
                    setRegisterErrors(errors);
                } else {
                    try {
                        await userStore.loginUser(userEmail, password)
                    } catch (error) {
                        navigate("/new/login")
                    }
                    navigate("/new/")
                }

            } catch (error) {
                setRegisterErrors(["Error al registrar el usuario."]);
            }
        }

        return { userEmail, password, confirmPassword, userName, userSurname, userNickname }
    }

    const [state, formAction, isPending] = useActionState(
        registerAction,
        { userEmail: "", password: "", confirmPassword: "", userName: "", userSurname: "", userNickname: "" }
    )


    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        const form = event.currentTarget;

        if (!form.checkValidity()) {
            event.stopPropagation();
            setWasValidated(true);
        } else {
            setWasValidated(true);
            startTransition(() => {
                formAction(new FormData(form));
            });
        }
    }

    return (
        <main className="main">
            <section id="quote" className="quote section pt-0">
                <div className="container" data-aos="fade-up" data-aos-delay={100}>
                    <div className="row justify-content-center">
                        <div className="col-lg-8" data-aos="fade-left" data-aos-delay={300}>
                            <div className="quote-form">
                                <div className="form-intro text-center mb-4">
                                    <h3>Únete a la aventura</h3>
                                    <p>Rellena los datos para crear tu cuenta en La Caverna del Dragón.</p>
                                </div>
                                {registerErrors.length > 0 && (
                                    <div className="alert alert-danger alert-dismissible fade show mb-4" role="alert" style={{ borderLeft: '5px solid #890f00' }}>
                                        <strong><ExclamationTriangleFill /> Ups! Revisa los datos:</strong>
                                        <ListGroup className="mt-2 mb-0">
                                            {registerErrors.map((error, index) => (
                                                <ListGroupItem key={index}>{error}</ListGroupItem>
                                            ))}
                                        </ListGroup>
                                        <Button type="button" className="btn-close" onClick={() => setRegisterErrors([])} aria-label="Close" />
                                    </div>
                                )}
                                <form id="registerForm" ref={formRef} onSubmit={handleSubmit} encType="multipart/form-data" className={`custom-login-form needs-validation ${wasValidated && "was-validated"}`} noValidate>
                                    <div className="form">
                                        <div className="form-field">
                                            <FormLabel htmlFor="email" className="required-label">Correo electrónico:</FormLabel>
                                            <FormControl type="email" name="userEmail" id="email" disabled={isPending} defaultValue={state.userEmail} className="form-control" required />
                                            <div className="invalid-feedback">
                                                Por favor, introduce un correo electrónico válido.
                                            </div>
                                        </div>
                                        <br />
                                        <div className="row">
                                            <div className="col-md-6 form-field mb-3 mb-md-0">
                                                <FormLabel htmlFor="password" className="required-label">Contraseña:</FormLabel>
                                                <FormControl type="password" name="password" id="password" disabled={isPending} defaultValue={state.password} className="form-control" minLength={4} maxLength={20} required />
                                                <div className="invalid-feedback">La contraseña debe tener entre 4 y 20 caracteres.
                                                </div>
                                            </div>
                                            <div className="col-md-6 form-field">
                                                <FormLabel htmlFor="confirm-password" className="required-label">Confirmar
                                                    contraseña:</FormLabel>
                                                <FormControl type="password" name="confirmPassword" id="confirm-password" disabled={isPending} defaultValue={state.confirmPassword} className="form-control" minLength={4} maxLength={20} required />
                                                <div className="invalid-feedback">
                                                    Las contraseñas deben coincidir.
                                                </div>
                                            </div>
                                        </div>
                                        <br />
                                        <div className="row">
                                            <div className="col-md-6 form-field mb-3 mb-md-0">
                                                <FormLabel htmlFor="name" className="required-label">Nombre:</FormLabel>
                                                <FormControl type="text" name="userName" id="name" disabled={isPending} defaultValue={state.userName} className="form-control" required />
                                                <div className="invalid-feedback">El nombre es obligatorio.</div>
                                            </div>
                                            <div className="col-md-6 form-field">
                                                <FormLabel htmlFor="surnames" className="required-label">Apellidos:</FormLabel>
                                                <FormControl type="text" name="userSurname" id="surnames" disabled={isPending} defaultValue={state.userSurname} className="form-control" required />
                                                <div className="invalid-feedback">Los apellidos son obligatorios.</div>
                                            </div>
                                        </div>
                                        <br />
                                        <div className="form-field">
                                            <FormLabel htmlFor="nickname" className="required-label">Nickname (Apodo):</FormLabel>
                                            <FormControl type="text" name="userNickname" id="nickname" disabled={isPending} defaultValue={state.userNickname} className="form-control" minLength={3} maxLength={15} required />
                                            <div className="invalid-feedback">El nickname debe tener entre 3 y 15 caracteres.</div>
                                        </div>
                                    </div>
                                    <div className="d-flex flex-column flex-md-row justify-content-evenly mt-5 mb-4">
                                        <Link to="/new" className="submit-btn" style={{ background: '#6c757d', marginTop: 0, justifyContent: 'center', display: 'flex', textDecoration: 'none' }}>
                                            <ArrowLeft />
                                            <span className="ms-2">Volver al inicio</span>
                                        </Link>

                                        <Button
                                            type="submit"
                                            className="submit-btn"
                                            style={{ marginTop: 0, justifyContent: 'center', display: 'flex' }}
                                            disabled={isPending}
                                        >
                                            {isPending ? (
                                                <>
                                                    <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" className="me-2" />
                                                    Registrando...
                                                </>
                                            ) : (
                                                <>
                                                    <span className="me-2">Completar Registro</span>
                                                    <PersonPlusFill />
                                                </>
                                            )}
                                        </Button>

                                    </div>
                                    <div className="text-center pt-4 border-top">
                                        <p className="mb-2 text-muted">¿Ya tienes cuenta en La Caverna del Dragón?</p>
                                        <Link to="/new/login" className="text-primary fw-bold text-decoration-none" style={{ fontSize: '1.1rem' }}>
                                            <BoxArrowInRight className="me-2" /> Iniciar sesión
                                        </Link>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </main>
    )
};