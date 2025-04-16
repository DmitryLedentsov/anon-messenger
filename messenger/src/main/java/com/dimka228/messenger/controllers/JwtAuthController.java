package com.dimka228.messenger.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.TokenDTO;
import com.dimka228.messenger.dto.UserAuthDTO;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.exceptions.WrongPasswordException;
import com.dimka228.messenger.security.jwt.TokenProvider;
import com.dimka228.messenger.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("auth")
public class JwtAuthController {

	private final UserService userService;

	private final AuthenticationManager authenticationManager;

	private final TokenProvider jwtTokenUtil;

	public JwtAuthController(UserService userService, AuthenticationManager authenticationManager,
			TokenProvider jwtTokenUtil) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@PostMapping("/signup")
	public void signUp(@RequestBody @Valid UserAuthDTO userDto, BindingResult result) {
		result.failOnError((m) -> new WrongPasswordException("passwd must be 5 letters length"));
		User user = User.fromAuth(userDto);

		log.debug("POST request to register user {}", user.getUsername());
		userService.registerUser(user);
	}

	@PostMapping("/signin")
	@SuppressWarnings("UseSpecificCatch")
	public TokenDTO signIn(@RequestBody UserAuthDTO userDto) {

		User user = userService.getUser(userDto.getLogin());
		try {

			authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userDto.getLogin(), userDto.getPassword()));
			final String token = jwtTokenUtil.generateToken(user);
			return new TokenDTO(token, user.getId());
		}
		catch (Exception e) {
			log.info(e.getMessage());
			throw new WrongPasswordException();
		}
	}

}
