package com.dimka228.messanger.controllers;

import com.dimka228.messanger.models.MessageInfo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/ajax")
public class AjaxController {
    @GetMapping("/messages")
    List<MessageInfo> all() {
        return null;
    }
}
