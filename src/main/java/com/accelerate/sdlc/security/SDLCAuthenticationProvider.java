package com.accelerate.sdlc.security;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.accelerate.sdlc.model.LoginCredentials;
import com.accelerate.sdlc.util.SDLCSecurityUtil;

import lombok.Cleanup;

public class SDLCAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	DataSource datasource;
	
	private static final Logger logger = LoggerFactory.getLogger(SDLCAuthenticationProvider.class);
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.info("Authentication provider is called");
		String name = authentication.getName();
		Object credentials = authentication.getCredentials();
		if (!(credentials instanceof String)) {
			throw new BadCredentialsException("Authentication failed for " + name);
		}
		String password = credentials.toString();
		@Cleanup LoginCredentials loginauth = new LoginCredentials();
		@Cleanup List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		try {
			if (this.isLoginAuthorized(name, password, loginauth).isIsauthorized()) {
				grantedAuthorities.add(new SimpleGrantedAuthority(loginauth.getRole()));
			} else {
				throw new BadCredentialsException("Authentication failed for " + name);
			}
		} catch (AuthenticationException ex) {
			logger.info("Exception occured in the given data username or password is incorrect");
			throw ex;
		} catch (Exception ex) {
			logger.info("Its a different exception");
			ex.printStackTrace();
		}
		Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuthorities);
		return auth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	private LoginCredentials isLoginAuthorized(String username, String password,LoginCredentials loginauth) throws Exception {
		logger.info("Authorisation method is called");
		loginauth.setIsauthorized(false);
		StringBuilder str=new StringBuilder();
		str.append("sdlc");
		str.append(username.toString());
		str.append(password.toUpperCase());
		String text=str.toString();
		String query = "SELECT AEL.AEL_LOGIN_ID AS USER,AEL.AEL_LOGIN_PASSWORD AS PASS,AEM.AEM_EMP_ROLE AS ROLE FROM SDLC.ACC_EMP_LOGIN AEL"
				+ " INNER JOIN SDLC.ACC_EMPLOYEE_MAST AEM ON (AEL.AEL_EMP_CODE=AEM.AEM_EMP_CODE)"
				+ " WHERE AEL.AEL_LOGIN_ID='" + username + "' AND AEL.AEL_LOGIN_PASSWORD='"
				+ SDLCSecurityUtil.getHash(text)+"'";
		try {
			@Cleanup
			Connection conn=datasource.getConnection();
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				loginauth.setUsername(rs.getString("USER"));
				loginauth.setPassword(rs.getString("PASS"));
				loginauth.setRole(rs.getString("ROLE"));
				loginauth.setIsauthorized(true);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception ex) {
			logger.info("Exception while validating password");
			ex.printStackTrace();
			throw ex;
		}
		return loginauth;
	}
	
	public boolean isCredentialsValid(String username, String password) throws Exception{
		@Cleanup
		LoginCredentials loginauth=new LoginCredentials();
		return this.isLoginAuthorized(username, password, loginauth).isIsauthorized();
	}

}
