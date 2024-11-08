package com.dimka228.messanger.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messanger.dto.UserDto;
import com.dimka228.messanger.entities.*;
import com.dimka228.messanger.exceptions.AppException;
import com.dimka228.messanger.exceptions.UserNotFoundException;
import com.dimka228.messanger.exceptions.WronPasswordException;
import com.dimka228.messanger.security.jwt.TokenProvider;
import com.dimka228.messanger.services.UserService;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/auth")
public class JwtAuthController {
    private final UserService userService;
    
    private final AuthenticationManager authenticationManager;
    private final TokenProvider jwtTokenUtil;

    @Autowired
    public JwtAuthController(UserService userService, AuthenticationManager authenticationManager, TokenProvider jwtTokenUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserDto userDto) {
        User user = userDto.getUser();
      
    
        log.debug("POST request to register user {}", user.getUsername());
        userService.registerUser(user);
        return new ResponseEntity<>("User registered", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody UserDto userDto) {

        if(!userService.checkUser(userDto.getLogin())) throw new UserNotFoundException(userDto.getLogin());
        try{
           
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        userDto.getLogin(),
                        userDto.getPassword()
                    )
            );
            //SecurityContextHolder.getContext().setAuthentication(authentication);
            final String token = jwtTokenUtil.generateToken(userDto.getUser());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e){
            throw new WronPasswordException();
        }
    }
}