package com.lkh.cflow.test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;

import nf.fr.eraasoft.pool.ObjectPool;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.h2.jdbcx.JdbcConnectionPool;
import org.json.simple.JSONObject;

import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

public class Test1 {

	static float Threshold = (float) 0.001;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Test1 t1 = new Test1();
		t1.testDbAdminPool();
	}

	private static String myEscape(String str) {
		str = StringUtils.replace(str, "\\", "\\\\");
		str = StringUtils.replace(str, "\"", "\\\"");
		// str = StringEscapeUtils.escapeJava(str);

		return str;
	}

	private void testCode() throws Exception {
		long f1 = Runtime.getRuntime().freeMemory();
		Object obj = new Object();
		long f2 = Runtime.getRuntime().freeMemory();
		System.out.println(f1 - f2);
	}

	private void testDbAdminPool() {
		for (int i = 0; i < 100; i++) {
			new Thread() {
				public void run() {
					ObjectPool<DbAdmin> dbAdminPool = DbAdminPool.pool();
					DbAdmin dbadmin = null;
					try {
						dbadmin = dbAdminPool.getObj();
						System.out.println(dbadmin.getCreateTimestamp() + "\t" + Thread.currentThread().getId());
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						dbAdminPool.returnObj(dbadmin);
					}
				}

			}.start();
		}
	}

	private void testH2() throws Exception {
		JdbcConnectionPool memCpool = JdbcConnectionPool.create("jdbc:h2:mem:cflow;MODE=MySQL", "cflow", "cflow");
		Connection conn = memCpool.getConnection();
		String sql = "CREATE TABLE IF NOT EXISTS cf_dev (ID varchar(24) NOT NULL,  NAME varchar(40) DEFAULT NULL, ACCESSKEY varchar(40) DEFAULT NULL, EMAIL varchar(50) DEFAULT NULL,   TZID char(9) NOT NULL DEFAULT 'GMT+00:00',   LANG varchar(8) NOT NULL DEFAULT 'en-US',   PRIMARY KEY (ID))";
		java.sql.Statement stat = conn.createStatement();
		stat.execute(sql);
		sql = "CREATE TABLE IF NOT EXISTS cf_sess (DEV varchar(24) NOT NULL,  SESSKEY varchar(40) DEFAULT NULL, SESSTIME timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,  KEY IDXSESSION (SESSKEY),   KEY cf_sess_ibfk_1 (DEV),  CONSTRAINT cf_sess_ibfk_1 FOREIGN KEY (DEV) REFERENCES cf_dev (ID) ON DELETE CASCADE	)";
		stat.execute(sql);
		sql = "select count(*) from cf_dev";
		ResultSet rs = stat.executeQuery(sql);
		if (rs.next()) {
			System.out.println(rs.getInt(1));
		}
	}

	private void testJSON() throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("hello", "hello大家好Value");
		System.out.println(obj.toString());
		System.out.println(obj.toJSONString());
		String abc = "hello大家好Value";
		System.out.println(abc.toString());
	}

	private void testBAOS() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String url = "http://localhost:8189/";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httget = new HttpGet(url);

		HttpResponse response = httpclient.execute(httget);
		HttpEntity entity = response.getEntity();
		long len = entity.getContentLength();
		InputStream inputStream = entity.getContent();

		inpsToOups(inputStream, baos);
		baos.close();
		System.out.print(baos.toString("UTF-8"));
		httpclient.getConnectionManager().shutdown();
	}

	private void readUrl() throws Exception {
		String url = "http://localhost:8189/";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httget = new HttpGet(url);

		HttpResponse response = httpclient.execute(httget);
		HttpEntity entity = response.getEntity();
		long len = entity.getContentLength();
		InputStream inputStream = entity.getContent();

		FileOutputStream os = new FileOutputStream("C:\\homepage\\test.html");
		inpsToOups(inputStream, os);
		httpclient.getConnectionManager().shutdown();

	}

	private void inpsToOups(InputStream inps, OutputStream oups) {
		byte[] buffer = new byte[1024];
		int i = 0;
		try {
			while ((i = inps.read(buffer)) != -1) {
				oups.write(buffer, 0, i);
			}// end while
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				inps.close();
				oups.flush();
				oups.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
