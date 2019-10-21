package com.accelerate.sdlc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SDLCSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Bean
	public AuthenticationProvider SDLCAuthenticationProvider() {
		return new SDLCAuthenticationProvider();
	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/resources/static/**","/sdlclogin","/assets/**","/","/**.css","/**.js","/**.ico","/**.jpg","/**.jpeg").permitAll().anyRequest().authenticated()
            .and().formLogin().loginPage("/").loginProcessingUrl("/sdlclogin").defaultSuccessUrl("/?success")
            .and().authenticationProvider(SDLCAuthenticationProvider()).csrf().disable();
    }
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(SDLCAuthenticationProvider());
	}
}
