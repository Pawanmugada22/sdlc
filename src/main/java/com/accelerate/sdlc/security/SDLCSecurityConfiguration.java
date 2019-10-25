package com.accelerate.sdlc.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SDLCSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Bean
	public AuthenticationProvider SDLCAuthenticationProvider() {
		return new SDLCAuthenticationProvider();
	}
	
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.addAllowedHeader("Access-Control-Allow-Origin");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
        	.authorizeRequests().antMatchers("/resources/static/**","/loginpage","/sdlclogin","/assets/**","/","/**.css","/**.js","/**.ico","/**.jpg","/**.jpeg").permitAll().anyRequest().authenticated()
            .and().formLogin().loginPage("/").loginProcessingUrl("/sdlclogin").defaultSuccessUrl("/?success")
            .and().authenticationProvider(SDLCAuthenticationProvider()).csrf().disable();
    }
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(SDLCAuthenticationProvider());
	}
}
