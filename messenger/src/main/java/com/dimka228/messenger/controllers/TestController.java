package com.dimka228.messenger.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class TestController {
  @GetMapping("/test")
  public String test() {
    // TODO: aa
    /*user = userService.getUser("aboba");
    id = 1;*/
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block

    }

    return "test";
  }
}
