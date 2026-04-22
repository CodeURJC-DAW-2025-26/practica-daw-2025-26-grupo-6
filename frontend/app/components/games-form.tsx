import type { FormEvent } from "react";
import { Button, Form } from "react-bootstrap";
import type GameDTO from "~/dtos/GameDTO";

interface GamesFormProps {
  game?: Partial<GameDTO>;
  actionState: [
    { success: boolean; error: string | null } | null,
    (formData: FormData) => void,
    boolean,
  ];
  onCancel: () => void;
}

export default function GamesForm({
  game,
  actionState: [state, formAction, isPending],
  onCancel,
}: GamesFormProps) {
  const isEditing = game?.gameId;

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    const form = event.currentTarget;

    if (!form.checkValidity()) {
      event.preventDefault();
      event.stopPropagation();
    }

    form.classList.add("was-validated");
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
                    {isEditing ? "Modificar" : "Añadir"} un juego
                  </h3>
                  <p>
                    {isEditing
                      ? "Modifica los datos del juego seleccionado."
                      : "Rellena los campos para publicar un nuevo juego."}
                  </p>
                </div>

                {state?.error && (
                  <div
                    className="alert alert-danger alert-dismissible fade show mb-4"
                    role="alert"
                    style={{ borderLeft: "5px solid #890f00" }}
                  >
                    <strong>
                      <i className="bi bi-exclamation-triangle-fill me-2"></i> Ups! Hay errores:
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
                >
                  <div className="form">
                    {isEditing && (<input type="hidden" name="gameId" value={game?.gameId ?? ""} />)}

                    <div className="form-field">
                      <Form.Label htmlFor="gameName" className="required-label">Nombre del Juego:</Form.Label>
                      <Form.Control
                        type="text"
                        name="gameName"
                        id="gameName"
                        defaultValue={game?.gameName ?? ""}
                        className="form-control"
                        minLength={2}
                        maxLength={100}
                        required
                      />
                      <div className="invalid-feedback">
                        El nombre del juego debe tener entre 2 y 100 caracteres.
                      </div>
                    </div>
                    <br />

                    <div className="form-field full-width">
                      <Form.Label htmlFor="gameDescription" className="required-label">Descripción:</Form.Label>
                      <textarea
                        name="gameDescription"
                        id="gameDescription"
                        rows={5}
                        className="form-control"
                        minLength={10}
                        maxLength={2000}
                        defaultValue={game?.gameDescription ?? ""}
                        required
                      />
                      <div className="invalid-feedback">
                        La descripción del juego debe tener entre 10 y 2000 caracteres.
                      </div>
                    </div>
                    <br />

                    <div className="form-field">
                      <Form.Label htmlFor="genre" className="required-label">Género:</Form.Label>
                      <Form.Control
                        type="text"
                        name="genre"
                        id="genre"
                        defaultValue={game?.genre ?? ""}
                        className="form-control"
                        minLength={3}
                        maxLength={50}
                        required
                      />
                      <div className="invalid-feedback">
                        El género del juego debe tener entre 3 y 50 caracteres.
                      </div>
                    </div>
                    <br />

                    <div className="row mt-4 p-3 mb-2 bg-light rounded border">
                      <div className="col-12 mb-2">
                        <h6 className="text-primary border-bottom">Jugadores</h6>
                      </div>
                      <div className="col-md-6 form-field">
                        <Form.Label htmlFor="minPlayers">Mínimo:</Form.Label>
                        <Form.Control
                          type="number"
                          name="minPlayers"
                          id="minPlayers"
                          defaultValue={game?.minPlayers ?? ""}
                          className="form-control"
                          min="1"
                          required
                        />
                        <div className="invalid-feedback">
                          El número mínimo de jugadores debe ser mayor o igual a 1.
                        </div>
                      </div>
                      <div className="col-md-6 form-field">
                        <Form.Label htmlFor="maxPlayers">Máximo:</Form.Label>
                        <Form.Control
                          type="number"
                          name="maxPlayers"
                          id="maxPlayers"
                          defaultValue={game?.maxPlayers ?? ""}
                          className="form-control"
                          min="1"
                          required
                        />
                        <div className="invalid-feedback">
                          El número máximo de jugadores debe ser mayor o igual a 1.
                        </div>
                      </div>
                    </div>
                    <br />

                    <div className="row mt-3 p-3 mb-2 bg-light rounded border">
                      <div className="col-12 mb-2">
                        <h6 className="text-primary border-bottom">Duración (minutos)</h6>
                      </div>
                      <div className="col-md-6 form-field">
                        <Form.Label htmlFor="minDuration">Mínima:</Form.Label>
                        <Form.Control
                          type="number"
                          name="minDuration"
                          id="minDuration"
                          defaultValue={game?.minDuration ?? ""}
                          className="form-control"
                          min="1"
                          required
                        />
                        <div className="invalid-feedback">
                          La duración mínima debe ser mayor o igual a 1 minuto.
                        </div>
                      </div>
                      <div className="col-md-6 form-field">
                        <Form.Label htmlFor="maxDuration">Máxima:</Form.Label>
                        <Form.Control
                          type="number"
                          name="maxDuration"
                          id="maxDuration"
                          defaultValue={game?.maxDuration ?? ""}
                          className="form-control"
                          min="1"
                          required
                        />
                        <div className="invalid-feedback">
                          La duración máxima debe ser mayor o igual a 1 minuto.
                        </div>
                      </div>
                    </div>

                    <br />

                    <div className="form-field">
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

                  <div className="d-flex flex-column flex-md-row justify-content-evenly mt-5 mb-2">
                    <Button
                      type="button"
                      className="submit-btn"
                      style={{
                        background: "#6c757d",
                        marginTop: 0,
                        justifyContent: "center",
                        display: "flex",
                        textDecoration: "none",
                      }}
                      onClick={onCancel}
                      disabled={isPending}
                    >
                      <i className="bi bi-arrow-left"></i>
                      <span className="ms-2">Atrás</span>
                    </Button>

                    <Button
                      type="submit"
                      className="submit-btn"
                      style={{
                        marginTop: 0,
                        justifyContent: "center",
                        display: "flex",
                      }}
                      disabled={isPending}
                    >
                      <span className="me-2">
                        {isPending
                          ? "Guardando..."
                          : isEditing
                            ? "Actualizar Juego"
                            : "Guardar Juego"}
                      </span>
                      <i className="bi bi-check2-circle"></i>
                    </Button>
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
