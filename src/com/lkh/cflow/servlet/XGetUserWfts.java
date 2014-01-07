package com.lkh.cflow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lkh.cflow.WftInfo;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * Servlet implementation class GetUserWfts
 */
public class XGetUserWfts extends HttpServlet {
	private static final long serialVersionUID = 1014L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XGetUserWfts() {
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
		String ret = "";
		request.setCharacterEncoding("UTF8");
		HttpSession session = request.getSession();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		ret += "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		ret += "<wfts>";
		if (session.getAttribute("DEVID") != null) {
			synchronized (session) {
				String devid = (String) session.getAttribute("DEVID");
				DbAdmin dbadmin = (DbAdmin) session.getAttribute("DBADMIN");
				boolean oldFlag = dbadmin.keepConnection(true);
				try {
					WftInfo[] userworkflows = dbadmin.getWftInfoByDeveloper(devid);
					for (int j = 0; j < userworkflows.length; j++) {
						String wftId = userworkflows[j].wftId;
						// WorkflowDocument.Workflow workflow =
						// CflowHelper.wftManager.getTemplateByID(wftId);
						ret += "<wft>";
						ret += "<wftId>" + wftId + "</wftId>";
						// ret += "<wftname>" + workflow.getName() +
						// "</wftname>";
						ret += "<wftname>" + "TODO HERE" + "</wftname>";
						ret += "</wft>";
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					DbAdminPool.ret(dbadmin);
				}
			}
		}
		ret += "</wfts>";
		response.getWriter().print(ret);
		response.getWriter().flush();
		response.flushBuffer();
	}

}
