package com.dimka228.messanger.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("say")
public class IndexController {
    @GetMapping(value = "hello")
    public String sayHello () {
        return
                "index" ;
    }
}