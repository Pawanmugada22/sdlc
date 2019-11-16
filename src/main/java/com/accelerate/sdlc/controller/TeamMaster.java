package com.accelerate.sdlc.controller;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accelerate.sdlc.model.TeamMasterModel;
import com.accelerate.sdlc.services.TeamMasterDao;

@RestController
@RequestMapping("/teammast")
public class TeamMaster {

	@Autowired
	DataSource datasource;
	
	@RequestMapping(value = "/getallteams", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SU')")
	@ResponseBody
	public List<TeamMasterModel> getAllTeams(Authentication auth) {
		TeamMasterDao teamMast = new TeamMasterDao();
		return teamMast.getAllTeams(auth, datasource);
	}
	
	@RequestMapping(value = "/getteamlist", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU')")
	@ResponseBody
	public List<TeamMasterModel> getTeamList(@RequestBody TeamMasterModel teamDet, Authentication auth) {
		TeamMasterDao teamMast = new TeamMasterDao();
		return teamMast.getTeamList(teamDet, auth, datasource);
	}

	@RequestMapping(value = "/createteam", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU')")
	public String createNewTeam(@RequestBody TeamMasterModel teamDet, Authentication auth) {
		TeamMasterDao teamMast = new TeamMasterDao();
		return teamMast.createNewTeam(teamDet, auth, datasource);
	}

	@RequestMapping(value = "/updateteam", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU')")
	public String updateTeam(@RequestBody TeamMasterModel teamDet, Authentication auth) {
		TeamMasterDao teamMast = new TeamMasterDao();
		return teamMast.updateTeam(teamDet, auth, datasource);
	}

}
