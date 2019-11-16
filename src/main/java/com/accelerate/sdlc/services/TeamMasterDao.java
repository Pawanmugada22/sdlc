package com.accelerate.sdlc.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.accelerate.sdlc.model.TeamMasterModel;
import com.accelerate.sdlc.util.SDLCRestUtil;

import lombok.Cleanup;

public class TeamMasterDao {

	private static final Logger logger = LoggerFactory.getLogger(TeamMasterDao.class);

	boolean queryCheck = true;
	
	public List<TeamMasterModel> getAllTeams(Authentication auth, DataSource datasource) {
		List<TeamMasterModel> teamList = new ArrayList<TeamMasterModel>();
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = null;
			rs=stmt.executeQuery("SELECT ATM_TEAM_CODE,ATM_TEAM_NAME FROM SDLC.ACC_TEAM_MAST");
			while(rs.next()) {
				TeamMasterModel teamMod = new TeamMasterModel();
				teamMod.setTeamCode(rs.getInt("ATM_TEAM_CODE"));
				teamMod.setTeamName(rs.getString("ATM_TEAM_NAME"));
				teamList.add(teamMod);
			}
			rs.close();
			stmt.close();
			conn.close();
			return teamList;
		}catch(Exception ex) {
			logger.info("Exception occured while getting all teams list");
			ex.printStackTrace();
			return teamList;
		}
	}

	public List<TeamMasterModel> getTeamList(TeamMasterModel teamDet, Authentication auth, DataSource datasource) {
		List<TeamMasterModel> teamList = new ArrayList<TeamMasterModel>();
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			@Cleanup
			Statement stmt = conn.createStatement();
			@Cleanup
			ResultSet rs = null;
			StringBuilder str = new StringBuilder();
			str.append(
					"SELECT ATM_TEAM_CODE,ATM_TEAM_NAME,ATM_TEAM_SHORT_NAME,ATM_STATUS,ATM_TEAM_REMARKS FROM SDLC.ACC_TEAM_MAST");
			if (teamDet.getTeamCode()>0) {
				str = this.buildEmpListQuery(str);
				str.append("ATM_TEAM_CODE LIKE '%" + teamDet.getTeamCode() + "%'");
				this.queryCheck = false;
			}
			if (teamDet.getTeamName().length() > 0) {
				str = this.buildEmpListQuery(str);
				str.append("ATM_TEAM_NAME LIKE '%" + teamDet.getTeamName() + "%'");
				this.queryCheck = false;
			}
			if (teamDet.getTeamShortName().length() > 0) {
				str = this.buildEmpListQuery(str);
				str.append("ATM_TEAM_SHORT_NAME LIKE '%" + teamDet.getTeamShortName() + "%'");
				this.queryCheck = false;
			}
			rs = stmt.executeQuery(str.toString());
			while (rs.next()) {
				TeamMasterModel teamMod = new TeamMasterModel();
				teamMod.setTeamCode(rs.getInt("ATM_TEAM_CODE"));
				teamMod.setTeamName(rs.getString("ATM_TEAM_NAME"));
				teamMod.setTeamShortName(rs.getString("ATM_TEAM_SHORT_NAME"));
				teamMod.setStatus(rs.getString("ATM_STATUS"));
				teamMod.setRemarks(rs.getString("ATM_TEAM_REMARKS"));
				teamList.add(teamMod);
			}
			rs.close();
			stmt.close();
			conn.close();
			return teamList;
		} catch (Exception ex) {
			logger.info("Exception occured while getting team list");
			ex.printStackTrace();
			return teamList;
		}
	}

	public String createNewTeam(TeamMasterModel teamDet, Authentication auth, DataSource datasource) {
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			int empId = SDLCRestUtil.getEmployeeId(stmt, conn, auth.getPrincipal().toString());
			PreparedStatement createQuery = conn.prepareStatement(
					"INSERT INTO ACC_TEAM_MAST (ATM_TEAM_NAME,ATM_TEAM_SHORT_NAME,ATM_STATUS,ATM_TEAM_CRT_BY,ATM_TEAM_CRT_DATE,ATM_TEAM_REMARKS)"
							+ " VALUES (?,?,?,?,NOW(),?)");
			createQuery.setString(1, teamDet.getTeamName());
			createQuery.setString(2, teamDet.getTeamShortName());
			createQuery.setString(3, teamDet.getStatus());
			createQuery.setInt(4, empId);
			createQuery.setString(5, teamDet.getRemarks());
			createQuery.execute();
			conn.commit();
			createQuery.close();
			stmt.close();
			conn.close();
			return "Success";
		} catch (Exception ex) {
			logger.info("Exception occured while creating new team");
			ex.printStackTrace();
			return "Failure";
		}
	}

	public String updateTeam(TeamMasterModel teamDet, Authentication auth, DataSource datasource) {
		try {
			@Cleanup
			Connection conn = datasource.getConnection();
			conn.setAutoCommit(false);
			@Cleanup
			Statement stmt = conn.createStatement();
			int empId = SDLCRestUtil.getEmployeeId(stmt, conn, auth.getPrincipal().toString());
			PreparedStatement updateQuery = conn
					.prepareStatement("UPDATE ACC_TEAM_MAST SET ATM_TEAM_NAME=?, ATM_STATUS=?, ATM_TEAM_REMARKS=?,"
							+ " ATM_TEAM_UPD_BY=?, ATM_TEAM_UPD_DATE=NOW() WHERE ATM_TEAM_CODE=?");
			updateQuery.setString(1, teamDet.getTeamName());
			updateQuery.setString(2, teamDet.getStatus());
			updateQuery.setString(3, teamDet.getRemarks());
			updateQuery.setInt(4, empId);
			updateQuery.setInt(5, teamDet.getTeamCode());
			conn.commit();
			updateQuery.close();
			stmt.close();
			conn.close();
			return "Success";
		} catch (Exception ex) {
			logger.info("Exception occured while updating the team details");
			ex.printStackTrace();
			return "Failure";
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
