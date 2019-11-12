package com.accelerate.sdlc.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.accelerate.sdlc.model.ChangePassword;
import com.accelerate.sdlc.model.PersonnalDetails;
import com.accelerate.sdlc.util.SDLCRestUtil;
import com.accelerate.sdlc.util.SDLCSecurityUtil;

import lombok.Cleanup;

public class PersonnalSettingsDao {

	private static final Logger logger = LoggerFactory.getLogger(PersonnalSettingsDao.class);

	public PersonnalDetails getPersonnalDetails(Authentication auth, DataSource datasource) {
		PersonnalDetails perDet = new PersonnalDetails();
		try {
			logger.info("Getting personnal details");
			@Cleanup
			Connection conn = datasource.getConnection();
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = null;
			logger.info("Getting task details from database");
			int empId = SDLCRestUtil.getEmployeeId(stmt, conn, auth.getPrincipal().toString());
			rs = stmt.executeQuery(
					"SELECT AEM_EMP_FIRST_NAME,AEM_EMP_MIDDLE_NAME,AEM_EMP_LAST_NAME,AEM_EMP_LAST_NAME,AEM_EMP_EMG_PHONE_NO,AEM_EMP_DOB,AEM_EMP_ADDR1,"
							+ "AEM_EMP_ADDR2,AEM_EMP_LANDMARK,AEM_EMP_CITY,AEM_EMP_STATE,AEM_EMP_COUNTRY,AEM_EMP_POST_CODE,AEM_EMP_SHORT_NAME FROM SDLC.ACC_EMPLOYEE_MAST"
							+ " WHERE AEM_EMP_CODE=" + empId);
			while (rs.next()) {
				perDet.setFirstName(rs.getString("AEM_EMP_FIRST_NAME"));
				perDet.setMiddleName(rs.getString("AEM_EMP_MIDDLE_NAME"));
				perDet.setLastName(rs.getString("AEM_EMP_LAST_NAME"));
				perDet.setPhNo(rs.getString("AEM_EMP_LAST_NAME"));
				perDet.setEmgPhNo(rs.getString("AEM_EMP_EMG_PHONE_NO"));
				perDet.setDob(rs.getString("AEM_EMP_DOB"));
				perDet.setAddr1(rs.getString("AEM_EMP_ADDR1"));
				perDet.setAddr2(rs.getString("AEM_EMP_ADDR2"));
				perDet.setLandmark(rs.getString("AEM_EMP_LANDMARK"));
				perDet.setCity(rs.getString("AEM_EMP_CITY"));
				perDet.setState(rs.getString("AEM_EMP_STATE"));
				perDet.setCountry(rs.getString("AEM_EMP_COUNTRY"));
				perDet.setPostCode(rs.getString("AEM_EMP_POST_CODE"));
				perDet.setShortName(rs.getString("AEM_EMP_SHORT_NAME"));
			}
			rs.close();
			stmt.close();
			conn.close();
			return perDet;
		} catch (Exception ex) {
			logger.info("Exception occured while getting personnal details");
			ex.printStackTrace();
			return perDet;
		}
	}

	public String setPersonnalDetails(PersonnalDetails perDet, Authentication auth, DataSource datasource) {
		try {
			logger.info("Updating personnal details");
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			logger.info("Getting task details from database");
			int empId = SDLCRestUtil.getEmployeeId(stmt, conn, auth.getPrincipal().toString());
			PreparedStatement prestmt = conn.prepareStatement(
					"UPDATE SDLC.ACC_EMPLOYEE_MAST SET AEM_EMP_FIRST_NAME=?,AEM_EMP_MIDDLE_NAME=?,AEM_EMP_LAST_NAME=?,AEM_EMP_PHONE_NO=?,AEM_EMP_EMG_PHONE_NO=?,"
							+ "AEM_EMP_DOB=?,AEM_EMP_ADDR1=?,AEM_EMP_ADDR2=?,AEM_EMP_LANDMARK=?,AEM_EMP_CITY=?,AEM_EMP_STATE=?,AEM_EMP_COUNTRY=?,AEM_EMP_POST_CODE=?,"
							+ "AEM_EMP_UPD_TIME=NOW(),AEM_EMP_UPD_BY=?,AEM_EMP_SHORT_NAME=? WHERE AEM_EMP_CODE=?");
			prestmt.setString(1, perDet.getFirstName());
			prestmt.setString(2, perDet.getMiddleName());
			prestmt.setString(3, perDet.getLastName());
			prestmt.setString(4, perDet.getPhNo());
			prestmt.setString(5, perDet.getEmgPhNo());
			prestmt.setDate(6, Date.valueOf(perDet.getDob() == null ? "2012-12-21" : perDet.getDob()));
			prestmt.setString(7, perDet.getAddr1());
			prestmt.setString(8, perDet.getAddr2());
			prestmt.setString(9, perDet.getLandmark());
			prestmt.setString(10, perDet.getCity());
			prestmt.setString(11, perDet.getState());
			prestmt.setString(12, perDet.getCountry());
			prestmt.setString(13, perDet.getPostCode());
//			prestmt.setTimestamp(14,Timestamp.valueOf(LocalDateTime.now()));
			prestmt.setInt(14, empId);
			prestmt.setString(15, perDet.getShortName());
			prestmt.setInt(16, empId);
			prestmt.execute();
			conn.commit();
			stmt.close();
			prestmt.close();
			conn.close();
			return "Success";
		} catch (Exception ex) {
			logger.info("Exception occured while updating personnal details");
			ex.printStackTrace();
			return "Failure";
		}
	}

	public String setNewPassword(ChangePassword perCre, Authentication auth, DataSource datasource) {
		String status = "Failure";
		try {
			logger.info("Updating new password");
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = null;
			logger.info("Getting task details from database");
			int empId = SDLCRestUtil.getEmployeeId(stmt, conn, auth.getPrincipal().toString());
			if (this.checkOldPassword(conn, stmt, rs, auth, perCre.getOldPassword())) {
				StringBuilder str = new StringBuilder();
				str.append("sdlc");
				str.append(auth.getPrincipal().toString());
				str.append(perCre.getNewPassword().toUpperCase());
				stmt.execute("DELETE FROM SDLC.ACC_EMP_LOGIN WHERE AEL_EMP_CODE=" + empId);
				stmt.execute("INSERT INTO SDLC.ACC_EMP_LOGIN (AEL_EMP_CODE,AEL_LOGIN_ID,AEL_LOGIN_PASSWORD,AEL_UPD_DATE) "
						+ "VALUES(" + empId + ",'" + auth.getPrincipal().toString() + "','"
						+ SDLCSecurityUtil.getHash(str.toString()) + "',NOW())");
				conn.commit();
				status = "Success";
			}
			stmt.close();
			conn.close();
			return status;
		} catch (Exception ex) {
			logger.info("Error while updating the password");
			ex.printStackTrace();
			return status;
		}
	}

	public boolean checkOldPassword(Connection conn, Statement stmt, ResultSet rs, Authentication auth, String oldPass)
			throws Exception {
		boolean isValid = false;
		StringBuilder str = new StringBuilder();
		str.append("sdlc");
		str.append(auth.getPrincipal().toString());
		str.append(oldPass.toUpperCase());
		rs = stmt.executeQuery(
				"SELECT COUNT(*) AS COUNT FROM SDLC.ACC_EMP_LOGIN WHERE AEL_LOGIN_ID='" + auth.getPrincipal().toString()
						+ "' AND AEL_LOGIN_PASSWORD='" + SDLCSecurityUtil.getHash(str.toString()) + "'");
		while (rs.next()) {
			if (rs.getInt("COUNT") >= 1) {
				isValid = true;
			}
		}
		rs.close();
		return isValid;
	}

}
