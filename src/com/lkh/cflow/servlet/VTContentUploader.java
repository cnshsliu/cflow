package com.lkh.cflow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.Ctool;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

public class VTContentUploader extends HttpServlet {
	private static final long serialVersionUID = -2201219701121037195L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		String tokenString = null;
		String vtname = null;
		String vtcontent = null;
		DbAdmin dbadmin = DbAdminPool.get();

		dbadmin.keepConnection(true);
		String ret = "";
		try {
			tokenString = (String) request.getParameter("token");
			vtname = (String) request.getParameter("vtname");
			vtcontent = (String) request.getParameter("vtcontent");
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				ret = "Session failed.";
			} else {
				JSONObject data = new JSONObject();
				data.put("VTNAME", vtname);
				Ctool.validateInput(data);

				dbadmin.addVtToDeveloper(dev, vtname);
				String vtPath = CflowHelper.storageManager.getVtPath(dev, vtname);
				CflowHelper.storageManager.upload(vtPath, vtcontent, "text/html");

				ret = "Success";
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = "Failed: " + e.getLocalizedMessage();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}
}