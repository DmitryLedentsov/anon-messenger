package com.dimka228.messenger.controllers.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ServerErrorController implements ErrorController {
   
   // override this error path to custom error path

   public String getErrorPath() {
    return "/error";
   }

   @GetMapping("/error")
   public String customHandling(HttpServletRequest request){
       // you can use request to get different error codes
       String error = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString();

       return error;
       // you can return different `view` based on error codes.
       // return 'error-404' or 'error-500' based on errors
   }
}