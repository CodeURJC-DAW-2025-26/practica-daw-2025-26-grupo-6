import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router";
import { useUserStore } from "~/stores/user-store";
import { Button, Dropdown, Form, Image } from "react-bootstrap";





export default function Header() {
    let { user, loadLoggedUser } = useUserStore();
    const navigate = useNavigate();

    // Par controlar el menu de movil
    const [isMobileNavActive, setIsMobileNavActive] = useState(false);

    useEffect(() => {
        loadLoggedUser();
    }, [loadLoggedUser]);

    // Par alternar el menu
    const toggleMobileNav = () => {
        setIsMobileNavActive(!isMobileNavActive);
        document.body.classList.toggle('mobile-nav-active');
    };

    // Para que se cierre el menu
    const closeMobileNav = () => {
        if (isMobileNavActive) {
            setIsMobileNavActive(false);
            document.body.classList.remove('mobile-nav-active');
        }
    };

    return <>
        <header id="header" className="header d-flex align-items-center sticky-top">
            <div className="container-fluid container-xl position-relative d-flex align-items-center">
                <Link to="/new" className="logo d-flex align-items-center me-auto">
                    <img src={'/favicon.ico'} width="64" height="64" />
                    <h1 className="sitename">La Caverna Del Dragón</h1>
                </Link>
                <nav id="navmenu" className="navmenu">
                    <ul>
                        <li><Link to="/new/games" onClick={closeMobileNav}>Juegos</Link></li>
                        <li><Link to="/new/events" onClick={closeMobileNav}>Eventos</Link></li>
                        <li><Link to="/new/news" onClick={closeMobileNav}>Noticias</Link></li>
                        {user && (
                            <>
                                <li><Link className="d-xl-none" to="/new/profile" onClick={closeMobileNav}>Mi perfil</Link></li>
                                {user.userRoles?.includes("ADMIN") && (
                                    <li><Link className="d-xl-none" to="/new/admin" onClick={closeMobileNav}>Administración</Link></li>
                                )}
                                <li>
                                    <button
                                        className="btn btn-link d-xl-none nav-link"
                                        style={{ textAlign: 'left', border: 'none' }}
                                        onClick={() => {/*logout*/ closeMobileNav(); }}
                                    >
                                        Cerrar sesión
                                    </button>
                                </li>
                            </>
                        )}
                    </ul>
                    <i
                        className={`mobile-nav-toggle d-xl-none bi ${isMobileNavActive ? 'bi-x' : 'bi-list'}`}
                        onClick={toggleMobileNav}
                    ></i>
                </nav>
                {!user && (
                    <Link className="btn-getstarted d-none d-sm-block" to="/new/login">
                        Registro / Iniciar Sesión
                        <i className="bi bi-person ms-1"></i>
                    </Link>
                )}
                {user && (
                    <div className="d-none d-xl-block ms-4">
                        <Dropdown align="end">
                            <Dropdown.Toggle as="a" className="submit-btn rounded-circle p-0" id="dropdown-user" style={{ cursor: 'pointer' }}>
                                <Image
                                    //No funciona bien, si alguien sabe cual es la ruta correcta que la cambie porfavor.
                                    src={`/api/v1/images/${user.userImage}/media`}
                                    roundedCircle
                                    width="45"
                                    height="45"
                                    style={{ objectFit: 'cover', border: '2px solid var(--accent-color)' }}
                                    alt="User"
                                />
                            </Dropdown.Toggle>

                            <Dropdown.Menu>
                                <Dropdown.Item as={Link} to="/new/profile">
                                    <i className="bi bi-person me-2"></i> Mi perfil
                                </Dropdown.Item>

                                {user.userRoles?.includes("ADMIN") && (
                                    <Dropdown.Item as={Link} to="/new/admin">
                                        <i className="bi bi-shield-lock me-2"></i> Administración
                                    </Dropdown.Item>
                                )}

                                <Dropdown.Divider />
                                {/*Dentro del onClick hay que poner el comando de logout cuando este listo*/}
                                <Dropdown.Item /*onClick={ }*/ className="text-danger">
                                    <i className="bi bi-box-arrow-right me-2"></i> Cerrar sesión
                                </Dropdown.Item>
                            </Dropdown.Menu>
                        </Dropdown>
                    </div>
                )}

            </div>
        </header>
    </>;
}