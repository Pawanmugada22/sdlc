package com.accelerate.sdlc.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.NullRequestCache;


@Configuration
public class SDLCSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
    @Autowired
    DataSource datasource;

    @Autowired
    SDLCAuthenticationProvider prov;
    
//    @Bean
//    public HttpSessionStrategy httpSessionStrategy() {
//        return new HeaderHttpSessionStrategy();
//    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(prov);
    }

    @Override
	protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
        .and().requestCache().requestCache(new NullRequestCache())
        .and().httpBasic();
    }
}
