package com.accelerate.sdlc.controller;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accelerate.sdlc.model.*;
import com.accelerate.sdlc.services.PersonnalTasksDao;



@RestController
@RequestMapping("/pertask")
public class SDLCPersonalTasks {
	
	@Autowired
	DataSource datasource;
	
	@RequestMapping(value="/getpertasks",method=RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	@ResponseBody
	public List<PersonnalTasks> getPerTasks(@RequestParam String context,Authentication auth) {
		PersonnalTasksDao pertask=new PersonnalTasksDao();
		return pertask.getPersonnalTask(context, auth, datasource);
	}
	
	@RequestMapping(value="/createpertask",method=RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	public SDLCHttpResp postNewPerTask(@RequestBody PersonnalTasks newTask,Authentication auth) {
		PersonnalTasksDao pertask=new PersonnalTasksDao();
		return pertask.createNewPerTask(newTask, auth, datasource);
	}
	
	@RequestMapping(value="/posttaskstatus",method=RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	public String postTaskStatus(Authentication auth) {
		return "Haha";
	}
	
	@RequestMapping(value="/updpertask",method=RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	public String updateTaskDetails(Authentication auth) {
		return "Haha";
	}
}