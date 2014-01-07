package com.lkh.cflow.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet implementation class TestScriptWeb
 */
public class TestTimeout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestTimeout() {
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
		JSONObject retObj = new JSONObject();
		request.setCharacterEncoding("UTF8");
		String ctx_string = request.getParameter("CTX");
		JSONParser parser = new JSONParser();
		JSONObject ctx;
		String ret = "";
		try {
			ctx = (JSONObject) parser.parse(ctx_string);
			Thread.sleep(100000);
			// 从Attachments集合中按变量名称取得变量的值。
			int idays = Integer.parseInt(ctx.get("days").toString());
			if (idays > 10)
				ret = "long";
			else
				ret = "short";
		} catch (Exception e) {
			ret = "onerror";
			retObj.put("error", e.getLocalizedMessage());
			e.printStackTrace();
		}
		retObj.put("RETURN", ret);
		// reason 在 模板中有，它的值将保留
		retObj.put("reason", "test value to change back to process attachment.");
		// ignored 在模板中没有，他的值将被忽略
		retObj.put("ignored", "this will be ignored.");

		ret = retObj.toJSONString();
		response.getWriter().print(ret);
		response.getWriter().flush();
		response.flushBuffer();
	}

}
