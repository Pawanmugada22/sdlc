package com.accelerate.sdlc.controller;


import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloSpring {
	
	private static final Logger logger=LoggerFactory.getLogger(HelloSpring.class);
	
   @RequestMapping("/hello")
    public String getSampleText(HttpServletRequest request)
    {
	  HttpSession session=request.getSession();
	  StringBuilder details=new StringBuilder("Details ");
	  details.append("ID : "+session.getId()+" ");
	  details.append(" Complete : "+session.toString());
	  logger.info("HI THis is info i guess xoxo");
	  logger.trace("This is trace xoxo");
	  logger.debug("THis is debug xoxo");
	  logger.error("THis is error xoxo");
	  logger.warn("THis is warning xoxo");
	  logger.info(details.toString());
      return "Hello World! First Spring boot Rest Service";
    }
   
   @RequestMapping("/takeout")
   public String invalidateSession(HttpServletRequest request)
   {
	   HttpSession session=request.getSession();
	   logger.info("Request "+request.toString());
	   logger.info("Session "+session.toString());
	   logger.info("Session details : "+session.getAttributeNames().toString());
	   session.invalidate();
	   return "Go to Home";
   }
   
   @RequestMapping("/testing")
   @PreAuthorize("hasAnyAuthority('SU')")
   public String getUserDataTest1(Authentication auth) {
	   logger.info("Authorities "+auth.getAuthorities().toString());
	   return "Is is working now ?";
   }
   
   @RequestMapping("/nottesting")
   @PreAuthorize("hasAnyAuthority('DEV')")
   public String getUserDataTest2(Principal principal) {
	   logger.info(principal.getName());
	   logger.info(principal.toString());
	   return "Is is working now ?";
   }
}
