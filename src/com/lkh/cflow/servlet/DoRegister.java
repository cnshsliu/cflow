package com.lkh.cflow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.Ctool;
import com.lkh.cflow.Developer;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * Servlet implementation class Register
 */
public class DoRegister extends HttpServlet {

	private static final long serialVersionUID = 1004L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DoRegister() {
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

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String devId = request.getParameter("devid");
			String devname = request.getParameter("devname");
			String accesskey1 = request.getParameter("accesskey1");
			String accesskey2 = request.getParameter("accesskey2");
			String email = request.getParameter("email");
			String lang = request.getParameter("userLang");

			int usrid_length = devId.length();
			if (devId == null || usrid_length < 4 || usrid_length > 24) {
				request.setAttribute("register.message", "user id length error. (4-24)");
				request.getRequestDispatcher("reg.jsp").forward(request, response);
				return;
			}

			if (devname == null || devname.length() < 2 || devname.length() > 20) {
				request.setAttribute("register.message", "user name length error, (2-20)");
				request.getRequestDispatcher("reg.jsp").forward(request, response);
				return;
			}

			if (accesskey1 == null || accesskey1.length() < 4 || accesskey1.length() > 30) {
				request.setAttribute("register.message", "accesskey length error. (4-30)");
				request.getRequestDispatcher("reg.jsp").forward(request, response);
				return;
			}

			if (accesskey2 == null || accesskey2.length() < 4 || accesskey2.length() > 30) {
				request.setAttribute("register.message", "accesskey length error. (4-30)");
				request.getRequestDispatcher("reg.jsp").forward(request, response);
				return;
			}

			if (email == null || email.length() < 6 || email.length() > 48) {
				request.setAttribute("register.message", "user email length error. (6-48)");
				request.getRequestDispatcher("reg.jsp").forward(request, response);
				return;
			}

			if (!accesskey1.equals(accesskey2)) {
				request.setAttribute("register.message", "accesskey do not match");
				request.getRequestDispatcher("reg.jsp").forward(request, response);
				return;
			}
			try {

				JSONObject data = new JSONObject();
				data.put("DEVID", devId);
				data.put("DEVNAME", devname);
				data.put("ACCESSKEY", accesskey1);
				data.put("EMAIL", email);
				data.put("LANG", lang);
				Ctool.validateInput(data);

				devId = devId.toLowerCase();
				Developer aDev = dbadmin.getDevById(devId);
				if (aDev != null) {
					request.setAttribute("register.message", "Already occupied.");
					request.getRequestDispatcher("reg.jsp").forward(request, response);
					return;
				} else {
					dbadmin.createDev(devId, devname, accesskey1, email, lang);
					try {
						CflowHelper.wftManager.copyModel(devId, dbadmin, "PutGiraffe", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_AND", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_OR", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_DoneByAnyone", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_DoneByAll", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_DoneByCondition", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_JAVA", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_WEB", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_JAVASCRIPT", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_RoleReference", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "LeaveApplication", "en_US");
						CflowHelper.wftManager.copyModel(devId, dbadmin, "Demo_SJYJ", "en_US");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					request.setAttribute("register.message", "Developer registered successfully.");
					request.getRequestDispatcher("login.jsp").forward(request, response);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("register.message", "Exception: " + e.getLocalizedMessage());
				request.getRequestDispatcher("reg.jsp").forward(request, response);
				return;
			}
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

}
