package com.accelerate.sdlc.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.accelerate.sdlc.model.*;

import lombok.Cleanup;

public class PersonnalTasksDao {

	private static final Logger logger = LoggerFactory.getLogger(PersonnalTasksDao.class);

	public List<PersonnalTasks> getPersonnalTask(String context, Authentication auth, DataSource datasource) {
		List<PersonnalTasks> pertasks = new ArrayList<PersonnalTasks>();
		try {
			logger.info("Getting task details");
			String taskContext;
			taskContext = (context == "A" ? "A" : "C");
			@Cleanup
			Connection conn = datasource.getConnection();
			@Cleanup
			Statement stmt = conn.createStatement();
			logger.info("Getting task details from database");
			@Cleanup
			ResultSet rs = stmt.executeQuery("SELECT AEL_EMP_CODE AS CODE FROM SDLC.ACC_EMP_LOGIN WHERE AEL_LOGIN_ID='"
					+ auth.getPrincipal().toString() + "'");
			int empId = rs.next() ? rs.getInt("CODE") : 0;
			rs = stmt.executeQuery(
					"SELECT APTC.APTC_TASK_PER_CODE AS TASKCODE,APTC.APTC_TASK_NAME AS TASKNAME,APTC.APTC_TASK_STATUS AS TASKSTATUS,"
							+ "APTE.APTE_TASK_SUMMARY AS TASKSUMMARY,APTE.APTE_TASK_DESCRIPTION AS TASKDESCRIPTION,APTE.APTE_REMARKS AS TASKREMARKS FROM ACC_PER_TASK_COM APTC"
							+ " INNER JOIN ACC_PER_TASK_EXP APTE ON (APTC.APTC_TASK_CODE=APTE.APTE_TASK_CODE AND APTC.APTC_TASK_PER_CODE=APTE.APTE_TASK_PER_CODE) "
							+ "WHERE APTC.APTC_ASSIGNED=" + empId + " AND APTC.APTC_TASK_CONTEXT='" + taskContext
							+ "'");
			logger.info("Processing details in database");
			while (rs.next()) {
				PersonnalTasks pertask = new PersonnalTasks();
				pertask.setTaskCode(rs.getInt("TASKCODE"));
				pertask.setTaskName(rs.getString("TASKNAME"));
				pertask.setTaskStatus(rs.getString("TASKSTATUS"));
				pertask.setTaskSummary(rs.getString("TASKSUMMARY"));
				pertask.setTaskDescription(rs.getString("TASKDESCRIPTION"));
				pertask.setTaskRemarks(rs.getString("TASKREMARKS"));
				pertasks.add(pertask);
			}
			return pertasks;
		} catch (Exception ex) {
			logger.info("Exception occured while getting the task list");
			ex.printStackTrace();
			return pertasks;
		}
	}

	public String createNewPerTask(PersonnalTasks newTask, Authentication auth, DataSource datasource) {
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = stmt.executeQuery("SELECT AEL_EMP_CODE FROM SDLC.ACC_EMP_LOGIN WHERE AEL_LOGIN_ID='"
					+ auth.getPrincipal().toString() + "'");
			int empId = rs.next() ? rs.getInt("AEL_EMP_CODE") : 0;
			rs = stmt
					.executeQuery("SELECT COALESCE(MAX(APTC_TASK_CODE)+1,1) AS TASKUNICODE FROM SDLC.ACC_PER_TASK_COM");
			int taskUniCode = rs.next() ? rs.getInt("TASKUNICODE") : 0;
			rs = stmt.executeQuery(
					"SELECT COALESCE(MAX(APTC_TASK_PER_CODE)+1,1) AS TASKCODE FROM SDLC.ACC_PER_TASK_COM WHERE APTC_ASSIGNED='"
							+ empId + "'");
			int taskCode = rs.next() ? rs.getInt("TASKCODE") : 0;
			logger.info("Inserting in common table");
			stmt.execute(
					"INSERT INTO ACC_PER_TASK_COM (APTC_TASK_CODE,APTC_TASK_PER_CODE,APTC_TASK_NAME,APTC_ASSIGNED,APTC_TASK_STATUS,APTC_TASK_DEPENDENCY,APTC_TASK_CONTEXT)"
							+ " VALUES (" + taskUniCode + "," + taskCode + ",'" + newTask.getTaskName() + "'," + empId
							+ ",'P',0,'C')");
			logger.info("Inserting in details table");
			stmt.execute(
					"INSERT INTO ACC_PER_TASK_HIS (APTH_TASK_CODE,APTH_TASK_PER_CODE,APTH_TASK_UPT_DATE,APTH_TASK_STATUS)"
							+ " VALUES (" + taskUniCode + "," + taskCode + ",'" + newTask.getTaskSummary() + "','"
							+ newTask.getTaskDescription() + "','" + newTask.getTaskRemarks() + "')");
			logger.info("Inserting in audit table");
			perTaskHistory(taskUniCode, taskCode, "P", conn, stmt);
			conn.commit();
			return "Task created successfully";
		} catch (Exception ex) {
			logger.info("Exception occured while creating new task");
			ex.printStackTrace();
			return "Task creation failed! Retry again";
		}
	}

	public void perTaskHistory(int taskUniCode, int taskCode, String status, Connection conn, Statement stmt)
			throws Exception {
		stmt.executeQuery(
				"INSERT INTO ACC_PER_TASK_HIS (APTH_TASK_CODE,APTH_TASK_PER_CODE,APTH_TASK_UPT_DATE,APTH_TASK_STATUS) "
						+ "VALUES (" + taskUniCode + "," + taskCode + ",NOW(),'" + status + "')");
	}
}
