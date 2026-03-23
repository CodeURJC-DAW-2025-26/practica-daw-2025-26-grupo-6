package com.grupo6daw.lcdd_daw.controller;

import com.grupo6daw.lcdd_daw.dto.ErrorDTO;
import com.grupo6daw.lcdd_daw.dto.ErrorMapper;
import com.grupo6daw.lcdd_daw.model.Error;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestControllerAdvice
public class CustomErrorRestController {

    private final ErrorMapper errorMapper;

    public CustomErrorRestController(ErrorMapper errorMapper) {
        this.errorMapper = errorMapper;
    }

    private ResponseEntity<ErrorDTO> buildErrorResponse(int statusCode, String message) {
        Error error = new Error(message, statusCode);
        ErrorDTO errorDTO = errorMapper.toDTO(error);
        return ResponseEntity.status(HttpStatus.valueOf(statusCode)).body(errorDTO);
    }

    /*
     * @ExceptionHandler(Exception.class)
     * 
     * @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
     * public ResponseEntity<ErrorDTO> handleGeneralError(HttpServletRequest
     * request, Exception ex) {
     * return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
     * "Error interno del servidor. Lo estamos revisando.");
     * }
     */
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDTO> handleNotFoundError(HttpServletRequest request, Exception ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND.value(), "La página solicitada no existe o ha sido movida.");
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorDTO> handleForbiddenError(HttpServletRequest request, Exception ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN.value(), "No tienes el rol necesario para ver este contenido.");
    }

    /*
     * @ExceptionHandler(ServletException.class)
     * 
     * @ResponseStatus(HttpStatus.BAD_REQUEST)
     * public ResponseEntity<ErrorDTO> handleBadRequestError(HttpServletRequest
     * request, Exception ex) {
     * return buildErrorResponse(HttpStatus.BAD_REQUEST.value(),
     * "La solicitud enviada no es válida.");
     * }
     */
    @RequestMapping(value = "/error", produces = "application/json")
    public ResponseEntity<ErrorDTO> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String errorMsg = "Ha ocurrido un error.";

        if (status != null) {
            try {
                statusCode = Integer.parseInt(status.toString());
                switch (statusCode) {
                    case 404:
                        errorMsg = "La página solicitada no existe o ha sido movida.";
                        break;
                    case 403:
                        errorMsg = "No tienes el rol necesario para ver este contenido.";
                        break;
                    case 500:
                        errorMsg = "Error interno del servidor. Lo estamos revisando.";
                        break;
                    case 400:
                        errorMsg = "La solicitud enviada no es válida.";
                        break;
                    default:
                        errorMsg = "Ha ocurrido un error inesperado.";
                        break;
                }
            } catch (NumberFormatException e) {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
                errorMsg = "Ha ocurrido un error inesperado.";
            }
        }

        return buildErrorResponse(statusCode, errorMsg);
    }
}
