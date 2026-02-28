package com.grupo6daw.lcdd_daw.security;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectToCurrentUrlStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        String targetUrl = request.getRequestURI();
        if (request.getQueryString() != null) {
            targetUrl += "?" + request.getQueryString();
        }

        response.sendRedirect(targetUrl);
    }
}