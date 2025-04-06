package org.freecode.demo.jobportal.config;

import org.freecode.demo.jobportal.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
	
	private final CustomUserDetailsService usrDetailService;
	private final CustomAuthenticationSuccessHandler customAuthSuccessHandler;
	
	@Autowired
	public WebSecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler customAuthSuccessHandler) {
		this.usrDetailService = customUserDetailsService;
		this.customAuthSuccessHandler = customAuthSuccessHandler;
	}
	
	private final String[] publicUrls = { "/",
		"/global-search/**",
		"/register",
		"/register/**",
		"/webjars/**",
		"/resources/**",
		"/assets/**",
		"/css/**",
		"/summernote/**",
		"/js/**",
		"/*.css",
		"/*.js",
		"/*.js.map",
		"/fonts**",
		"/favicon.ico",
		"/error"};
	
	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
		httpSec.authenticationProvider(authenticationProvider());
		httpSec.authorizeHttpRequests(auth -> {
			auth.requestMatchers(publicUrls).permitAll();
			auth.anyRequest().authenticated();
		});
		
		httpSec.formLogin(form->form.loginPage("/login").permitAll()
				.successHandler(customAuthSuccessHandler))
		.logout(logout->{
			logout.logoutUrl("/logout");
			logout.logoutSuccessUrl("/");
		}).cors(Customizer.withDefaults())
		.csrf(csrf->csrf.disable());
		return httpSec.build();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		authenticationProvider.setUserDetailsService(usrDetailService);
		
		return authenticationProvider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
