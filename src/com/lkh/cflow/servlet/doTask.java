package com.lkh.cflow.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.WorkItemInfo;
import com.lkh.cflow.adhocEntry;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;
import com.lkh.cflow.exception.CflowException;

/**
 * Servlet implementation class doTask
 */
public class doTask extends HttpServlet {
	private static final long serialVersionUID = 1005L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public doTask() {
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
		String tokenString = request.getParameter("token");
		String doer = request.getParameter("doer");
		String tid = request.getParameter("tid");

		if (doer == null && tid == null)
			request.setAttribute("doTask.message", "Either doer or tid should present");
		else {
			DbAdmin dbadmin = DbAdminPool.get();
			dbadmin.keepConnection(true);
			try {
				String dev = TokenAdmin.getDevByToken(tokenString, request.getRemoteAddr());
				if (dev == null) {
					request.setAttribute("doTask.message", "Session failed");
				}

				if (tid != null) {
					WorkItemInfo wii = dbadmin.getWiiByTid(tid);
					if (wii != null) {
						doer = wii.doer;
					}
				}
				// 取网页数据
				// 当前进程编号
				String prcId = (String) request.getParameter("prcid");
				// 当前模板节点编号
				String nodeid = (String) request.getParameter("nodeid");
				// 当前活动的sessid.
				String sessid = (String) request.getParameter("sessid");
				// 当前活动的委托者用户代码。无委托时赋值"SYSTEM";
				String delegaterid = (String) request.getParameter("dlg");
				// 用户对可选项所做的选择
				String optionPicked = (String) request.getParameter("option_1");
				// 取委托标示
				String delegateFlag = (String) request.getParameter("delegateflag");
				// 取被委托者代码
				String delegatee = (String) request.getParameter("delegatee");
				// 取角色的个数
				String s_numberOfRole = (String) request.getParameter("number_of_roles");
				// 取参数的个数
				String s_numberOfAttachments = (String) request.getParameter("numberOfAttachments");

				String pageDoTaskPost = "/cflow/Worklist.jsp";
				String pageDoTaskError = "/cflow/doTaskErr.jsp";
				String pageShowError = "/cflow/showError.jsp";
				String pageShowTask = "/cflow/ShowTask.jsp";

				// 赋值：角色个数、参数个数、委托标示的缺省值
				if (s_numberOfRole == null)
					s_numberOfRole = "0";
				if (s_numberOfAttachments == null)
					s_numberOfAttachments = "0";
				if (delegateFlag == null)
					delegateFlag = "0";

				// Acquire Status()
				// 取当前活动的“ACQUIRE状态。
				String acqs = "UNKNOWN"; // CflowHelper.prcManager.getAcqStatus(prc,
											// theWork, doer, delegaterid);
				// 如果这个活动已经被他人ACQUIRE
				if (acqs.equals(CflowHelper.ACQ_BY_OTHERS)) {
					response.sendRedirect(pageShowError + "&error=" + "ACQUIRED BY OTHERS");
				} else {// 否则，可以完成这个活动
						// 如果设置了委托标志
					/*
					 * try { // new DelegateManager().delegateOneTask(dbadmin,
					 * // usrid, // delegatee, prcId, nodeid, sessid); //
					 * 将该工作委托出去 CflowHelper.prcManager.delegateOneTask(dev,
					 * dbadmin, prcId, prc.getWftid(), theWork, doer,
					 * delegatee); } catch (Exception e) { e.printStackTrace();
					 * } response.sendRedirect(pageDoTaskPost);
					 */

					// Collect attachments
					JSONObject attachmentsMap = new JSONObject();
					/*
					 * Attachment[] attachments =
					 * CflowHelper.prcManager.getWorkAttachmentTemplate
					 * (theWork); for (int i = 0; i < attachments.length; i++) {
					 * String attname = attachments[i].getAttname();
					 * attachmentsMap.put(attname,
					 * request.getParameter(attname)); }
					 */
					Vector<String> v = new Vector<String>();
					v.add("btn_submit");
					v.add("delegateflag");
					v.add("prcid");
					v.add("token");
					v.add("sessid");
					v.add("tid");
					v.add("option");
					v.add("nodeid");
					v.add("option_1");
					for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
						String kn = e.nextElement();
						if (!v.contains(kn)) {
							attachmentsMap.put(kn, request.getParameter(kn));
						}

					}

					// Collect adhoc活动项定义
					adhocEntry[] adhocs = {};
					ArrayList<adhocEntry> list = new ArrayList<adhocEntry>();
					for (int i = 0; i < 10; i++) {
						String adhoc = request.getParameter("adhoc_" + i);
						String adhocMat = request.getParameter("adhocMat_" + i);
						String adhocRole = request.getParameter("adhocRole_" + i);

						if (!StringUtils.isBlank(adhoc)) {
							list.add(new adhocEntry(adhoc, adhocMat, adhocRole));
						}
					}
					if (list.size() > 0) {
						adhocs = list.toArray(adhocs);
						// 插入adhoc活动
						CflowHelper.prcManager.insertAdhoc(dev, prcId, nodeid, adhocs);
					}

					// Collect role to changes
					/*
					 * HashMap<String, String[]> rtcs = new HashMap<String,
					 * String[]>(); for (int i = 0; i < 100; i++) { String rtc =
					 * request.getParameter("rtc_" + i); if
					 * (!StringUtils.isBlank(rtc)) { String participate_ids =
					 * request.getParameter("participate_ids_" + i); // if
					 * (StringUtils.isBlank(participate_ids)) { //
					 * participate_ids = prc.getStartby(); // } String[]
					 * participates = StringUtils.split(participate_ids, ',');
					 * rtcs.put(rtc, participates); } else break; }
					 */
					// TODO:
					// Change Team member should change into cf_team
					// CflowHelper.prcManager.updateTeamMembers(prc, rtcs);

					try {
						// 完成这个活动
						String ret = CflowHelper.prcManager.doTask(dev, dbadmin, doer, prcId, nodeid, sessid, optionPicked, attachmentsMap);
					} catch (CflowException ex) {
						response.sendRedirect(pageDoTaskError);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				request.setAttribute("doTask.message", "Error: " + ex.getLocalizedMessage());
			} finally {
				DbAdminPool.ret(dbadmin);
			}
		}

		// request.getRequestDispatcher("wl.jsp?token=" + token + "&doer" +
		// doer).forward(request, response);
		response.sendRedirect("wl.jsp?token=" + tokenString + "&doer=" + doer);

	}
}
