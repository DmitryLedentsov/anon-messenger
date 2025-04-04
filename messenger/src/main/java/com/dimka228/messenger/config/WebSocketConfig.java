package com.dimka228.messenger.config;

import com.dimka228.messenger.config.properties.WebSocketProperties;
import com.dimka228.messenger.exceptions.AppException;
import com.dimka228.messenger.exceptions.WrongTokenException;
import com.dimka228.messenger.security.jwt.TokenProvider;
import com.dimka228.messenger.services.UserDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final TokenProvider jwtTokenUtil;

	private final UserDetailsService userDetailsService;

	private final WebSocketProperties properties;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(properties.getPath()).setAllowedOrigins("*");
		registry.setErrorHandler(new StompSubProtocolErrorHandler() {
			@Override
			public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
				return super.handleClientMessageProcessingError(clientMessage, ex.getCause());
			}

			private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, AppException apiError,
					String errorCode) {
				String message = apiError.getMessage();

				StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

				accessor.setMessage(errorCode);
				accessor.setLeaveMutable(true);

				return MessageBuilder.createMessage(message != null ? message.getBytes() : "".getBytes(),
						accessor.getMessageHeaders());
			}
		});
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

				assert accessor != null;
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {

					String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

					if (authorizationHeader == null)
						throw new WrongTokenException("empty token");
					log.debug("TOKEN: " + authorizationHeader);
					String token = authorizationHeader.substring(7);

					try {
						String username = jwtTokenUtil.extractUserName(token);
						UserDetails userDetails = userDetailsService.loadUserByUsername(username);
						UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

						accessor.setUser(usernamePasswordAuthenticationToken);
					}
					catch (Exception e) {
						throw new WrongTokenException();
					}
				}

				return message;
			}
		});
	}

}
