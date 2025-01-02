package com.dimka228.messenger.controllers.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServerErrorController implements ErrorController {

    public String getErrorPath() {
        return "/error";
    }

    @GetMapping("/error")
    public String customHandling(HttpServletRequest request) {
        String error = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString();

        return error;
    }
}
