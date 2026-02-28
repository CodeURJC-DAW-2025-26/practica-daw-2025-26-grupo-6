package com.grupo6daw.lcdd_daw.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	RepositoryUserDetailsService userDetailsService;

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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES
						.requestMatchers("/").permitAll()
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
						// PRIVATE PAGES
						.requestMatchers("/profile").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/event_form").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/event_form/*").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/new_form").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/new_form/*").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/removeNew/*").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/removeEvent/*").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/removeGame/*").hasAnyRole("ADMIN")
						.requestMatchers("/game_form/*").hasAnyRole("ADMIN")
						.requestMatchers("/game_form").hasAnyRole("ADMIN")
						.requestMatchers("/admin/**").hasAnyRole("ADMIN"))
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.usernameParameter("email")
						.defaultSuccessUrl("/")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}
}
