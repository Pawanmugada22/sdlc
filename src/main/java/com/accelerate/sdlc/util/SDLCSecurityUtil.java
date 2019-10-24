package com.accelerate.sdlc.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDLCSecurityUtil {

	private static final Logger logger = LoggerFactory.getLogger(SDLCSecurityUtil.class);

	public static String getHash(String text) {
		logger.info("Method is called for hashing");
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hashInBytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
			for (byte b : hashInBytes) {
				sb.append(String.format("%02x", b));
			}
			logger.info("No Exception occured while hashing");
		} catch (Exception ex) {
			logger.info("Exception occured while hashing the text");
			ex.printStackTrace();
		}
		String hashValue = sb.toString().toUpperCase();
		return hashValue;
	}
}
