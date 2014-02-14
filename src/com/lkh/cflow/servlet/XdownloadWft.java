package com.lkh.cflow.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * Servlet implementation class XdownloadWftService
 */
public class XdownloadWft extends HttpServlet {
	private static final long serialVersionUID = 1012L;
	private Logger logger = Logger.getLogger(XdownloadWft.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XdownloadWft() {
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
		String tokenString = (String) request.getParameter("token");
		String wftId = (String) request.getParameter("wftid");
		if (tokenString == null || wftId == null)
			return;

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				logger.error("Failed to get Developer");
				return;
			} else {
				OutputStream outstream = response.getOutputStream();
				try {
					String path = CflowHelper.storageManager.getWftPath(dev, wftId);
					CflowHelper.storageManager.download(path, outstream);
					outstream.flush();
					outstream.close();
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
				}// end catch
				finally {
					outstream.flush();
					outstream.close();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		} finally {
			DbAdminPool.ret(dbadmin);
		}

	}

}
