package com.accelerate.sdlc.model;

public class PerTaskStatus {
	private int taskCode;
	private String taskStatus;
	private String taskContext;
	private String taskChange;
	public int getTaskCode() {
		return taskCode;
	}
	public void setTaskCode(int taskCode) {
		this.taskCode = taskCode;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	public String getTaskContext() {
		return taskContext;
	}
	public void setTaskContext(String taskContext) {
		this.taskContext = taskContext;
	}
	public String getTaskChange() {
		return taskChange;
	}
	public void setTaskChange(String taskChange) {
		this.taskChange = taskChange;
	}
}
