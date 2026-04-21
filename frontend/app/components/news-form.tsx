import type { FormEvent } from "react";
import { Button, Form } from "react-bootstrap";
import type NewDTO from "~/dtos/NewDTO";

interface NewsFormProps {
  post?: Partial<NewDTO>;
  actionState: [
    { success: boolean; error: string | null } | null,
    (formData: FormData) => void,
    boolean,
  ];
  onCancel: () => void;
}

export default function NewsForm({
  post,
  actionState: [state, formAction, isPending],
  onCancel,
}: NewsFormProps) {
  const isEditing = post?.newId;

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
                    {isEditing ? "Modificar" : "Añadir"} una noticia
                  </h3>
                  <p>
                    {isEditing
                      ? "Modifica los datos de la noticia seleccionada."
                      : "Rellena los campos para publicar una nueva noticia."}
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
                    {isEditing && (<input type="hidden" name="newId" value={post?.newId ?? ""} />)}

                    <div className="form-field">
                      <Form.Label htmlFor="newName" className="required-label">
                        Título:
                      </Form.Label>
                      <Form.Control
                        type="text"
                        name="newName"
                        id="newName"
                        defaultValue={post?.newName ?? ""}
                        className="form-control"
                        minLength={10}
                        maxLength={100}
                        required
                        disabled={isPending}
                      />
                      <div className="invalid-feedback">
                        El título debe tener entre 10 y 100 caracteres.
                      </div>
                    </div>
                    <br />

                    <div className="form-field full-width">
                      <Form.Label htmlFor="newDescription" className="required-label">
                        Descripción:
                      </Form.Label>
                      <Form.Control
                        as="textarea"
                        name="newDescription"
                        id="newDescription"
                        rows={8}
                        className="form-control"
                        minLength={50}
                        maxLength={3000}
                        required
                        defaultValue={post?.newDescription ?? ""}
                        disabled={isPending}
                      ></Form.Control>
                      <div className="invalid-feedback">
                        La descripción debe tener entre 50 y 3000 caracteres.
                      </div>
                    </div>
                    <br />

                    <div className="form-field">
                      <Form.Label htmlFor="newTag" className="form-label">
                        Tag:
                      </Form.Label>
                      <Form.Control
                        type="text"
                        name="newTag"
                        id="newTag"
                        defaultValue={post?.newTag ?? ""}
                        className="form-control"
                        maxLength={50}
                        disabled={isPending}
                      />
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
                            ? "Actualizar Noticia"
                            : "Guardar Noticia"}
                      </span>
                      <i className="bi bi-check2-circle"></i>
                    </Button>
                  </div>

                </Form>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
