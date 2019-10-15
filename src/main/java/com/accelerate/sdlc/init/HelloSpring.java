package com.accelerate.sdlc.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloSpring
{
	private static final Logger logger=LoggerFactory.getLogger(HelloSpring.class);
	
   @RequestMapping("/hello")
    public String getSampleText()
    {
	  logger.info("HI THis is info i guess xoxo");
	  logger.trace("This is trace xoxo");
	  logger.debug("THis is debug xoxo");
	  logger.error("THis is error xoxo");
	  logger.warn("THis is warning xoxo");
      return "Hello World! First Spring boot Rest Service";
    }
}
