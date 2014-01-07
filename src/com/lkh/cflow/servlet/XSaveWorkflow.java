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
 * Servlet implementation class XSaveWorkflow
 */
public class XSaveWorkflow extends HttpServlet {
	private static final long serialVersionUID = 1016L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XSaveWorkflow() {
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
		String prcId = (String) request.getParameter("prcid");
		String devId = (String) request.getParameter("devid");
		String wft = (String) request.getParameter("wft");
		String wftname = (String) request.getParameter("wftname");
		String echo = "<result>";
		DbAdmin dbadmin = DbAdminPool.get();
		boolean oldFlag = dbadmin.keepConnection(true);
		try {
			prcId = CflowHelper.wftManager.saveInstanceWft(wft, prcId, devId);
			if (prcId == null) {
				echo += "FAILED";
			} else {
				echo += "SUCCESSED:" + prcId;
			}
		} catch (Exception e) {
			if (prcId != null) {
				echo += "FAILED:" + prcId;
			} else {
				echo += "FAILED";
			}
			e.printStackTrace();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
		echo += "</result>";
		response.getWriter().print(echo);
		response.getWriter().flush();
		response.flushBuffer();
	}

}
