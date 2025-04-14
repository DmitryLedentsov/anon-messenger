package com.dimka228.messenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import com.dimka228.messenger.config.properties.WebSocketProperties;
import com.dimka228.messenger.exceptions.TokenExpiredException;
import com.dimka228.messenger.exceptions.WrongTokenException;
import com.dimka228.messenger.security.jwt.TokenProvider;
import com.dimka228.messenger.services.UserDetailsService;
import com.dimka228.messenger.utils.ErrorBuilder;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		registry.setErrorHandler(new StompSubProtocolErrorHandler() { //кастомный еррор хэндлер, который выставляет хидеры
			public @Nullable Message<byte[]> customHandleError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
				StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
				accessor.setMessage(ex.getMessage());
				ErrorBuilder.createExceptionMessage(ex).forEach((key,value)->{
					accessor.setNativeHeader(key, value.toString());
				});
				accessor.setLeaveMutable(true);
					
				StompHeaderAccessor clientHeaderAccessor = null;
				if (clientMessage != null) {
					clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
					if (clientHeaderAccessor != null) {
						String receiptId = clientHeaderAccessor.getReceipt();
						if (receiptId != null) {
							accessor.setReceiptId(receiptId);
						}
					}
				}
			
				return handleInternal(accessor, new byte[0], ex, clientHeaderAccessor);
			}
			@Override
			public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
				return customHandleError(clientMessage, ex.getCause());
			}
		});
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			@SuppressWarnings("UseSpecificCatch")
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
					catch (ExpiredJwtException e) {
						throw new TokenExpiredException();
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
