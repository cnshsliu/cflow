package com.lkh.cflow;

/**
 *
 * @author  gcai
 */

// Necessary classes to get Pooled Database Connection
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * This class defines getConnection and releaseConnection methods. In
 * getConnection method, connection caching feature of JDBC 2.0 is implemented
 * to get the database connection.
 */
public class DruidConnector {

	private static DruidConnector connector_ = null;
	private static DruidDataSource dataSource_ = null;

	static Logger logger = Logger.getLogger(DruidConnector.class);

	// Create a sinlgeton object
	public static DruidConnector getInstance() throws Exception {
		if (connector_ == null) {
			connector_ = new DruidConnector();
			connector_.initializeDataSource();
		}

		return connector_;
	}

	private void initializeDataSource() throws Exception {
		javax.naming.Context initCtx = new InitialContext();
		javax.naming.Context envCtx = (javax.naming.Context) initCtx.lookup("java:comp/env");
		dataSource_ = (DruidDataSource) ((javax.naming.Context) envCtx).lookup("jdbc/cflow");

	}

	public synchronized Connection getConnection() throws SQLException {
		return dataSource_.getConnection();
	}

	public int getNumActive() {
		return dataSource_.getActiveCount();
	}

	public int getNumIdle() {
		return dataSource_.getMaxIdle();
	}

}
