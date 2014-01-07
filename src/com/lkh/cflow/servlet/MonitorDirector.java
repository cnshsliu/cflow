package com.lkh.cflow.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.ProcessDocument;
import com.lkh.cflow.ProcessT;
import com.lkh.cflow.Work;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * Servlet implementation class MonitorDirector
 */
public class MonitorDirector extends HttpServlet {
	private static final long serialVersionUID = 1008L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MonitorDirector() {
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
		String token = request.getParameter("token");
		try {
			String dev = TokenAdmin.getDevByToken(token, request.getRemoteAddr());
			if (dev == null) {
				request.setAttribute("upload.message", "Session failed");
			} else {
				String prcId = request.getParameter("prcid");
				Object lock = null;
				synchronized (CflowHelper.lruDocLock) {
					lock = CflowHelper.lruDocLock.get(prcId);
					if (lock == null) {
						lock = new Object();
						CflowHelper.lruDocLock.put(prcId, lock);
					}

				}
				synchronized (lock) {
					String nodeid = request.getParameter("nodeid");
					String action = request.getParameter("action");
					if ((prcId == null || prcId.length() < 1) || (action == null || action.length() < 1)) {
						response.getWriter().write("prcId can not be null");
						return;
					}
					ProcessDocument prcDoc = null;
					ProcessT prc = null;
					try {
						String path = CflowHelper.storageManager.getPrcPath(dev, prcId);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						CflowHelper.storageManager.download(path, baos);
						ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
						prcDoc = ProcessDocument.Factory.parse(bais);
						baos.close();
						bais.close();
						prc = prcDoc.getProcess();
					} catch (Exception e) {
						e.printStackTrace();
						response.getWriter().write(e.getLocalizedMessage());
						return;
					}
					if (action.equals("suspendProcess")) {
						if (prc.getStatus().equals(CflowHelper.PROCESS_RUNNING)) {
							prc.setStatus(CflowHelper.PROCESS_SUSPENDED);
							CflowHelper.prcManager.saveProcessDoc(dev, prcDoc, prcId);
							try {
								dbadmin.updateProcessLog(prc.getId(), prc.getStartby(), prc.getStartat(), prc.getEndat(), prc.getStatus());
								dbadmin.suspendProcessTaskto(prc.getId());
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					} else if (action.equals("cancelProcess")) {
						prc.setStatus(CflowHelper.PROCESS_CANCELED);
						prc.setEndat(Calendar.getInstance());
						CflowHelper.prcManager.saveProcessDoc(dev, prcDoc, prcId);
						try {
							dbadmin.removeProcessTaskto(prc.getId());
							dbadmin.updateProcessLog(prc.getId(), prc.getStartby(), prc.getStartat(), prc.getEndat(), prc.getStatus());
							dbadmin.removeProcessTaskto(prc.getId());
						} catch (SQLException e) {
							e.printStackTrace();
						}

					} else if (action.equals("resumeProcess")) {
						if (prc.getStatus().equals(CflowHelper.PROCESS_SUSPENDED)) {
							prc.setStatus(CflowHelper.PROCESS_RUNNING);
							CflowHelper.prcManager.saveProcessDoc(dev, prcDoc, prcId);
							try {
								dbadmin.resumeProcessTaskto(prc.getId());
								dbadmin.updateProcessLog(prc.getId(), prc.getStartby(), prc.getStartat(), prc.getEndat(), prc.getStatus());
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}

					} else if (action.equals("suspendWork")) {
						if (nodeid == null || nodeid.length() < 1) {
							response.sendRedirect("/cflow/MonitorProcess.jsp");
							return;
						}
						String queryExpression = "./work[@nodeid='" + nodeid + "'][@roundid='" + prc.getRoundid() + "']";
						Work[] works = CflowHelper.prcManager.queryWorks(prc, queryExpression);
						for (int i = 0; i < works.length; i++) {
							if (works[i].getStatus().equals(CflowHelper.WORK_RUNNING)) {
								works[i].setStatus(CflowHelper.WORK_SUSPENDED);
								dbadmin.suspendWorkTaskto(works[i].getSessid());
							}
						}
						CflowHelper.prcManager.saveProcessDoc(dev, prcDoc, prcId);
					} else if (action.equals("resumeWork")) {
						if (nodeid == null || nodeid.length() < 1) {
							response.sendRedirect("/cflow/MonitorProcess.jsp");
							return;
						}
						String queryExpression = "./work[@nodeid='" + nodeid + "'][@roundid='" + prc.getRoundid() + "']";
						Work[] works = CflowHelper.prcManager.queryWorks(prc, queryExpression);
						for (int i = 0; i < works.length; i++) {
							if (works[i].getStatus().equals(CflowHelper.WORK_SUSPENDED)) {
								works[i].setStatus(CflowHelper.WORK_RUNNING);
								dbadmin.resumeWorkTaskto(works[i].getSessid());
								CflowHelper.prcManager.restoreUserAssociation(dev, dbadmin, prc, works[i]);
							}
						}
						CflowHelper.prcManager.saveProcessDoc(dev, prcDoc, prcId);

					}
				}
				response.getWriter().write("Done");
				return;
			}
		} catch (Exception ex) {
			response.getWriter().write(ex.getLocalizedMessage());
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}
}
