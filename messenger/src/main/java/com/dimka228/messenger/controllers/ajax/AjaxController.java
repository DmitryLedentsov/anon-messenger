package com.dimka228.messenger.controllers.ajax;

import com.dimka228.messenger.models.MessageInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/ajax")
public class AjaxController {
  @GetMapping("/messages")
  List<MessageInfo> all() {
    return null;
  }
}
