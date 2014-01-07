package com.lkh.cflow;

import java.sql.SQLException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.lkh.cflow.db.DbAdmin;

public class FifoHelper {
	DbAdmin dbadmin;
	Locale locale;
	TextHelper textHelper;
	
	public FifoHelper(DbAdmin dbadmin, Locale theLocale, TextHelper textHelper)
	{
		this.dbadmin = dbadmin;
		this.locale = theLocale;
		this.textHelper = textHelper;
	}
	
	
	public void Fi(HttpServletRequest request) throws SQLException{
		String owner = request.getParameter("owner");
		String msg = request.getParameter("msg");
		String toend = request.getParameter("toend");
		
		dbadmin.fi("q1", owner, toend, msg);
	}
	
	public void Fi(String owner, String toend, String msg) throws SQLException{
		dbadmin.fi("q1", owner, toend, msg);
	}

}
