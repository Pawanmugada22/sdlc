package com.accelerate.sdlc.security;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.Cleanup;

@Component
public class SDLCInitResetSession {

	@Autowired
	DataSource datasource;

	@Value("${sdlc.security.clearSessionOnRestart}")
	boolean session;
	
	private static final Logger logger = LoggerFactory.getLogger(SDLCInitResetSession.class);

	@EventListener(ApplicationReadyEvent.class)
	public void resetSession() {
		if (session) {
			logger.info("Started to clear residual sessions");
			try {
				@Cleanup
				Connection conn = datasource.getConnection();
				@Cleanup
				Statement stmt = conn.createStatement();
				conn.setAutoCommit(false);
				stmt.execute("TRUNCATE TABLE SDLC.SPRING_SESSION CASCADE");
				conn.commit();
				logger.info("Previous sessions are cleared successfully ");
			} catch (Exception ex) {
				logger.info("Exception occured while clearing old sessionds");
				ex.printStackTrace();
			}
		} else {
			logger.info("Previous sessions are not cleared");
		}
	}
}
