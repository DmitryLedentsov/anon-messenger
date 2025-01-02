package com.dimka228.messenger.controllers;

import com.dimka228.messenger.dto.TokenDTO;
import com.dimka228.messenger.dto.UserDto;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.exceptions.WrongPasswordException;
import com.dimka228.messenger.security.jwt.TokenProvider;
import com.dimka228.messenger.services.UserService;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class JwtAuthController {
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final TokenProvider jwtTokenUtil;

    @Autowired
    public JwtAuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            TokenProvider jwtTokenUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(
            @RequestBody @Valid UserDto userDto, BindingResult result) {
        result.failOnError((m) -> new WrongPasswordException("passwd must be 5 letters length"));
        User user = userDto.getUser();

        log.debug("POST request to register user {}", user.getUsername());
        userService.registerUser(user);
        return new ResponseEntity<>("User registered", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenDTO> signIn(@RequestBody UserDto userDto) {

        User user = userService.getUser(userDto.getLogin());
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getLogin(), userDto.getPassword()));
            final String token = jwtTokenUtil.generateToken(userDto.getUser());
            return new ResponseEntity<>(new TokenDTO(token, user.getId()), HttpStatus.OK);
        } catch (Exception e) {
            throw new WrongPasswordException();
        }
    }
}
