import { Link, useSearchParams } from "react-router";

export default function ErrorPage() {
    const [searchParams] = useSearchParams();

    const errorCode = searchParams.get("code") || "Error Desconocido";
    const errorText = searchParams.get("text") || "Ha ocurrido un error.";

    return (
        <main className="main py-5">
            <section id="error-page" className="section">
                <div className="container">
                    <div className="row justify-content-center">
                        <div className="col-lg-8 col-md-10">
                            <div
                                className="text-center p-5"
                                style={{
                                    backgroundColor: "#e5b7b7",
                                    borderRadius: "12px",
                                    boxShadow: "0 2px 10px rgba(0,0,0,0.05)",
                                }}
                            >
                                <h1
                                    style={{
                                        fontWeight: 700,
                                        color: "#a71b12",
                                        marginBottom: "15px",
                                    }}
                                >
                                    ERROR: {errorCode}
                                </h1>
                                <p
                                    style={{
                                        fontSize: "1.15rem",
                                        color: "#555",
                                        marginBottom: "30px",
                                    }}
                                >
                                    {errorText}
                                </p>
                                <Link
                                    to="/new"
                                    className="btn px-4 py-2"
                                    style={{
                                        backgroundColor: "#a71b12",
                                        color: "white",
                                        fontWeight: 500,
                                        borderRadius: "6px",
                                    }}
                                >
                                    Volver al Inicio
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </main>
    );
}
