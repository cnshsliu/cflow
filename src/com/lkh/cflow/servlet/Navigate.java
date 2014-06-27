package com.lkh.cflow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Navigate
 */
public class Navigate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Navigate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF8");
		String toUrl = request.getParameter("to");
		StringBuffer sb = new StringBuffer();
		if (toUrl == null || toUrl.length() == 0) {
			toUrl = "/cflow/index.jsp";
		}
		
			String token = request.getParameter("token"); 
			if(token==null || token.equalsIgnoreCase("null")) 
				token=null;
			if(token == null){
				sb.append("/cflow/login.jsp?target=");
				sb.append(toUrl);
			}else{
				sb.append(toUrl).append("?token=").append(token);
			}
		response.sendRedirect(sb.toString());
		return;
	}

}
