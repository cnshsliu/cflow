package com.lkh.cflow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.lkh.cflow.db.DbAdmin;

public class DelegateManager {

	public void delegateAllTasks(String devId, DbAdmin dbadmin, String delegater, String delegatee, Calendar starttime, Calendar endtime) throws SQLException {
		Connection conn = dbadmin.getConnection();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "INSERT INTO cfdelegate (DEV, DELEGATER, DELEGATEE, STARTTIME, ENDTIME, DELEGATETYPE) VALUES (?,?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, delegater);
		ps.setString(2, delegatee);
		ps.setString(3, format.format(starttime.getTime()));
		ps.setString(4, format.format(endtime.getTime()));
		ps.setString(5, "ALL");
		ps.execute();

	}

	public void unDelegateAllTasks(String devId, DbAdmin dbadmin, String delegater, long id) throws SQLException {
		Connection conn = dbadmin.getConnection();
		Statement stat = conn.createStatement();
		String sql = "DELETE FROM cfdelegate  WHERE DELEGATER = ? AND ID=?";
		stat.execute(sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, delegater);
		ps.setLong(2, id);
		ps.execute();
	}

	public String[] getPeriodDelegatee(String devId, DbAdmin dbadmin, String delegater) throws SQLException {
		String[] ret = {};
		ArrayList<String> list = new ArrayList<String>();
		Connection conn = dbadmin.getConnection();
		String sql = "SELECT DELEGATEE FROM cf_delegate WHERE DEV=? AND DELEGATETYPE='ALL' AND DELEGATER=? AND STARTTIME < CURRENT_TIMESTAMP() AND ENDTIME > CURRENT_TIMESTAMP()";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, devId);
		ps.setString(2, delegater);
		ResultSet rs = ps.executeQuery();
		for (;;) {
			if (rs.next()) {
				String tmp = rs.getString("DELEGATEE");
				list.add(tmp);
			} else
				break;
		}
		ret = (String[]) list.toArray(new String[list.size()]);
		return ret;
	}
}
