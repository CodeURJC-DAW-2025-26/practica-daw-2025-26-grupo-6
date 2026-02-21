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
						.requestMatchers("/css/**", "/js/**", "/img/**", "/vendor/**").permitAll()
						.requestMatchers("/sample_images/**").permitAll()
						.requestMatchers("/index").permitAll()
						.requestMatchers("/events").permitAll()
						.requestMatchers("/detail_event_page").permitAll()
						.requestMatchers("/news").permitAll()
						.requestMatchers("/detail_new_page").permitAll()
						.requestMatchers("/games").permitAll()
						.requestMatchers("/detail_game_page").permitAll()
						.requestMatchers("/login").permitAll()
						.requestMatchers("/register").permitAll()
						// PRIVATE PAGES
						.requestMatchers("/profile").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/event_form").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/new_form").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/logout").hasAnyRole("REGISTERED_USER")
						.requestMatchers("/game_form").hasAnyRole("ADMIN")
						.requestMatchers("/admin").hasAnyRole("ADMIN"))
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.usernameParameter("email")
						.defaultSuccessUrl("/index")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}
}
