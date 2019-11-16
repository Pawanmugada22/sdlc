package com.accelerate.sdlc.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.accelerate.sdlc.model.PersonnalDetails;
import com.accelerate.sdlc.model.RoleMaster;
import com.accelerate.sdlc.util.SDLCRestUtil;
import com.accelerate.sdlc.util.SDLCSecurityUtil;

import lombok.Cleanup;

public class EmployeeMasterDao {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeMasterDao.class);

	boolean queryCheck = true;

	public List<RoleMaster> getRoleDetails(Authentication auth, DataSource datasource) {
		List<RoleMaster> rmList = new ArrayList<RoleMaster>();
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = null;
			rs = stmt.executeQuery("SELECT ARM_ROLE_NAME,ARM_ROLE_CODE FROM SDLC.ACC_ROLE_MAST");
			while (rs.next()) {
				RoleMaster rm = new RoleMaster();
				rm.setRoleName(rs.getString("ARM_ROLE_NAME"));
				rm.setRoleCode(rs.getString("ARM_ROLE_CODE"));
				rmList.add(rm);
			}
			rs.close();
			stmt.close();
			conn.close();
			return rmList;
		} catch (Exception ex) {
			logger.info("Exception occured while getting role list");
			ex.printStackTrace();
			return rmList;
		}
	}

	public List<PersonnalDetails> getEmployeeList(PersonnalDetails perDet, Authentication auth, DataSource datasource) {
		List<PersonnalDetails> perDetList = new ArrayList<PersonnalDetails>();
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = null;
			StringBuilder query = new StringBuilder();
			query.append(
					"SELECT AEM_EMP_CODE,AEM_EMP_FIRST_NAME,AEM_EMP_MIDDLE_NAME,AEM_EMP_LAST_NAME,AEM_EMP_PHONE_NO,AEM_EMP_EMG_PHONE_NO,"
							+ "AEM_EMP_DOB,AEM_EMP_ADDR1,AEM_EMP_ADDR2,AEM_EMP_LANDMARK,AEM_EMP_CITY,AEM_EMP_STATE,AEM_EMP_COUNTRY,AEM_EMP_POST_CODE,"
							+ "AEM_EMP_SHORT_NAME,AEM_EMP_TEAM,AEM_EMP_ROLE,AEM_EMP_STATUS,AEM_EMP_JOINING_DATE,AEM_EMP_REMARKS FROM SDLC.ACC_EMPLOYEE_MAST");
			if (perDet.getEmpId() > 0) {
				query = buildEmpListQuery(query);
				query.append("AEM_EMP_CODE=" + perDet.getEmpId());
			}
			if (perDet.getFirstName().length() > 0) {
				query = buildEmpListQuery(query);
				query.append("AEM_EMP_FIRST_NAME LIKE '%" + perDet.getFirstName() + "%'");
			}
			if (perDet.getRoleCode().length() > 0) {
				query = buildEmpListQuery(query);
				query.append("AEM_EMP_ROLE='" + perDet.getRoleCode() + "'");
			}
			if (perDet.getTeamCode() > 0) {
				query = buildEmpListQuery(query);
				query.append("AEM_EMP_TEAM=" + perDet.getTeamCode());
			}
			logger.info(query.toString());
			rs = stmt.executeQuery(query.toString());
			while (rs.next()) {
				PersonnalDetails perDets = new PersonnalDetails();
				perDets.setEmpId(rs.getInt("AEM_EMP_CODE"));
				perDets.setFirstName(rs.getString("AEM_EMP_FIRST_NAME"));
				perDets.setMiddleName(rs.getString("AEM_EMP_MIDDLE_NAME"));
				perDets.setLastName(rs.getString("AEM_EMP_LAST_NAME"));
				perDets.setPhNo(rs.getString("AEM_EMP_LAST_NAME"));
				perDets.setEmgPhNo(rs.getString("AEM_EMP_EMG_PHONE_NO"));
				perDets.setDob(rs.getString("AEM_EMP_DOB"));
				perDets.setAddr1(rs.getString("AEM_EMP_ADDR1"));
				perDets.setAddr2(rs.getString("AEM_EMP_ADDR2"));
				perDets.setLandmark(rs.getString("AEM_EMP_LANDMARK"));
				perDets.setCity(rs.getString("AEM_EMP_CITY"));
				perDets.setState(rs.getString("AEM_EMP_STATE"));
				perDets.setCountry(rs.getString("AEM_EMP_COUNTRY"));
				perDets.setPostCode(rs.getString("AEM_EMP_POST_CODE"));
				perDets.setShortName(rs.getString("AEM_EMP_SHORT_NAME"));
				perDets.setTeamCode(rs.getInt("AEM_EMP_TEAM"));
				perDets.setState(rs.getString("AEM_EMP_STATUS"));
				perDets.setRoleCode(rs.getString("AEM_EMP_ROLE"));
				perDets.setJoiningDate(rs.getString("AEM_EMP_JOINING_DATE"));
				perDets.setStatus(rs.getString("AEM_EMP_STATUS"));
				perDets.setRemarks(rs.getString("AEM_EMP_REMARKS"));
				perDetList.add(perDets);
			}
			rs.close();
			stmt.close();
			conn.close();
			return perDetList;
		} catch (Exception ex) {
			logger.info("Exception occured while getching the employee list");
			ex.printStackTrace();
			return perDetList;
		}
	}

	public String createNewEmployee(PersonnalDetails perDet, Authentication auth, DataSource datasource) {
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = null;
			int empId = SDLCRestUtil.getEmployeeId(stmt, conn, auth.getPrincipal().toString());
			if (perDet.isResetPass()) {
				int empCode = 0;
				rs = stmt.executeQuery("SELECT COUNT(*) AS COUNT FROM SDLC.ACC_EMP_LOGIN WHERE AEL_LOGIN_ID='"
						+ perDet.getUserName() + "'");
				while (rs.next()) {
					int count = rs.getInt("COUNT");
					if (count > 0) {
						return "Username already taken please use different username";
					}
				}
				PreparedStatement empDetailsQuery = conn
						.prepareStatement("INSERT INTO ACC_EMPLOYEE_MAST (AEM_EMP_FIRST_NAME,AEM_EMP_CRT_BY,"
								+ "AEM_EMP_CRT_TIME,AEM_EMP_TEAM,AEM_EMP_ROLE,AEM_EMP_STATUS,AEM_EMP_JOINING_DATE,AEM_EMP_REMARKS) VALUES (?,?,NOW(),?,?,?,?,?)");
				empDetailsQuery.setString(1, perDet.getFirstName());
				empDetailsQuery.setInt(2, empId);
				empDetailsQuery.setInt(3, perDet.getTeamCode());
				empDetailsQuery.setString(4, perDet.getRoleCode());
				empDetailsQuery.setString(5, perDet.getStatus());
				empDetailsQuery.setDate(6, Date.valueOf(perDet.getJoiningDate()));
				empDetailsQuery.setString(7, perDet.getRemarks());
				empDetailsQuery.execute();
				rs = stmt.executeQuery("SELECT AEM_EMP_CODE FROM SDLC.ACC_EMPLOYEE_MAST WHERE AEM_EMP_CRT_BY=" + empId
						+ " ORDER BY AEM_EMP_CRT_TIME DESC LIMIT 1");
				while (rs.next()) {
					empCode = rs.getInt("AEM_EMP_CODE");
				}
				if (this.setNewPassword(perDet, auth, empCode, stmt)) {
					empDetailsQuery.close();
					conn.commit();
				}
			}
			rs.close();
			stmt.close();
			conn.close();
			return "Success";
		} catch (Exception ex) {
			logger.info("Exception occured while creating new employee");
			ex.printStackTrace();
			return "Failure";
		}
	}

	public String updateEmployeeDetails(PersonnalDetails perDet, Authentication auth, DataSource datasource) {
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			int empId = SDLCRestUtil.getEmployeeId(stmt, conn, auth.getPrincipal().toString());
			PreparedStatement updateQuery = conn.prepareStatement(
					"UPDATE SDLC.ACC_EMPLOYEE_MAST SET AEM_EMP_TEAM=?,AEM_EMP_ROLE=?,AEM_EMP_STATUS=?,AEM_EMP_REMARKS=?,AEM_EMP_UPD_TIME=NOW(),AEM_EMP_UPD_BY=? WHERE AEM_EMP_CODE=?");
			updateQuery.setInt(1, perDet.getTeamCode());
			updateQuery.setString(2, perDet.getRoleCode());
			updateQuery.setString(3, perDet.getStatus());
			updateQuery.setString(4, perDet.getRemarks());
			updateQuery.setInt(5, empId);
			updateQuery.setInt(6, perDet.getEmpId());
			updateQuery.execute();
			if (perDet.isResetPass()) {
				stmt.executeQuery("DELETE ACC_EMP_LOGIN WHERE AEL_LOGIN_ID=" + perDet.getEmpId());
				if (this.setNewPassword(perDet, auth, perDet.getEmpId(), stmt)) {
					logger.info("Password updated");
				}
			}
			conn.commit();
			updateQuery.close();
			stmt.close();
			conn.close();
			return "Success";
		} catch (Exception ex) {
			logger.info("Error while updating the employee details");
			ex.printStackTrace();
			return "Failure";
		}
	}

	public boolean setNewPassword(PersonnalDetails perDet, Authentication auth, int empCode, Statement stmt) {
		try {
			StringBuilder str = new StringBuilder();
			str.append("sdlc");
			str.append(perDet.getUserName());
			str.append(perDet.getNewPassword().toUpperCase());
			String empPass = SDLCSecurityUtil.getHash(str.toString());
			if (empCode > 0) {
				stmt.execute(
						"INSERT INTO ACC_EMP_LOGIN (AEL_EMP_CODE,AEL_LOGIN_ID,AEL_LOGIN_PASSWORD,AEL_UPD_DATE) VALUES("
								+ empCode + ",'" + perDet.getUserName() + "','" + empPass + "',NOW())");
			}
			return true;
		} catch (Exception ex) {
			logger.info("Error while updating the password");
			ex.printStackTrace();
			return false;
		}
	}

	public StringBuilder buildEmpListQuery(StringBuilder str) {
		if (this.queryCheck) {
			str.append(" WHERE ");
			queryCheck = false;
		} else {
			str.append(" AND ");
		}
		return str;
	}
}
