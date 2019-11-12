package com.accelerate.sdlc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import lombok.Cleanup;

public class SDLCRestUtil {

	public static int getEmployeeId(Statement stmt, Connection conn, String empLogin) throws Exception {
		@Cleanup
		ResultSet rs = stmt.executeQuery(
				"SELECT AEL_EMP_CODE AS CODE FROM SDLC.ACC_EMP_LOGIN WHERE AEL_LOGIN_ID='" + empLogin + "'");
		int empId = rs.next() ? rs.getInt("CODE") : 0;
		rs.close();
		return empId;
	}
}
