package com.lkh.cflow.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lkh.cflow.Developer;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * Servlet implementation class RLogin
 */
public class DoLogin extends HttpServlet {
	private static final long serialVersionUID = 1003L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DoLogin() {
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
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		String ip = request.getRemoteAddr();

		String id = request.getParameter("id");
		String acsk = request.getParameter("acsk");
		String target = request.getParameter("target");
		if (target == null)
			target = "/cflow/wft.jsp";

		DbAdmin dbadmin = DbAdminPool.get();
		synchronized (dbadmin) {
			try {
				Developer dev = dbadmin.getDev(id, acsk);

				if (dev == null) {
					response.sendRedirect("login.jsp");
					return;
				} else {
					String token = TokenAdmin.newToken(dev.id);
					target = target + "?token=" + token;
					response.sendRedirect(target);
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendRedirect("login.jsp");
				return;
			} finally {
				DbAdminPool.ret(dbadmin);
			}
		}
	}
}
