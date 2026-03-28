package com.grupo6daw.lcdd_daw.controller;

import com.grupo6daw.lcdd_daw.dto.ErrorDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import java.util.NoSuchElementException;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice(annotations = RestController.class)
@RestController
public class CustomErrorRestController implements ErrorController {

    private static final int UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();
    private static final int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    private static final int FORBIDDEN = HttpStatus.FORBIDDEN.value();
    private static final int NOT_FOUND = HttpStatus.NOT_FOUND.value();
    private static final int INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();

    private String getErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 401 -> "Debes iniciar sesion para acceder a este recurso.";
            case 404 -> "La página solicitada no existe o ha sido movida.";
            case 403 -> "No tienes el permiso necesario para ver este contenido.";
            case 500 -> "Error interno del servidor. Lo estamos revisando.";
            case 400 -> "La solicitud enviada no es válida.";
            default -> "Ha ocurrido un error inesperado.";
        };
    }

    private ResponseEntity<ErrorDTO> buildErrorResponse(int statusCode) {
        ErrorDTO errorDTO = new ErrorDTO(statusCode, getErrorMessage(statusCode));
        return ResponseEntity.status(statusCode).body(errorDTO);
    }

    @ExceptionHandler({ IllegalArgumentException.class, ServletException.class })
    public ResponseEntity<ErrorDTO> handleBadRequest() {
        return buildErrorResponse(BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleUnauthorized() {
        return buildErrorResponse(UNAUTHORIZED);
    }

    @ExceptionHandler({ NoHandlerFoundException.class, NoSuchElementException.class })
    public ResponseEntity<ErrorDTO> handleNotFound() {
        return buildErrorResponse(NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleForbidden() {
        return buildErrorResponse(FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGeneralError() {
        return buildErrorResponse(INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/error", produces = "application/json")
    public ResponseEntity<ErrorDTO> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = INTERNAL_SERVER_ERROR;

        if (status != null) {
            try {
                statusCode = Integer.parseInt(status.toString());
            } catch (NumberFormatException e) {
                statusCode = INTERNAL_SERVER_ERROR;
            }
        }

        return buildErrorResponse(statusCode);
    }
}
