package com.dimka228.messenger.controllers.error;

import com.dimka228.messenger.dto.ErrorDTO;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.exceptions.AppException;
import com.dimka228.messenger.services.UserService;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@AllArgsConstructor
public class ErrorControllerAdvice {
  private final UserService userService;
  private final SimpMessagingTemplate msgTemplate;

  @MessageExceptionHandler
  public void handle(Principal principal, AppException e) {
    User user = userService.getUser(principal.getName());
    ErrorDTO errorMsg = new ErrorDTO(e);
    msgTemplate.convertAndSend("/topic/user/" + user.getId() + "/error", errorMsg);
  }
}
