package com.accelerate.sdlc.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SdlcApplication {

	private static final Logger logger = LoggerFactory.getLogger(SdlcApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SdlcApplication.class, args);
		logger.info("Logger Started for SDLC Application");
		logger.info("xoxo");
	}

}
