import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router";
import { useUserStore } from "~/stores/user-store";
import { Button, Dropdown, Form, Image, ListGroup, ListGroupItem } from "react-bootstrap";
import { BoxArrowRight, Person, ShieldLock } from "react-bootstrap-icons";

export default function Header() {

    let { user, loadLoggedUser, logoutUser } = useUserStore();
    const navigate = useNavigate();

    const [isMobileNavActive, setIsMobileNavActive] = useState(false);

    useEffect(() => {
        loadLoggedUser();
    }, [loadLoggedUser]);

    const toggleMobileNav = () => {
        setIsMobileNavActive(!isMobileNavActive);
        document.body.classList.toggle('mobile-nav-active');
    };

    const closeMobileNav = () => {
        if (isMobileNavActive) {
            setIsMobileNavActive(false);
            document.body.classList.remove('mobile-nav-active');
        }
    };

    const handleLogout = () => {
        logoutUser();      // Calls the logout function from the user store
        closeMobileNav();  // Closes the mobile navigation if it's open
        navigate("/new/"); // Redirects the user to the home page after logging out
    };

    return <>
        <header id="header" className="header d-flex align-items-center sticky-top">
            <div className="container-fluid container-xl position-relative d-flex align-items-center">
                <Link to="/new" className="logo d-flex align-items-center me-auto">

                    <Image src="/favicon.ico" width="64" height="64" />
                    <h1 className="sitename">La Caverna Del Dragón</h1>
                </Link>
                <nav id="navmenu" className="navmenu">
                    <ul>
                        <ListGroupItem as="li"><Link to="/new/games" onClick={closeMobileNav}>Juegos</Link></ListGroupItem>
                        <ListGroupItem as="li"><Link to="/new/events" onClick={closeMobileNav}>Eventos</Link></ListGroupItem>
                        <ListGroupItem as="li"><Link to="/new/news" onClick={closeMobileNav}>Noticias</Link></ListGroupItem>
                        {user && (
                            <>
                                <ListGroupItem as="li"><Link className="d-xl-none" to="/new/profile" onClick={closeMobileNav}>Mi perfil</Link></ListGroupItem>
                                {user.userRoles?.includes("ADMIN") && (
                                    <ListGroupItem as="li"><Link className="d-xl-none" to="/new/admin" onClick={closeMobileNav}>Administración</Link></ListGroupItem>
                                )}
                                <ListGroupItem as="li">
                                    <Button
                                        className="btn btn-link d-xl-none nav-link"
                                        style={{ textAlign: 'left', border: 'none' }}
                                        onClick={handleLogout}
                                    >
                                        Cerrar sesión
                                    </Button>
                                </ListGroupItem>
                            </>
                        )}
                    </ul >
                    <i
                        className={`mobile-nav-toggle d-xl-none bi ${isMobileNavActive ? 'bi-x' : 'bi-list'}`}
                        onClick={toggleMobileNav}
                    ></i>
                </nav>
                {!user && (
                    <Link className="btn-getstarted d-none d-sm-block" to="/new/login">
                        Registro / Iniciar Sesión
                        <Person />
                    </Link>
                )}
                {user && (
                    <div className="d-none d-xl-block ms-4">
                        <Dropdown align="end">
                            <Dropdown.Toggle as="a" className="submit-btn rounded-circle p-0" id="dropdown-user" style={{ cursor: 'pointer' }}>
                                <Image
                                    src={`/api/v1/users/${user.userId}/image`}
                                    roundedCircle
                                    width="45"
                                    height="45"
                                    style={{ objectFit: 'cover', border: '2px solid var(--accent-color)' }}
                                    alt="User"
                                />
                            </Dropdown.Toggle>

                            <Dropdown.Menu>
                                <Dropdown.Item as={Link} to="/new/profile">
                                    <Person className="me-2" /> Mi perfil
                                </Dropdown.Item>

                                {user.userRoles?.includes("ADMIN") && (
                                    <Dropdown.Item as={Link} to="/new/admin">
                                        <ShieldLock className="me-2" /> Administración
                                    </Dropdown.Item>
                                )}

                                <Dropdown.Divider />
                                <Dropdown.Item onClick={handleLogout} className="text-danger">
                                    <BoxArrowRight className="me-2" /> Cerrar sesión
                                </Dropdown.Item>
                            </Dropdown.Menu>
                        </Dropdown>
                    </div>
                )}

            </div>
        </header>
    </>;
}