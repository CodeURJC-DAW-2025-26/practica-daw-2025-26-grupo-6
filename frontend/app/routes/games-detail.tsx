import { Link, useNavigate } from "react-router";
import { Image, Button, Form, Alert, Modal } from "react-bootstrap";
import type { Route } from "./+types/games-detail";
import { getGame, removeGame } from "~/services/games-service";
import { useState } from "react";
import { useUserStore } from "~/stores/user-store";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  return await getGame(Number(params.id));
}

export default function GamesDetail({ loaderData }: Route.ComponentProps) {
  let { user } = useUserStore();
  const game = loaderData;
  const navigate = useNavigate();

  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [isPendingDelete, setPendingDelete] = useState(false);
  const [isDeleteDialogOpen, setDeleteDialogOpen] = useState(false);

  async function handleDelete() {
    setPendingDelete(true);
    setDeleteError(null);
    try {
      await removeGame(game.gameId);
      navigate("/new/games");
    } catch (err) {
      console.error(err);
      setDeleteError("Hubo un error al borrar el juego.");
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
                  {game.gameImage ? (
                    <Image
                      src={`/api/v1/images/${game.gameImage.id}/media`}
                      className="img-fluid"
                      alt={game.gameName}
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
                  {/*
                  <div style={{ position: "absolute", top: "15px", left: "15px", zIndex: 10 }}>
                    <Form action={`/new/game/${game.gameId}/toggle-fav`} method="post" className="m-0">
                      <Button className="btn btn-light rounded-circle shadow-sm d-flex align-items-center justify-content-center"
                        style={{ width: "42px", height: "42px", border: "none" }}
                        title={isFavourite ? "Quitar de favoritos" : "Anadir a favoritos"}>
                        {isFavourite ? (
                          <i className="bi bi-heart-fill text-danger fs-5"></i>
                        ) : (
                          <i className="bi bi-heart text-danger fs-5"></i>
                        )}
                      </Button>
                    </Form>
                  </div>
                  */}
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
                    {game.gameName}
                  </h1>

                  <div
                    className="project-description mb-4"
                    style={{ fontSize: "1.1rem", lineHeight: 1.6 }}
                  >
                    {game.gameDescription}
                  </div>

                  <div className="row bg-light rounded p-3 mb-4 mx-0 border text-center text-sm-start">

                    <div className="col-sm-4 mb-3 mb-sm-0">
                      <strong className="text-primary d-block mb-1"><i className="bi bi-people-fill me-2"></i>Jugadores</strong>
                      <span className="text-dark fs-5">{game.minPlayers} - {game.maxPlayers}</span>
                    </div>

                    <div className="col-sm-4 mb-3 mb-sm-0">
                      <strong className="text-primary d-block mb-1"><i className="bi bi-clock-fill me-2"></i>Duración</strong>
                      <span className="text-dark fs-5">{game.minDuration} - {game.maxDuration}'</span>
                    </div>

                    <div className="col-sm-4">
                      <strong className="text-primary d-block mb-1"><i className="bi bi-controller me-2"></i>Género</strong>
                      <span className="text-dark fs-5">{game.genre}</span>
                    </div>

                  </div>


                  {user?.userRoles?.includes("ADMIN") && (
                    <div className="row mt-5 pt-3 border-top align-items-center">
                      <div className="col-auto">
                        <Link
                          className="btn btn-link text-decoration-none p-0"
                          style={{ color: "#890f00" }}
                          to={`/new/games-edit/${game.gameId}`}
                        >
                          <i className="bi bi-pencil-square me-1"></i> Modificar juego
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

                </div>
              </div>
            </div>
          </div>
        </section>
      </main>
      <Modal show={isDeleteDialogOpen} onHide={handleCloseDeleteDialog}>
        <Modal.Header closeButton>
          <Modal.Title>Eliminar Juego</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            ¿Estás seguro de que quieres eliminar <b>"{game.gameName}"</b>?
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
