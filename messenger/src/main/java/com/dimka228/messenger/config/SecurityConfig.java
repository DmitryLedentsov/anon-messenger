package com.dimka228.messenger.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.dimka228.messenger.security.jwt.JwtAuthenticationFilter;
import com.dimka228.messenger.services.UserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserDetailsService userDetailsService;

    @Value("${messenger.websocket.path}")
    private final String websocketPath = "/ws";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                // TODO potato: включить CORS, он не так просто существует
                .cors(
                        cors ->
                                cors.configurationSource(
                                        request -> {
                                            var corsConfiguration = new CorsConfiguration();
                                            corsConfiguration.setAllowedOriginPatterns(
                                                    List.of("*"));
                                            corsConfiguration.setAllowedMethods(
                                                    List.of(
                                                            "GET", "POST", "PUT", "DELETE",
                                                            "OPTIONS"));
                                            corsConfiguration.setAllowedHeaders(List.of("*"));
                                            corsConfiguration.setAllowCredentials(true);
                                            return corsConfiguration;
                                        }))
                .authorizeHttpRequests(
                        request ->
                                request.requestMatchers("/auth/**")
                                        .permitAll()
                                        .requestMatchers(
                                                "/",
                                                "/test",
                                                "/app",
                                                "/welcome",
                                                websocketPath + "/**")
                                        .permitAll()
                                        .requestMatchers(
                                                "/js/**",
                                                "/css/**",
                                                "/icons/**",
                                                "/fonts/**",
                                                "favicon.ico")
                                        .permitAll()
                                        .requestMatchers(
                                                "/swagger-ui/**",
                                                "/swagger-resources/*",
                                                "/v3/api-docs/**","/api-docs-ui.html","/api-docs.yaml")
                                        .permitAll()
                                        .requestMatchers("/api-docs/**", "/api-docs-ui/*")
                                        .permitAll()
                                        .requestMatchers("/endpoint", "/admin/**")
                                        .hasRole("ADMIN")
                                        .anyRequest()
                                        .authenticated())
                .sessionManagement(
                        manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
