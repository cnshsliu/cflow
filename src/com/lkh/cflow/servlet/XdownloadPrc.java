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
 * Servlet implementation class XdownloadPrc
 */
public class XdownloadPrc extends HttpServlet {
	private static final long serialVersionUID = 1011L;
	private Logger logger = Logger.getLogger(XdownloadPrc.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XdownloadPrc() {
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

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				logger.error("Failed to get Developer");
				return;
			} else {
				String prcId = (String) request.getParameter("prcid");
				Object lock = null;
				synchronized (CflowHelper.lruDocLock) {
					lock = CflowHelper.lruDocLock.get(prcId);
					if (lock == null) {
						lock = new Object();
						CflowHelper.lruDocLock.put(prcId, lock);
					}

				}
				synchronized (lock) {
					OutputStream outstream = response.getOutputStream();
					try {
						String path = CflowHelper.storageManager.getPrcPath(dev, prcId);
						CflowHelper.storageManager.download(path, outstream);
						outstream.flush();
						outstream.close();
					}// end try
					catch (Exception ex) {
						logger.error("Download process file failed, devid:" + dev + " prcId:" + prcId);
						ex.printStackTrace();
						if (CflowHelper.cfg.getBoolean("file.prc.notfound.delete", false))
							CflowHelper.prcManager.deleteProcess(dev, dbadmin, prcId);

					}// end catch
					finally {
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());

		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

}
