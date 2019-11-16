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

import com.accelerate.sdlc.model.PersonnalDetails;
import com.accelerate.sdlc.model.RoleMaster;
import com.accelerate.sdlc.services.EmployeeMasterDao;

@RestController
@RequestMapping("/empmast")
public class EmployeeMaster {

	@Autowired
	DataSource datasource;
	
	@RequestMapping(value="/getrolelist",method=RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SU')")
	@ResponseBody
	public List<RoleMaster> getRoleList(Authentication auth) {
		EmployeeMasterDao empMast=new EmployeeMasterDao();
		return empMast.getRoleDetails(auth, datasource);
	}
	
	@RequestMapping(value="/getemplist",method=RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU')")
	@ResponseBody
	public List<PersonnalDetails> getEmpList(@RequestBody PersonnalDetails perDet,Authentication auth) {
		EmployeeMasterDao empMast=new EmployeeMasterDao();
		return empMast.getEmployeeList(perDet, auth, datasource);
	}
	
	@RequestMapping(value="/createemp",method=RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU')")
	public String createEmployee(@RequestBody PersonnalDetails perDet,Authentication auth) {
		EmployeeMasterDao empMast=new EmployeeMasterDao();
		return empMast.createNewEmployee(perDet, auth, datasource);
	}
	
	@RequestMapping(value="/updateemp",method=RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SU')")
	public String updateEmployee(@RequestBody PersonnalDetails perDet,Authentication auth) {
		EmployeeMasterDao empMast=new EmployeeMasterDao();
		return empMast.updateEmployeeDetails(perDet, auth, datasource);
	}
	
	@RequestMapping(value="/testemp",method=RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SU','TL','RS')")
	public String testMeyhod(Authentication auth) {
		return auth.getPrincipal().toString();
	}
}
