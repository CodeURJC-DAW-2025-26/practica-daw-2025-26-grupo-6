import { useEffect } from "react";
import { Link } from "react-router";
import { useUserStore } from "~/stores/user-store";

export default function Header() {
    let { loadLoggedUser } = useUserStore();

    useEffect(() => {
        loadLoggedUser();
    }, [loadLoggedUser]);

    return <>
        <header id="header" className="header d-flex align-items-center sticky-top">
            <div className="container-fluid container-xl position-relative d-flex align-items-center">
                <Link to="/new" className="logo d-flex align-items-center me-auto">
                    <img src={'/favicon.ico'} width="64" height="64" />
                    <h1 className="sitename">La Caverna Del Dragón</h1>
                </Link>
                <nav id="navmenu" className="navmenu">
                    <ul>
                        <li><Link to="/new/games">Juegos</Link></li>
                        <li><Link to="/new/events">Eventos</Link></li>
                        <li><Link to="/new/news">Noticias</Link></li>
                    </ul>
                </nav>
            </div>
        </header>
    </>;
}