import { Link } from "react-router";

export default function Header() {
    return <>
        <header id="header" className="header d-flex align-items-center sticky-top">
            <div className="container-fluid container-xl position-relative d-flex align-items-center">
                <a href="/" className="logo d-flex align-items-center me-auto">
                    <img src={'/public/favicon.ico'} width="64" height="64" />
                    <h1 className="sitename">La Caverna Del Dragón</h1>
                </a>
                <nav id="navmenu" className="navmenu">
                    <ul>
                        <li><Link to="/game/games">Juegos</Link></li>
                        <li><Link to="/event/events">Eventos</Link></li>
                        <li><Link to="/new/news">Noticias</Link></li>
                    </ul>
                </nav>
            </div>
        </header>
    </>;
}