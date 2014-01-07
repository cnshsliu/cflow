package com.lkh.cflow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * Servlet implementation class XSaveWft
 */
public class XSaveWft extends HttpServlet {
	private static final long serialVersionUID = 1005L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XSaveWft() {
		super();
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
		request.setCharacterEncoding("UTF8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		String wftId = (String) request.getParameter("wftid");
		String devId = (String) request.getParameter("devid");
		String wft = (String) request.getParameter("wft");
		String wftname = (String) request.getParameter("wftname");
		if (wftId.equals("index") || wftId.equals("newiid"))
			wftId = CflowHelper.myID();
		// session.setAttribute("USRID", usrid);
		String echo = "<body>";
		DbAdmin dbadmin = DbAdminPool.get();
		boolean oldFlag = dbadmin.keepConnection(true);
		try {
			wftId = CflowHelper.wftManager.saveWft(devId, dbadmin, wft, wftId, wftname);
			if (wftId == null) {
				echo += "FAILED";
			} else {
				echo += "SUCCESSED:" + wftId;
			}
		} catch (Exception e) {
			if (wftId != null) {
				echo += "FAILED:" + wftId;
			} else {
				echo += "FAILED";
			}
			e.printStackTrace();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
		echo += "</body>";
		response.getWriter().print(echo);
		response.getWriter().flush();
		response.flushBuffer();
	}

}
