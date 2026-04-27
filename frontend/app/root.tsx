import {
    isRouteErrorResponse,
    Links,
    Meta,
    Navigate,
    Outlet,
    Scripts,
    ScrollRestoration,
    useNavigation,
} from "react-router";
import { useEffect } from "react";

import type { Route } from "./+types/root";
import appHref from "./app.css?url";

export function links() {
    return [
        { rel: "stylesheet", href: "https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" },
        { rel: "stylesheet", href: appHref }
    ];
}

const ERROR_PATH = "/new/error";


function GlobalSpinner() {
    const navigation = useNavigation();

    const isLoading = navigation.state !== "idle";

    if (!isLoading) return null;

    return (
        <div
            className="position-fixed top-0 start-0 w-100 h-100 d-flex flex-column align-items-center justify-content-center"
            style={{
                zIndex: 9999,
                backgroundColor: "rgba(255, 255, 255, 0.7)",
                backdropFilter: "blur(2px)"
            }}
        >
            <div className="spinner-border" style={{ color: "#a71b12" }} role="status">
                <span className="visually-hidden">Cargando...</span>
            </div>
            <p className="mt-3 text-muted fw-bold">Cargando contenido...</p>
        </div>
    );
}

export function Layout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="en">
            <head>
                <meta charSet="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1" />
                <Meta />
                <Links />
            </head>
            <body>
                {children}
                <ScrollRestoration />
                <Scripts />
            </body>
        </html>
    );
}

export default function App() {
    useEffect(() => {
        const redirectToError = (statusCode: number) => {
            if (window.location.pathname === ERROR_PATH) {
                return;
            }

            const errorUrl = buildErrorUrl(
                String(statusCode),
                getErrorMessageByStatus(statusCode),
            );

            window.location.assign(errorUrl);
        };

        const onError = () => redirectToError(500);
        const onUnhandledRejection = () => redirectToError(500);

        window.addEventListener("error", onError);
        window.addEventListener("unhandledrejection", onUnhandledRejection);

        return () => {
            window.removeEventListener("error", onError);
            window.removeEventListener("unhandledrejection", onUnhandledRejection);
        };
    }, []);

    return (<><GlobalSpinner />
        <Outlet /></>
    );
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
    const statusCode = isRouteErrorResponse(error) ? error.status : 500;
    const safeStatusCode = Number.isInteger(statusCode) ? statusCode : 500;
    const message = getErrorMessageByStatus(safeStatusCode);

    return (
        <Navigate
            replace
            to={buildErrorUrl(String(safeStatusCode), message)}
        />
    );
}

function buildErrorUrl(code: string, message: string): string {
    return `${ERROR_PATH}?code=${encodeURIComponent(code)}&text=${encodeURIComponent(message)}`;
}


function getErrorMessageByStatus(statusCode: number): string {
    switch (statusCode) {
        case 401:
            return "Debes iniciar sesion para acceder a este recurso.";
        case 404:
            return "La pagina solicitada no existe o ha sido movida.";
        case 403:
            return "No tienes el permiso necesario para ver este contenido.";
        case 500:
            return "Error interno del servidor. Lo estamos revisando.";
        case 400:
            return "La solicitud enviada no es valida.";
        default:
            return "Ha ocurrido un error inesperado.";
    }
}