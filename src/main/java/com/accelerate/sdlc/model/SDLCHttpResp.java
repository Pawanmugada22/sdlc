package com.accelerate.sdlc.model;

public class SDLCHttpResp {
	private String message;
	private String status;
	private Boolean boolstatus;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Boolean getBoolstatus() {
		return boolstatus;
	}
	public void setBoolstatus(Boolean boolstatus) {
		this.boolstatus = boolstatus;
	}
}
