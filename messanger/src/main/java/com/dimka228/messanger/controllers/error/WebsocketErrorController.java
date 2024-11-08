package com.dimka228.messanger.controllers.error;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.stereotype.Controller;

import com.dimka228.messanger.dto.ErrorDTO;
import com.dimka228.messanger.exceptions.AppException;

@Controller
public class WebsocketErrorController {

    // ...

    @MessageExceptionHandler
    public ErrorDTO handleException(AppException e) {
        // ...
        return new ErrorDTO(e.getMessage());
    }
}