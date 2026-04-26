import {
    Envelope,
    TwitterX,
    Instagram,
    Twitch,
    Youtube
} from 'react-bootstrap-icons';

export default function Footer() {
    return <footer id="footer" className="footer dark-background">
        <div className="social-container">
            <div className="col-1 text-center">
                <a href="mailto:lcddoficial@gmail.com" className="social-link" title="Email">
                    <Envelope />
                </a>
            </div>

            <div className="col-1 text-center">
                <a href="https://x.com/lcdd_urjc" target="_blank" className="social-link" title="Twitter/X">
                    <TwitterX />
                </a>
            </div>

            <div className="col-1 text-center">
                <a href="https://www.instagram.com/lcdd_urjc" target="_blank" className="social-link" title="Instagram">
                    <Instagram />
                </a>
            </div>

            <div className="col-1 text-center">
                <a href="https://www.twitch.tv/lcdd_oficial" target="_blank" className="social-link" title="Twitch">
                    <Twitch />
                </a>
            </div>

            <div className="col-1 text-center">
                <a href="https://youtube.com/@lcddurjc" target="_blank" className="social-link" title="YouTube">
                    <Youtube />
                </a>
            </div>

        </div>
        <div className="footer-bottom">
            <div className="container">
                <div className="row">
                    <div className="col-12">
                        <div className="footer-bottom-content">
                            <p className="mb-0"><span className="sitename">La Caverna Del Dragón</span></p>
                            <div className="credits">
                                Desarrollo de Aplicaciones Web - Grupo 6 - 2026
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </footer>;
}