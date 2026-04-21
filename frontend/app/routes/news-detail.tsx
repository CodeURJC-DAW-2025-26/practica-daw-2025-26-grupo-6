import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { Alert, Image, Button, Modal } from "react-bootstrap";
import type { Route } from "./+types/news-detail";
import { getNew, removeNew } from "~/services/news-service";
//import { useUserStore } from "~/stores/user-store";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  return await getNew(Number(params.id));
}

export default function NewsDetail({ loaderData }: Route.ComponentProps) {
  // let { user } = useUserStore();
  const post = loaderData;
  const navigate = useNavigate();

  const formattedDate = post.creationDate
    ? new Date(post.creationDate).toLocaleDateString("es-ES", {
      year: "numeric",
      month: "long",
      day: "numeric",
    })
    : "";

  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [isPendingDelete, setPendingDelete] = useState(false);
  const [isDeleteDialogOpen, setDeleteDialogOpen] = useState(false);

  async function handleDelete() {
    setPendingDelete(true);
    setDeleteError(null);
    try {
      await removeNew(post.newId);
      navigate("/new/news");
    } catch (err) {
      console.error(err);
      setDeleteError("Hubo un error al borrar la noticia.");
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
                  {post.newImage ? (
                    <Image
                      src={`/api/v1/images/${post.newImage.id}/media`}
                      className="img-fluid"
                      alt={post.newName}
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
                      <i className="bi bi-newspaper fs-1"></i>
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
                      <i className="bi bi-tag-fill me-1"></i> {post.newTag}
                    </span>
                  </div>
                </div>
              </div>

              <div className="col-lg-7 col-md-12">
                <div className="project-info ps-lg-4 mt-4 mt-lg-0">
                  <h1
                    className="project-title"
                    style={{
                      fontWeight: 700,
                      color: "#212529",
                      marginTop: 0,
                      marginBottom: "5px",
                    }}
                  >
                    {post.newName}
                  </h1>

                  <div className="mb-4 text-muted d-flex align-items-center flex-wrap gap-3" style={{ fontSize: "0.95rem" }}>
                    <div>
                      <i className="bi bi-person-circle me-1"></i> Creado por:{" "}
                      {post.newCreator ? (
                        <Link
                          to={`/user/${post.newCreator.userId}`}
                          className="text-decoration-none fw-bold"
                          style={{ color: "#a71b12" }}
                        >
                          {post.newCreator.userNickname || "La Caverna del Dragón"}
                        </Link>
                      ) : (
                        <Link to="/" className="text-decoration-none fw-bold" style={{ color: "#a71b12" }}>
                          La Caverna del Dragón
                        </Link>
                      )}
                    </div>

                    {formattedDate && (
                      <div className="border-start ps-3">
                        <i className="bi bi-calendar3 me-1"></i> {formattedDate}
                      </div>
                    )}
                  </div>

                  <div
                    className="project-description mb-4"
                    style={{ fontSize: "1.1rem", lineHeight: 1.6 }}
                  >
                    {post.newDescription}
                  </div>
                  {/*
                  {user && (
                    <div className="row mt-5 pt-3 border-top align-items-center">
                      <div className="col-auto">
                        <Link
                          className="btn btn-link text-decoration-none p-0"
                          style={{ color: "#890f00" }}
                          to={`/new/news-edit/${post.newId}`}
                        >
                          <i className="bi bi-pencil-square me-1"></i> Modificar noticia
                        </Link>
                      </div>

                      <div className="col-auto ms-3">
                        <Button
                          type="button"
                          className="btn btn-link text-decoration-none p-0"
                          style={{ boxShadow: "none", color: "#890f00" }}
                          onClick={handleOpenDeleteDialog}
                          disabled={isPendingDelete}
                        >
                          <i className="bi bi-trash me-1"></i> Eliminar
                        </Button>
                      </div>
                    </div>
                  )}
                  */}
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>
      <Modal show={isDeleteDialogOpen} onHide={handleCloseDeleteDialog}>
        <Modal.Header closeButton>
          <Modal.Title>Eliminar Noticia</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            ¿Estás seguro de que quieres eliminar <b>"{post.newName}"</b>?
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
