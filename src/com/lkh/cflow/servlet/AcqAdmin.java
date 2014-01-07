package com.lkh.cflow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.Developer;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * Servlet implementation class AcqAdmin
 */
public class AcqAdmin extends HttpServlet {
	private static final long serialVersionUID = 1001L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AcqAdmin() {
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
		String prcId = request.getParameter("prcid");
		String sessid = request.getParameter("sessid");
		String action = request.getParameter("action");
		String token = request.getParameter("token");

		String delegaterid = request.getParameter("dlg");
		String usrid = request.getParameter("usrid");

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			Developer dev = dbadmin.getDevByToken(token, request.getRemoteAddr());
			if (dev == null) {
				request.setAttribute("upload.message", "Session failed");
			} else {
				if (action.equalsIgnoreCase("acq")) {
					CflowHelper.prcManager.acquireTask(dev.id, dbadmin, prcId, sessid, usrid);
				} else {
					CflowHelper.prcManager.unAcquireTask(dev.id, dbadmin, prcId, sessid, usrid);
				}

				response.flushBuffer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}
}
