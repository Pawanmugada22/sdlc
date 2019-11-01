package com.accelerate.sdlc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
public class DevTools {
	
	private static final Logger logger=LoggerFactory.getLogger(DevTools.class);
	
	
	@RequestMapping("/sessions")
	public String getSessionDetails() {
		
		logger.info("Invalidating all the sessions now !!!");
		
		return "Testing";
	}
}
