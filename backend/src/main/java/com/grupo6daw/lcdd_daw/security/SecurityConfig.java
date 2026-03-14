package com.grupo6daw.lcdd_daw.security;

import java.beans.BeanProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.grupo6daw.lcdd_daw.security.jwt.JwtTokenProvider;
import com.grupo6daw.lcdd_daw.security.jwt.JwtRequestFilter;
import com.grupo6daw.lcdd_daw.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
                .exceptionHandling(
                        handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        // Games
                        .requestMatchers(HttpMethod.POST, "/api/v1/games/*/favourites")
                        .hasRole("REGISTERED_USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/games/*/favourites")
                        .hasRole("REGISTERED_USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/games").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/games/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/games/*").hasRole("ADMIN")
                        // News
                        .requestMatchers(HttpMethod.POST, "/api/v1/news")
                        .hasRole("REGISTERED_USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/news/*")
                        .hasRole("REGISTERED_USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/news/*")
                        .hasRole("REGISTERED_USER")
                        // Events
                        .requestMatchers(HttpMethod.POST, "/api/v1/events")
                        .hasRole("REGISTERED_USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/events/*")
                        .hasRole("REGISTERED_USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/events/*")
                        .hasRole("REGISTERED_USER")
                        // PUBLIC ENDPOINTS
                        .anyRequest().permitAll());

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
        http.addFilterBefore(new JwtRequestFilter(userDetailsService, jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PUBLIC PAGES
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .requestMatchers("/sample_images/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/index").permitAll()
                        .requestMatchers("/events").permitAll()
                        .requestMatchers("/event/*").permitAll()
                        .requestMatchers("/news").permitAll()
                        .requestMatchers("/new/*").permitAll()
                        .requestMatchers("/games").permitAll()
                        .requestMatchers("/game/*").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/userExists").permitAll()
                        .requestMatchers("/user/*").permitAll()
                        .requestMatchers("/user/*/image").permitAll()
                        .requestMatchers("/userDeleted").permitAll()
                        // PRIVATE PAGES
                        .requestMatchers("/game/*/toggle-fav").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/profile").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/event_form").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/event_form/*").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/new_form").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/new_form/*").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/removeNew/*").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/removeEvent/*").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/user/*/update").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/user/*/delete").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/removeEvent/*").hasAnyRole("REGISTERED_USER")
                        .requestMatchers("/removeGame/*").hasAnyRole("ADMIN")
                        .requestMatchers("/game_form/*").hasAnyRole("ADMIN")
                        .requestMatchers("/game_form").hasAnyRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())
                .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> res.sendError(403))
                        .accessDeniedHandler((req, res, ex) -> res.sendError(403)));

        // SessionRegistry configuration
        http
                .sessionManagement(session -> session
                        .maximumSessions(-1) // No limit on sessions
                        .sessionRegistry(sessionRegistry())
                        .expiredSessionStrategy(new RedirectToCurrentUrlStrategy()));

        return http.build();
    }
}
