package com.accelerate.sdlc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class SDLCRestUtil {
	public static int getEmployeeId(Statement stmt,Connection conn,ResultSet rs,String empLogin) throws Exception {
		rs = stmt.executeQuery("SELECT AEL_EMP_CODE AS CODE FROM SDLC.ACC_EMP_LOGIN WHERE AEL_LOGIN_ID='"
				+ empLogin + "'");
		int empId = rs.next() ? rs.getInt("CODE") : 0;
		return empId;
	}
}
