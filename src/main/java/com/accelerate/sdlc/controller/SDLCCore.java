package com.accelerate.sdlc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accelerate.sdlc.model.LoginCredentials;

@RestController
public class SDLCCore {
	
	private static final Logger logger=LoggerFactory.getLogger(SDLCCore.class);

	   @RequestMapping("/helloauthentication")
	   @PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	   public LoginCredentials getAuthenticationDetails(Authentication auth) {
		   logger.info("Authentication API is called");
		   LoginCredentials login=new LoginCredentials();
		   login.setUsername(auth.getPrincipal().toString());
		   login.setRole(auth.getAuthorities().toString().substring(1, 3));
		   login.setIsauthorized(auth.isAuthenticated());
		   login.setPassword("CONFIDENTIAL");
		   return login;
	   }
}
