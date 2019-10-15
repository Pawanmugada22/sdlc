package com.accelerate.sdlc.init;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class SDLCSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Bean
	public AuthenticationProvider SDLCAuthenticationProvider() {
		return new SDLCAuthenticationProvider();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(this.SDLCAuthenticationProvider());
	}
}
