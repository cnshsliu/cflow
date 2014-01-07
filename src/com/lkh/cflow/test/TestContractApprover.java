package com.lkh.cflow.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class TestContractApprover
 */
public class TestContractApprover extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestContractApprover() {
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
		String ctx_string = request.getParameter("CTX");
		JSONParser parser = new JSONParser();
		JSONObject ctx;
		String approver = "";
		try {
			ctx = (JSONObject) parser.parse(ctx_string);

			// 从Attachments集合中按变量名称取得变量的值。
			String cvs = ctx.get("contract_value").toString();
			int cv = Integer.valueOf(cvs).intValue();

			if (cv < 100)
				approver = "manager";
			else if (cv < 1000)
				approver = "director";
			else
				approver = "gm";
		} catch (ParseException e) {
			approver = "error";
			e.printStackTrace();
		}
		JSONObject retObj = new JSONObject();
		retObj.put("RETURN", approver);
		retObj.put("reason", "test value to change back to process attachment.");

		response.getWriter().print(retObj.toJSONString());
		response.getWriter().flush();
		response.flushBuffer();
	}

}
