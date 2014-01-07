package com.lkh.cflow.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * Servlet implementation class TestSL1
 */
public class TestSL1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestSL1() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		testDbAdminPool();
	}

	private void testDbAdminPool() {
		for (int i = 0; i < 100; i++) {
			new Thread() {
				public void run() {

					DbAdmin dbadmin = DbAdminPool.get();
					try {
						System.out.println(dbadmin.getInstanceNumber() + "\t" + DbAdminPool.pool().toString());
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						DbAdminPool.ret(dbadmin);
					}
				}

			}.start();
		}
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(DbAdminPool.pool().toString());
	}
}
