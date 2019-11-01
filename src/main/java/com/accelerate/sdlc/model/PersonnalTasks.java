package com.accelerate.sdlc.model;

public class PersonnalTasks {

	private int taskCode;
	private String taskStatus;
	private String taskContext;
	private String taskName;
	private String taskSummary;
	private String taskDescription;
	private String taskRemarks;
	
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
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskSummary() {
		return taskSummary;
	}
	public void setTaskSummary(String taskSummary) {
		this.taskSummary = taskSummary;
	}
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	public String getTaskRemarks() {
		return taskRemarks;
	}
	public void setTaskRemarks(String taskRemarks) {
		this.taskRemarks = taskRemarks;
	}
}
