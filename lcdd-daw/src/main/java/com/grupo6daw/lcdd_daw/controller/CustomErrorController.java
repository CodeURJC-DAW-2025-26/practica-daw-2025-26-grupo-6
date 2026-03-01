package com.grupo6daw.lcdd_daw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.webmvc.error.ErrorController;

@Controller
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request, Model model) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    String errorMsg = "Ha ocurrido un error.";
    String errorCode = "Error Desconocido";

    if (status != null) {
      Integer statusCode = Integer.valueOf(status.toString());
      errorCode = statusCode.toString();

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
    }

    model.addAttribute("error-code", errorCode);
    model.addAttribute("error-text", errorMsg);

    return "error";
  }
}