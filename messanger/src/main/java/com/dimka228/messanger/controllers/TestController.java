package com.dimka228.messanger.controllers;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messanger.dto.UserProfileDTO;
import com.dimka228.messanger.entities.UserAction;
import com.dimka228.messanger.entities.UserProfile;
import com.dimka228.messanger.utils.DateConverter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/test")
    public String test( ){
        //TODO: aa
        /*user = userService.getUser("aboba");
        id = 1;*/

        return "test";
    }
}
