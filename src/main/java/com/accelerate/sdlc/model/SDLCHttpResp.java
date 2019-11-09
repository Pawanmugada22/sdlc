package com.accelerate.sdlc.model;

public class SDLCHttpResp {
	private String message;
	private String status;
	private Boolean boolstatus;
	private int perTaskCode;
	public int getPerTaskCode() {
		return perTaskCode;
	}
	public void setPerTaskCode(int perTaskCode) {
		this.perTaskCode = perTaskCode;
	}
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
