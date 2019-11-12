package com.accelerate.sdlc.controller;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accelerate.sdlc.model.ChangePassword;
import com.accelerate.sdlc.model.PersonnalDetails;
import com.accelerate.sdlc.services.PersonnalSettingsDao;

@RestController
@RequestMapping("/persettings")
public class PersonnalSettings {
	
	@Autowired
	DataSource datasource;
	
	@RequestMapping(value="/getperdetails",method=RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	@ResponseBody
	public PersonnalDetails getPersonnalDetails(Authentication auth) {
		PersonnalSettingsDao perSet=new PersonnalSettingsDao();
		return perSet.getPersonnalDetails(auth, datasource);
	}
	
	@RequestMapping(value="/setperdetails",method=RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	public String setPersonnalDetails(@RequestBody PersonnalDetails perDet,Authentication auth) {
		PersonnalSettingsDao perSet=new PersonnalSettingsDao();
		return perSet.setPersonnalDetails(perDet, auth, datasource);
	}
	
	@RequestMapping(value="/setnewpass",method=RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	public String setNewPassword(@RequestBody ChangePassword perCre,Authentication auth) {
		PersonnalSettingsDao perSet=new PersonnalSettingsDao();
		return perSet.setNewPassword(perCre, auth, datasource);
	}
}
