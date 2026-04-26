import { useActionState, useRef, useState } from "react";
import { Button, FormControl, FormLabel } from "react-bootstrap";
import { ArrowLeft, BoxArrowInRight, ExclamationCircleFill, PersonPlus } from "react-bootstrap-icons";
import { Form, Link, useNavigate } from "react-router";
import { useUserStore } from "~/stores/user-store";

export default function Login() {

  const userStore = useUserStore()

  const navigate = useNavigate()

  const [wasValidated, setWasValidated] = useState(false)

  const formRef = useRef<HTMLFormElement>(null);

  const [errorMessage, setErrorMessage] = useState("")


  async function loginAction(prevState: {}, formData: FormData) {
    const email = (formData.get("email") as string) ?? "";
    const password = (formData.get("password") as string) ?? "";

    const form = formRef.current;

    setWasValidated(true)

    if (form && form.checkValidity()) {
      try {
        await userStore.loginUser(email, password)

        const error = useUserStore.getState().errorMessage;

        if (error) {
          setErrorMessage(error)
        } else {
          navigate("/new/")
        }

      } catch (error) {
        setErrorMessage("Error al iniciar sesión.");
      }
    }

    return { email, password }
  }

  const [state, formAction, isPending] = useActionState(
    loginAction,
    { email: "", password: "" }
  )

  return (
    <main className="main">
      <section id="quote" className="quote section pt-0">
        <div className="container" data-aos="fade-up" data-aos-delay={100}>
          <div className="row justify-content-center">
            <div className="col-lg-8" data-aos="fade-left" data-aos-delay={300}>
              <div className="quote-form">
                <div className="form-intro text-center mb-4">
                  <h3>Bienvenido de nuevo</h3>
                  <p>Introduce tus credenciales para acceder a La Caverna del Dragón.</p>
                </div>
                {errorMessage && (
                  <div className="alert alert-danger alert-dismissible fade show mb-4" role="alert" style={{ borderLeft: '5px solid #890f00' }}>
                    <strong><ExclamationCircleFill /> Error de acceso:</strong>
                    <p className="mb-0 mt-1">{errorMessage}</p>
                    <Button type="button" className="btn-close" data-bs-dismiss="alert" aria-label="Close" />
                  </div>
                )}
                <form action={formAction} ref={formRef} method="post" className={`custom-login-form needs-validation ${wasValidated && "was-validated"}`} noValidate>
                  <div className="form">
                    <div className="form-field">
                      <FormLabel htmlFor="email" className="required-label">Correo electrónico:</FormLabel>
                      <FormControl type="email" disabled={isPending} defaultValue={state.email} className="form-control" name="email" id="email" placeholder="ejemplo@correo.com" required />
                      <div className="invalid-feedback">
                        Por favor, ingrese un correo electrónico válido.
                      </div>
                    </div>
                    <br />
                    <div className="form-field">
                      <FormLabel htmlFor="password" className="required-label">Contraseña:</FormLabel>
                      <FormControl type="password" disabled={isPending} defaultValue={state.password} className="form-control" name="password" id="password" placeholder="********" minLength={4} maxLength={20} required />
                      <div className="invalid-feedback">
                        Contraseña incorrecta.
                      </div>
                    </div>
                  </div>
                  <div className="d-flex flex-column flex-md-row justify-content-evenly mt-5 mb-4">
                    <Link to="/new" className="submit-btn" style={{ background: '#6c757d', marginTop: 0, justifyContent: 'center', display: 'flex', textDecoration: 'none' }}>
                      <ArrowLeft />
                      <span className="ms-2">Volver</span>
                    </Link>
                    {isPending && (<div style={{ marginTop: 0, justifyContent: 'center', display: 'flex' }}>
                      <div className="spinner-border" role="status">
                        <span className="visually-hidden">Loading...</span>
                      </div>
                    </div>)}
                    {!isPending && (<Button type="submit" className="submit-btn" style={{ marginTop: 0, justifyContent: 'center', display: 'flex' }}>
                      <span className="me-2">Iniciar sesión</span>
                      <BoxArrowInRight />
                    </Button>)}

                  </div>
                  <div className="text-center pt-4 border-top">
                    <p className="mb-2 text-muted">¿No tienes cuenta en La Caverna del Dragón?</p>
                    <Link to="/new/register" className="text-primary fw-bold text-decoration-none" style={{ fontSize: '1.1rem' }}>
                      <PersonPlus className="me-2" /> Click aquí para crear una
                    </Link>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}