package com.dimka228.messanger.config;


import com.dimka228.messanger.services.UserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private  final  BCryptPasswordEncoder passwordEncoder;
    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/auth/**","/error").permitAll()
                .anyRequest().authenticated()
        ).formLogin(form -> form
                        .loginPage("/auth/login")
                        .permitAll()
                .defaultSuccessUrl("/index")
        ).logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)        // set invalidation state when logout
                .deleteCookies("JSESSIONID"));

        return http.build();
        // ...
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*http.formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/index",true)
                .failureUrl("/auth/login?error=true");
        return http.build();*/

        /*http.authorizeRequests()
                .requestMatchers("/css/**", "/js/**", "/auth/**").permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/auth/login")
                .permitAll();

        http.formLogin()
                .defaultSuccessUrl("/index", true);
        return http.build();*/

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/js/**","css/**","resources/**","static/**","/img/**", "/icon/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/users").hasRole("ADMIN")
                                .anyRequest().authenticated()
                ).formLogin(
                        form -> form
                                .usernameParameter("login")
                                .passwordParameter("password")
                                .loginPage("/auth/login")
                                .loginProcessingUrl("/auth/login")
                                .defaultSuccessUrl("/index")
                                .failureUrl("/auth/login?error=true")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .invalidateHttpSession(true)        // set invalidation state when logout
                                .logoutSuccessUrl("/auth/login")
                                .deleteCookies("JSESSIONID")
                                .permitAll()
                );
        return http.build();
    }

    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
    /*@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/js/**","css/**","resources/**","static/**","/img/**", "/icon/**");
    }*/

}
