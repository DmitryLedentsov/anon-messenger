package com.dimka228.messenger.controllers.error;

import com.dimka228.messenger.dto.ErrorDTO;
import com.dimka228.messenger.exceptions.AppException;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketErrorController {

    @MessageExceptionHandler
    public ErrorDTO handleException(AppException e) {
        return new ErrorDTO(e);
    }
}
