package com.lkh.cflow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.web.WebContext;

/**
 * Servlet implementation class WftMode
 */
public class WftMode extends HttpServlet {
	private static final long serialVersionUID = 1010L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WftMode() {
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
		request.setCharacterEncoding("UTF8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		WebContext webCtx = new WebContext(request);
		String wftMode = CflowHelper.wftMode(request);
		if (wftMode.equals("todo")) {
			response.sendRedirect(webCtx.getUrl("/cflow/newView.jsp"));
		} else {
			response.sendRedirect(webCtx.getUrl("/cflow/WftDesigner.jsp"));
		}
	}

}
