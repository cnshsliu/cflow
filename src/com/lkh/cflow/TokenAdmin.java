package com.lkh.cflow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

public class TokenAdmin {
	public static JdbcConnectionPool memCpool = null;
	private final static Logger logger = Logger.getLogger(TokenAdmin.class);
	private final static long TOKEN_AVAILABILITY = 1800000L;
	private final static long TOKEN_CLEAR_DURATION = 10000L;

	public static void initialize(boolean useTCP) throws SQLException {
		if (useTCP) {
			memCpool = JdbcConnectionPool.create("jdbc:h2:tcp://localhost:8092/mem:cflow;MODE=MySQL", "cflow", "cflow1234");
		} else {
			memCpool = JdbcConnectionPool.create("jdbc:h2:mem:cflow;MODE=MySQL", "cflow", "cflow1234");
		}
		Connection conn = memCpool.getConnection();
		String sql = "CREATE TABLE IF NOT EXISTS cflow_token (DEV varchar(24) NOT NULL, TOKEN varchar(40),  CRTAT long not null, PRIMARY KEY (TOKEN)) ";
		java.sql.Statement stat = conn.createStatement();
		stat.execute(sql);
		sql = "select count(*) from cflow_token";
		ResultSet rs = stat.executeQuery(sql);
		if (rs.next()) {
			rs.getInt(1);
		}

		new Thread(new Runnable() {
			public void run() {
				Connection conn = null;
				for (;;) {
					conn = null;
					try {
						conn = memCpool.getConnection();
						String sql = "DELETE FROM cflow_token WHERE CRTAT < (? - ?)";
						PreparedStatement ps = conn.prepareCall(sql);
						ps.setLong(1, System.currentTimeMillis());
						ps.setLong(2, TOKEN_AVAILABILITY);
						ps.execute();
						ps.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (conn != null) {
							try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						Thread.currentThread().sleep(TOKEN_CLEAR_DURATION);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static String newToken(String dev) throws SQLException {
		String newToken = null;
		Connection conn = null;
		try {
			conn = memCpool.getConnection();
			newToken = UUID.randomUUID().toString().replaceAll("-", "");
			long crtAt = System.currentTimeMillis();
			String memSql = "INSERT INTO cflow_token (DEV, TOKEN, CRTAT) values (?, ?, ?)";
			PreparedStatement psMem = conn.prepareStatement(memSql);
			psMem.setString(1, dev);
			psMem.setString(2, newToken);
			psMem.setLong(3, crtAt);
			psMem.execute();
			psMem.close();
			conn.close();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return newToken;
	}

	public static String copyToken(String tokenTobeCopied, String dev) throws SQLException {
		Connection conn = null;
		try {
			conn = memCpool.getConnection();
			long crtAt = System.currentTimeMillis();
			String memSql = "INSERT INTO cflow_token (DEV, TOKEN, CRTAT) values (?, ?, ?)";
			PreparedStatement psMem = conn.prepareStatement(memSql);
			psMem.setString(1, dev);
			psMem.setString(2, tokenTobeCopied);
			psMem.setLong(3, crtAt);
			psMem.execute();
			psMem.close();
			conn.close();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tokenTobeCopied;
	}

	public static String getDevByToken(String token) throws SQLException {
		String dev = null;
		Connection conn = null;
		try {
			conn = memCpool.getConnection();
			String sql = "SELECT DEV FROM cflow_token WHERE TOKEN=? AND CRTAT > (? - ?) ";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, token);
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, TOKEN_AVAILABILITY);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				dev = rs.getString("DEV");
			}
			ps.close();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dev;
	}

	public static void refreshToken(String token) {
		Connection conn = null;
		PreparedStatement updatePs = null;
		try {
			conn = memCpool.getConnection();
			String updateSql = "UPDATE cflow_token SET CRTAT = ? WHERE TOKEN=?";
			updatePs = conn.prepareStatement(updateSql);
			updatePs.setLong(1, System.currentTimeMillis());
			updatePs.setString(2, token);
			updatePs.execute();
			updatePs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void removeToken(String token, String ip) {
	}
}
