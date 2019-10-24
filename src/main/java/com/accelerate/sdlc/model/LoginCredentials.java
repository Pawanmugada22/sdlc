package com.accelerate.sdlc.model;

public class LoginCredentials {
	
	private String username;
	private String password;
	private String role;
	private boolean isauthorized;
	
	
	public boolean isIsauthorized() {
		return isauthorized;
	}
	public void setIsauthorized(boolean isauthorized) {
		this.isauthorized = isauthorized;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

}
