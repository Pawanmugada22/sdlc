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

import com.accelerate.sdlc.model.PerTaskStatus;
import com.accelerate.sdlc.model.PersonnalTasks;
import com.accelerate.sdlc.model.SDLCHttpResp;
import com.accelerate.sdlc.util.SDLCRestUtil;

import lombok.Cleanup;

public class PersonnalTasksDao {

	private static final Logger logger = LoggerFactory.getLogger(PersonnalTasksDao.class);

	public List<PersonnalTasks> getPersonnalTask(String context, Authentication auth, DataSource datasource) {
		List<PersonnalTasks> pertasks = new ArrayList<PersonnalTasks>();
		try {
			logger.info("Getting task details");
			String taskContext;
			logger.info(context+".");
			taskContext = (context.toString() == "C" ? "C" : "N");
			@Cleanup
			Connection conn = datasource.getConnection();
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = null;
			logger.info("Getting task details from database");
			int empId=SDLCRestUtil.getEmployeeId(stmt,conn,auth.getPrincipal().toString());
			logger.info(taskContext+" "+empId);
			rs = stmt.executeQuery(
					"SELECT APTC.APTC_TASK_PER_CODE AS TASKCODE,APTC.APTC_TASK_NAME AS TASKNAME,APTC.APTC_TASK_STATUS AS TASKSTATUS,APTC.APTC_TASK_CONTEXT AS TASKCONTEXT,"
							+ "APTE.APTE_TASK_SUMMARY AS TASKSUMMARY,APTE.APTE_TASK_DESCRIPTION AS TASKDESCRIPTION,APTE.APTE_REMARKS AS TASKREMARKS FROM SDLC.ACC_PER_TASK_COM APTC"
							+ " INNER JOIN SDLC.ACC_PER_TASK_EXP APTE ON (APTC.APTC_ASSIGNED=APTE.APTE_ASSIGNED_CODE AND APTC.APTC_TASK_PER_CODE=APTE.APTE_TASK_PER_CODE) "
							+ "WHERE APTC.APTC_ASSIGNED=" + empId + " AND APTC.APTC_TASK_CONTEXT='" + context.toString()
							+ "'");
			logger.info("Processing details in database");
			while (rs.next()) {
				PersonnalTasks pertask = new PersonnalTasks();
				pertask.setTaskCode(rs.getInt("TASKCODE"));
				pertask.setTaskName(rs.getString("TASKNAME"));
				pertask.setTaskContext(rs.getString("TASKCONTEXT"));
				pertask.setTaskStatus(rs.getString("TASKSTATUS"));
				pertask.setTaskSummary(rs.getString("TASKSUMMARY"));
				pertask.setTaskDescription(rs.getString("TASKDESCRIPTION"));
				pertask.setTaskRemarks(rs.getString("TASKREMARKS"));
				pertasks.add(pertask);
			}
			rs.close();
			stmt.close();
			conn.close();
			return pertasks;
		} catch (Exception ex) {
			logger.info("Exception occured while getting the task list");
			ex.printStackTrace();
			return pertasks;
		}
	}

	public SDLCHttpResp createNewPerTask(PersonnalTasks newTask, Authentication auth, DataSource datasource) {
		SDLCHttpResp httpresp=new SDLCHttpResp();
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs=null;
			logger.info("Getting task details from database");
			int empId=SDLCRestUtil.getEmployeeId(stmt,conn,auth.getPrincipal().toString());
			rs = stmt.executeQuery(
					"SELECT COALESCE(MAX(APTC_TASK_PER_CODE)+1,1) AS TASKCODE FROM SDLC.ACC_PER_TASK_COM WHERE APTC_ASSIGNED='"
							+ empId + "'");
			int taskCode = rs.next() ? rs.getInt("TASKCODE") : 0;
			logger.info("Inserting in common table");
			stmt.execute(
					"INSERT INTO ACC_PER_TASK_COM (APTC_TASK_PER_CODE,APTC_TASK_NAME,APTC_ASSIGNED,APTC_TASK_STATUS,APTC_TASK_DEPENDENCY,APTC_TASK_CONTEXT)"
							+ " VALUES (" + taskCode + ",'" + newTask.getTaskName() + "'," + empId
							+ ",'P',0,'C')");
			logger.info("Inserting in details table");
			stmt.execute(
					"INSERT INTO ACC_PER_TASK_EXP (APTE_ASSIGNED_CODE,APTE_TASK_PER_CODE,APTE_TASK_SUMMARY,APTE_TASK_DESCRIPTION,APTE_REMARKS)"
							+ " VALUES (" + empId + "," + taskCode + ",'" + newTask.getTaskSummary() + "','"
							+ newTask.getTaskDescription() + "','" + newTask.getTaskRemarks() + "')");
			logger.info("Inserting in audit table");
			perTaskHistory(empId, taskCode, "P", conn, stmt);
			conn.commit();
			httpresp.setMessage("Task created successfully");
			httpresp.setStatus("Success");
			httpresp.setBoolstatus(true);
			httpresp.setPerTaskCode(taskCode);
			rs.close();
			stmt.close();
			conn.close();
			return httpresp;
		} catch (Exception ex) {
			logger.info("Exception occured while creating new task");
			ex.printStackTrace();
			httpresp.setMessage("Error while creating new Task");
			httpresp.setStatus("Failure");
			httpresp.setBoolstatus(false);
			return httpresp;
		}
	}

	public void perTaskHistory(int empId, int taskCode, String status, Connection conn, Statement stmt)
			throws Exception {
		stmt.execute(
				"INSERT INTO ACC_PER_TASK_HIS (APTH_ASSIGNED_CODE,APTH_TASK_PER_CODE,APTH_TASK_UPT_DATE,APTH_TASK_STATUS) "
						+ "VALUES (" + empId + "," + taskCode + ",NOW(),'" + status + "')");
	}
	
	public String changePerTaskStatus(PerTaskStatus pertask, Authentication auth, DataSource datasource) {
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			logger.info("Getting task details from database");
			int empId=SDLCRestUtil.getEmployeeId(stmt,conn,auth.getPrincipal().toString());
			if(pertask.getTaskChange()=="S") {
				perTaskHistory(empId,pertask.getTaskCode(),pertask.getTaskStatus(),conn,stmt);	
			}
			stmt.execute("UPDATE SDLC.ACC_PER_TASK_COM SET APTC_TASK_STATUS='"+pertask.getTaskStatus()
			+"', APTC_TASK_CONTEXT='"+pertask.getTaskContext()+"' WHERE APTC_TASK_PER_CODE="+pertask.getTaskCode()
			+" AND APTC_ASSIGNED="+empId);
			conn.commit();
			stmt.close();
			conn.close();
			return "Success";	
		} catch(Exception ex) {
			logger.info("Error while updating status of personnal task");
			ex.printStackTrace();
			return "Failure";
		}
	}
	
	public String updatePerTask(PersonnalTasks pertask, Authentication auth, DataSource datasource) {
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			logger.info("Getting task details from database");
			int empId=SDLCRestUtil.getEmployeeId(stmt,conn,auth.getPrincipal().toString());
			stmt.execute("UPDATE SDLC.ACC_PER_TASK_COM SET APTC_TASK_NAME='"+pertask.getTaskName()
			+"' WHERE APTC_TASK_PER_CODE="+pertask.getTaskCode()+" AND APTC_ASSIGNED="+empId);
			stmt.execute("UPDATE SDLC.ACC_PER_TASK_EXP SET APTE_TASK_SUMMARY='"+pertask.getTaskSummary()
			+"',APTE_TASK_DESCRIPTION='"+pertask.getTaskDescription()+"',APTE_REMARKS='"+pertask.getTaskRemarks()
			+"' WHERE APTE_TASK_PER_CODE="+pertask.getTaskCode()+" AND APTE_ASSIGNED_CODE= "+empId);
			conn.commit();
			stmt.close();
			conn.close();
			return "Success";
		} catch(Exception ex) {
			logger.info("Error while updating the details of personnal task");
			ex.printStackTrace();
			return "Failure";
		}
	}
}
