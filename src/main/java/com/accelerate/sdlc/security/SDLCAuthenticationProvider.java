package com.accelerate.sdlc.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public class SDLCAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final Logger logger=LoggerFactory.getLogger(SDLCAuthenticationProvider.class);
	
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.info("testing its working !!");
		String name = authentication.getName();
		Object credentials = authentication.getCredentials();
		logger.info("credentials class: "+credentials.getClass());
        if (!(credentials instanceof String)) {
        	throw new BadCredentialsException("Authentication failed for " + name);
        }
        String password = credentials.toString();
//        String role= this.getRole(name, password);
//        if (role==null) {
//            throw new BadCredentialsException("Authentication failed for " + name);
//        }
        grantedAuthorities.add(new SimpleGrantedAuthority("SU"));
        Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuthorities);
        return auth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
//	public String getRole(String name,String password) {
//		String sql="";
//	}
	
}
