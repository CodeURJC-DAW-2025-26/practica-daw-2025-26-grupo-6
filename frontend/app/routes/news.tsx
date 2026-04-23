import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router";
import type { Route } from "./+types/news";
import { getNews } from "~/services/news-service";
import { Button, Form } from "react-bootstrap";
import { useUserStore } from "~/stores/user-store";

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  const name = url.searchParams.get("name") ?? "";
  const tag = url.searchParams.get("tag") ?? "";
  const page = Number(url.searchParams.get("page") ?? "0");
  const result = await getNews(name, tag, page);

  return { name, tag, items: result.items, hasNext: result.hasNext };
}

export default function News({ loaderData }: Route.ComponentProps) {
  const [news, setNews] = useState(loaderData.items);
  const [hasNext, setHasNext] = useState(loaderData.hasNext);
  const [currentPage, setCurrentPage] = useState(1);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [loadMoreError, setLoadMoreError] = useState(false);

  useEffect(() => {
    setNews(loaderData.items);
    setHasNext(loaderData.hasNext);
    setCurrentPage(1);
    setIsLoadingMore(false);
    setLoadMoreError(false);
  }, [loaderData]);

  const handleLoadMore = async () => {
    if (isLoadingMore || !hasNext) {
      return;
    }

    setIsLoadingMore(true);
    setLoadMoreError(false);

    try {
      const result = await getNews(loaderData.name, loaderData.tag, currentPage);
      setNews((previousNews) => [...previousNews, ...result.items]);
      setHasNext(result.hasNext);

      if (result.hasNext) {
        setCurrentPage((previousPage) => previousPage + 1);
      }
    } catch {
      setLoadMoreError(true);
    } finally {
      setIsLoadingMore(false);
    }
  };

  let { user } = useUserStore();

  const navigate = useNavigate();

  return (
    <main className="main">
      <section id="projects" className="projects section">
        <div className="container" data-aos="fade-up">
          <div className="form-intro text-center mb-5 mt-4">
            <h1
              style={{
                fontWeight: 700,
                color: "#212529",
                textTransform: "uppercase",
              }}
            >
              Noticias
            </h1>
            <p className="text-muted">
              Mantente al dia con las ultimas novedades y anuncios
            </p>
            <hr
              style={{
                width: "50px",
                margin: "20px auto",
                borderTop: "3px solid #a71b12",
                opacity: 1,
              }}
            />
          </div>

          <div className="card shadow-sm border-0 p-3 bg-light mb-5">
            <form method="get" action="/new/news">
              <div className="row g-2 align-items-center">
                <div className="col-lg-6 col-md-5">
                  <input
                    name="name"
                    className="form-control"
                    type="search"
                    placeholder="Buscar por titulo..."
                    defaultValue={loaderData.name}
                  />
                </div>
                <div className="col-lg-2 col-md-3">
                  <Form.Select name="tag" className="form-select" defaultValue={loaderData.tag}>
                    <option value="">Cualquier tema</option>
                    <option value="Cambios">Cambios</option>
                    <option value="Evento">Evento</option>
                    <option value="Rol">Rol</option>
                  </Form.Select>
                </div>

                <div className="col-lg-2 col-md-2">
                  <Button
                    className="btn_lcdd w-100 d-flex align-items-center justify-content-center"
                    type="submit"
                    style={{
                      height: "38px",
                      border: "none",
                      backgroundColor: "#a71b12",
                    }}
                  >
                    <i className="bi bi-search me-2"></i> Buscar
                  </Button>
                </div>

                {user && (
                  <div className="col-lg-2 col-md-2">
                    <Button
                      onClick={() => navigate("/new/news-create")}
                      className="btn_lcdd_black w-100 text-decoration-none d-flex align-items-center justify-content-center"
                      style={{ height: "38px" }}
                    >
                      <i className="bi bi-plus-circle me-2"></i> Redactar
                    </Button>
                  </div>
                )}

              </div>
            </form>
          </div>

          <div id="news-container" className="row gy-4">
            {news.length > 0 ? (
              news.map((item) => (
                <div key={item.newId} className="col-lg-4 col-md-6 news-item">
                  <div className="project-card h-100 shadow-sm border-0">
                    <Link to={`/new/news/${item.newId}`} className="text-decoration-none">
                      <div className="project-image" style={{ height: "220px", overflow: "hidden" }}>
                        {item.newImage ? (
                          <img
                            src={`/api/v1/images/${item.newImage.id}/media`}
                            alt={item.newName}
                            className="img-fluid w-100 h-100"
                            style={{ objectFit: "cover" }}
                          />
                        ) : (
                          <div className="bg-secondary w-100 h-100 d-flex align-items-center justify-content-center text-white">
                            <i className="bi bi-newspaper fs-1"></i>
                          </div>
                        )}
                      </div>
                      <div className="project-info p-3">
                        <div className="project-category mb-2 fw-bold" style={{ color: "#a71b12", fontSize: "0.8rem" }}>
                          {item.newTag}
                        </div>
                        <h4 className="project-title text-dark">{item.newName}</h4>
                        <p
                          className="project-description text-muted small"
                          style={{
                            display: "-webkit-box",
                            WebkitLineClamp: 3,
                            WebkitBoxOrient: "vertical",
                            overflow: "hidden",
                          }}
                        >
                          {item.newDescription}
                        </p>
                      </div>
                    </Link>
                  </div>
                </div>
              ))
            ) : (
              <div className="col-12 text-center py-5">
                <i className="bi bi-journal-x fs-1 text-muted"></i>
                <p className="mt-3 text-muted">
                  No hay noticias que coincidan con tu busqueda.
                </p>
                <Link to="/new/news" className="btn btn-link text-danger">
                  Ver todas las noticias
                </Link>
              </div>
            )}
          </div>

          {news.length > 0 && hasNext && (
            <div className="text-center mt-5 mb-5">
              <Button
                id="load-more-news-btn"
                className="btn-load-more"
                onClick={handleLoadMore}
                disabled={isLoadingMore}
                type="button"
              >
                <span id="btn-news-text">
                  {isLoadingMore
                    ? "Cargando..."
                    : loadMoreError
                      ? "Error. Reintentar"
                      : "Cargar más noticias"}
                </span>
                <span
                  id="btn-news-spinner"
                  className={`spinner-border spinner-border-sm ${isLoadingMore ? "" : "d-none"}`}
                  role="status"
                ></span>
              </Button>
            </div>
          )}
        </div>
      </section>
    </main>
  );
}
