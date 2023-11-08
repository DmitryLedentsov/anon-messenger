package com.dimka228.messanger.controllers.error;

import com.dimka228.messanger.dto.ErrorDTO;
import com.dimka228.messanger.entities.User;
import com.dimka228.messanger.exceptions.AppException;
import com.dimka228.messanger.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@ControllerAdvice
@AllArgsConstructor

public class ErrorControllerAdvice {
    private final UserService userService;
    private final SimpMessagingTemplate msgTemplate;
    @MessageExceptionHandler
    public void handle(Principal principal, AppException e) {
        User user = userService.getUser(principal.getName());
        ErrorDTO errorMsg = new ErrorDTO(e);
        msgTemplate.convertAndSend("/topic/user/"+user.getId()+"/error", errorMsg);
    }
}
