package com.lkh.cflow;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.lkh.cflow.db.DbAdmin;

public class HtmlHelper {
	DbAdmin dbadmin = null;
	Locale locale = null;
	TextHelper textHelper = null;
	String usrid = null;

	public HtmlHelper(DbAdmin da, Locale locale, TextHelper textHelper, String usrid) {
		super();
		this.dbadmin = da;
		this.locale = locale;
		this.textHelper = textHelper;
		this.usrid = usrid;
	}

	public void init(DbAdmin da, Locale locale, TextHelper textHelper, String usrid) {
		this.dbadmin = da;
		this.locale = locale;
		this.textHelper = textHelper;
		this.usrid = usrid;
	}

	public String selectMate(User theUser, String selectName, Vector<User> mates) {
		StringBuffer sb = new StringBuffer();
		sb.append("<select name=\"").append(selectName).append("\">");
		try {
			for (int i = 0; i < mates.size(); i++) {
				User aUser = (User) mates.elementAt(i);
				sb.append("<option value=\"").append(aUser.id).append("\">").append(aUser.id).append("[").append(aUser.name).append("]").append("</option>");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		sb.append("</select>");
		return sb.toString();
	}

	public String formAdhocTasks(Work theWork, User theUser, boolean isMobileVersion, Vector<User> mates) {
		StringBuffer sb = new StringBuffer();
		if (theWork.getAllowAdhoc()) {
			sb.append("<DIV id=\"adhocTasks\" class=\"adhocTaskDIV\" style=\"display: none;\">").append("<table>");
			boolean oddrow = true;
			String rowClass = "oddrow";
			for (int i = 0; i < 10; i++) {
				int j = i + 1;
				if (oddrow)
					rowClass = "oddrow";
				else
					rowClass = "evenrow";

				if (!isMobileVersion) {
					sb.append("<tr class=\"" + rowClass + "\">").append("<td>").append(j).append(". ").append(textHelper.getText("showtask.adhoc.user")).append(selectMate(theUser, "adhocMat_" + i, mates)).append("</td>").append("<td> ").append(textHelper.getText("showtask.adhoc.role")).append(
							"<input name='adhocRole_" + i + "'>").append("</td>").append("<td>").append(textHelper.getText("showtask.adhoc.willdo")).append("<input type=\"text\" name=\"adhoc_").append(i).append("\"></td>").append("</tr>");
				} else {
					sb.append("<tr class=\"" + rowClass + "\"><td>").append("<table width=\"100%\">").append("<tr><td>").append(j).append(". ").append(textHelper.getText("showtask.adhoc.user")).append(" </td><td align=\"right\">").append(selectMate(theUser, "adhocMat_" + i, mates)).append(
							"</td></tr>").append("<tr><td>").append(textHelper.getText("showtask.adhoc.role")).append(" </td><td align=\"right\">").append("<input name='adhocRole_" + i + "'>").append("</td></tr>").append("<tr><td>").append(textHelper.getText("showtask.adhoc.willdo")).append(
							" </td><td align=\"right\"><input type=\"text\" name=\"adhoc_").append(i).append("\"></td></tr>").append("</table>").append("</td></tr>");
				}

				oddrow = !oddrow;
			}
			sb.append("</table></DIV>");
		} else {
			sb.append("");
		}

		return sb.toString();
	}

	public String history(DbAdmin dbadmin, ProcessT thePrc, String tzId) {
		StringBuffer sb = new StringBuffer();
		sb.append("<table width='100%'>");
		Boolean evenRow = true;
		try {
			Work[] finishedWorks = CflowHelper.prcManager.getFinishedWorks(thePrc);
			// TODO SORT BY DATE
			for (int i = 0; i < finishedWorks.length; i++) {
				sb.append(" <tr> <td class='").append(evenRow ? "evenrow" : "oddrow").append("'>").append("<table width='100%'>").append(" <tr> <td colspan=2> ").append(" <table width='100%'>").append(" <tr><td class='rowTitle'>").append(finishedWorks[i].getName()).append("</td></tr>").append(
						" <tr><td>by: ").append("<img src='/cflow/userIcons/usrIcon.png'>").append(finishedWorks[i].getCompletedBy()).append("</td></tr>").append(" <tr><td>at: ").append(CflowHelper.showTime(finishedWorks[i].getCompletedAt(), tzId)).append("</td></tr>").append(" </table>").append(
						" </td> </tr>");
				Attachment[] atts = CflowHelper.prcManager.getWorkLogAttachment(finishedWorks[i]);
				for (int j = 0; j < atts.length; j++) {
					sb.append(" <tr> <td nowrap class='rowTitle'>").append(atts[j].getLabel()).append("</td>");
					if (atts[j].getType().equals("url")) {
						sb.append("<td width='100%'>");
						String urlString = atts[j].getValue();
						if ((urlString.endsWith(".gif") || urlString.endsWith(".GIF") || urlString.endsWith(".jpg") || urlString.endsWith(".JPG")) && atts[j].getAttname().indexOf(" /i") > 0) {
							sb.append("<IMG SRC='").append(urlString).append("'>");
						} else {
							sb.append("<a href='").append(atts[j].getValue()).append("' target='_blank'>");
							sb.append(atts[j].getValue());
							sb.append("</a>");
						}
						sb.append("</td>");
					} else {
						sb.append("<td width='100%'>").append(atts[j].getValue()).append("</td>");
					}
					sb.append("</tr>");
				}
				String decision = finishedWorks[i].getDecision();
				sb.append("<tr> <td class='rowTitle'>Decision</td> <td nowrap>").append(decision).append("</td> </tr>");
				sb.append("</table>");
				sb.append(" </td> </tr>");
				evenRow = !evenRow;
			}
		} catch (java.lang.ClassCastException e) {
			e.printStackTrace();
		}
		sb.append("</table>");

		return sb.toString();
	}

	public String formChangeParticipate(ProcessT thePrc, WorkflowDocument.Workflow theWorkflow, Node theNode, User theUser) throws SQLException {
		StringBuffer sb = new StringBuffer();
		String[] rtc = {};
		if (theNode.getAllowRoleChange()) {
			String roleToChange = theNode.getRoleToChange();
			if (roleToChange.equals("all")) {
				String qe = "./node/taskto[@type='role'][@whom!='starter']";
				Taskto[] tasktos = CflowHelper.prcManager.queryTaskto(theWorkflow, qe);
				ArrayList<String> listarr = new ArrayList<String>();
				for (int i = 0; i < tasktos.length; i++) {
					listarr.add(tasktos[i].getWhom());
				}
				if (listarr.size() > 0) {
					rtc = listarr.toArray(rtc);
				}

			} else {
				rtc = StringUtils.split(roleToChange, ',');
				for (int i = 0; i < rtc.length; i++) {
					rtc[i] = StringUtils.trim(rtc[i]);
				}
			}
			Vector<String> tmpV = new Vector<String>();
			for (int i = 0; i < rtc.length; i++) {
				if (!tmpV.contains(rtc[i]))
					tmpV.add(rtc[i]);
			}
			sb.append("<table><tr><td  class=\"cfLowBorderLine\"> <div class=\"cfBodyHeader\">").append(textHelper.getText("showtask.assign")).append("</td></tr> <tr><td>");
			sb.append("<table>").append("<tr><th>").append(textHelper.getText("showtask.assign.role")).append("</th><th>").append(textHelper.getText("showtask.assign.participate")).append("</th>");
			for (int i = 0; i < tmpV.size(); i++) {
				sb.append("<tr><td>").append(tmpV.elementAt(i));
				sb.append("<input type='hidden' name='rtc_").append(i).append("' value='").append(tmpV.elementAt(i)).append("'>");
				sb.append("</td>");
				sb.append("<td>");
				// sb.append("<input type=\"Button\" onClick=\"javascript:ChangeRoleParticipate(").append(i).append(");\">");
				// sb.append("<DIV ID='PARTICIPATE_NAME_").append(i).append("'>");
				StringBuffer participate_ids = new StringBuffer();
				String qe = "./team/role[@name='" + tmpV.elementAt(i) + "']";
				Role[] roles = CflowHelper.prcManager.queryTeamRoles(thePrc, qe);
				for (int j = 0; j < roles.length; j++) {
					if (j > 0) {
						// sb.append(",");
						participate_ids.append(",");
					}
					// User theUser = CflowHelper.getUserByIdInCache(dbadmin,
					// roles[j].getValue());
					// sb.append(theUser.name);
					participate_ids.append(roles[j].getValue());
				}
				// sb.append("</DIV>");
				sb.append("<DIV ID='PARTICIPATE_IDS_").append(i).append("'>");
				sb.append("<input type='Text' name='participate_ids_").append(i).append("' value='").append(participate_ids.toString()).append("'>");
				sb.append("</DIV>");
				sb.append("</td>").append("<td><DIV ID='CHOOSER_").append(i).append("'>&nbsp;</DIV></td></tr>");
			}
			sb.append("</table>").append("<input type=\"hidden\" name=\"rtc_count\" value=\"").append(tmpV.size()).append("\">");
			sb.append(" </td></tr> </table>");

			return sb.toString();

		} else {
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	public String getExtraFormStartWorkflow(WorkflowDocument.Workflow theWorkflow, String usrid, JSONObject ctx) {
		String ret = "";
		String customizer = theWorkflow.getCustomizer();
		if (!StringUtils.isBlank(customizer)) {
			Class<Customizer> custClass;
			try {
				custClass = (Class<Customizer>) Class.forName(customizer);
				Customizer theCustomizer = (Customizer) custClass.newInstance();
				Method theMethod = custClass.getDeclaredMethod("extraFormStartWorkflow", JSONObject.class);
				ret = (String) theMethod.invoke(theCustomizer, ctx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;

	}

	@SuppressWarnings("unchecked")
	public String getExtraFormShowTask(Work theWork, String usrid, JSONObject ctx) {
		String ret = "";
		String customizer = theWork.getCustomizer();
		if (!StringUtils.isBlank(customizer)) {
			Class<Customizer> custClass;
			try {
				custClass = (Class<Customizer>) Class.forName(customizer);
				Customizer theCustomizer = (Customizer) custClass.newInstance();
				Method theMethod = custClass.getDeclaredMethod("extraFormShowTask", JSONObject.class);
				ret = (String) theMethod.invoke(theCustomizer, ctx);
			} catch (Exception e) {
				ret = e.getLocalizedMessage();
			}
		}

		return ret;

	}

	public static String removeHTML(String htmlString) {
		// Remove HTML tag from java String
		String noHTMLString = htmlString.replaceAll("\\<.*?\\>", "");

		// Remove Carriage return from java String
		noHTMLString = noHTMLString.replaceAll("\r", "<br/>");

		// Remove New line from java string and replace html break
		noHTMLString = noHTMLString.replaceAll("\n", " ");
		noHTMLString = noHTMLString.replaceAll("\'", "&#39;");
		noHTMLString = noHTMLString.replaceAll("\"", "&quot;");
		return noHTMLString;
	}

	public static String bbcode(String text, boolean isScriptSafe) {

		Map<String, String> bbMap = new HashMap<String, String>();

		bbMap.put("\\[b\\](.+?)\\[/b\\]", "<strong>$1</strong>");

		bbMap.put("\\[i\\](.+?)\\[/i\\]", "<span style='font-style:italic;'>$1</span>");

		bbMap.put("\\[u\\](.+?)\\[/u\\]", "<span style='text-decoration:underline;'>$1</span>");

		bbMap.put("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>");

		bbMap.put("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>");

		bbMap.put("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>");

		bbMap.put("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>");

		bbMap.put("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>");

		bbMap.put("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>");

		bbMap.put("\\[quote\\](.+?)\\[/quote\\]", "<blockquote>$1</blockquote>");

		bbMap.put("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>");

		bbMap.put("\\[ul\\](.+?)\\[/ul\\]", "<ul>$1</ul>");

		bbMap.put("\\[li\\](.+?)\\[/li\\]", "<li>$1</li>");

		bbMap.put("\\[p=(.+?),(.+?)\\](.+?)\\[/p\\]", "<p style='text-indent:$1px;line-height:$2%;'>$3</p>");

		bbMap.put("\\[center\\](.+?)\\[/center\\]", "<div align='center'>$1</div>");

		bbMap.put("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align='$1'>$2");

		bbMap.put("\\[color=(.+?)\\](.+?)\\[/color\\]", "<span style='color:$1;'>$2</span>");

		bbMap.put("\\[size=(.+?)\\](.+?)\\[/size\\]", "<span style='font-size:$1;'>$2</span>");

		bbMap.put("\\[img\\](.+?)\\[/img\\]", "<img src='$1' />");

		bbMap.put("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width='$1' height='$2' src='$3' />");

		bbMap.put("\\[email\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$1</a>");

		bbMap.put("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$2</a>");

		bbMap.put("\\[url\\](.+?)\\[/url\\]", "<a href='$1'>$1</a>");

		bbMap.put("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href='$1'>$2</a>");

		bbMap.put("\\[urb=(.+?)\\](.+?)\\[/urb\\]", "<a href='$1' target='_blank'>$2</a>");

		bbMap.put("\\[urw=(.+?),(.+?)\\](.+?)\\[/urw\\]", "<a href='$1' target='$2'>$3</a>");

		bbMap.put("\\[youtube\\](.+?)\\[/youtube\\]", "<object width='640' height='380'><param name='movie' value='http://www.youtube.com/v/$1'></param><embed src='http://www.youtube.com/v/$1' type='application/x-shockwave-flash' width='640' height='380'></embed></object>");

		bbMap.put("\\[video\\](.+?)\\[/video\\]", "<video src='$1' />");

		if (isScriptSafe)
			bbMap.put("\\[script\\](.+?)\\[/script\\]", "<script language='JavaScript'>$1</script>");
		else
			bbMap.put("\\[script\\](.+?)\\[/script\\]", "$1");

		for (Map.Entry entry : bbMap.entrySet()) {

			text = text.replaceAll(entry.getKey().toString(), entry.getValue().toString());

		}

		return text;

	}

	public String getAcqMenu(ProcessT prc, Work theWork, User theUser, String delegaterid) {
		return getAcqMenu(prc, theWork, theUser, delegaterid, false);
	}

	public String getAcqMenu(ProcessT prc, Work theWork, User theUser, String delegaterid, boolean isMobile) {
		String acq = "/cflow/Acq";
		String ret = "";
		String acqs = CflowHelper.prcManager.getAcqStatus(prc, theWork, theUser.id, delegaterid);
		if (acqs.equals(CflowHelper.ACQ_NOT_ACQUIRABLE)) {
			ret = "<!-- NOT ACQUIRABLE -->";
		} else {
			if (acqs.equals(CflowHelper.ACQ_ALREADY_ACQUIRED)) {
				String url = acq + "?prcid=" + prc.getId() + "&nodeid=" + theWork.getNodeid() + "&sessid=" + theWork.getSessid() + "&action=unacq";
				if (delegaterid != null)
					url += "&dlg=" + delegaterid;
				url += "&usrid=" + theUser.id;
				if (isMobile)
					url += "&isMobile=" + (isMobile ? "true" : "false");
				ret += "<a href=\"" + url + "\">" + textHelper.getText("showtask.unacquire") + "</a>";
			} else {
				if (acqs.equals(CflowHelper.ACQ_CAN_ACQUIRE)) {
					String url = acq + "?prcid=" + prc.getId() + "&nodeid=" + theWork.getNodeid() + "&sessid=" + theWork.getSessid() + "&action=acq";
					if (delegaterid != null)
						url += "&dlg=" + delegaterid;
					url += "&usrid=" + theUser.id;
					if (isMobile)
						url += "&isMobile=" + (isMobile ? "true" : "false");
					ret += "<a href=\"" + url + "\">" + textHelper.getText("showtask.acquire") + "</a>";
				} else {
					ret = "<!-- HIDE dotaskform -->" + "<input type=\"hidden\" name=\"hideControl\" id=\"hideControl\" " + " value=\"";
					if (isMobile) {
						ret += "/cflow/Worklist.jsp?isMobile=true&usrid=" + theUser.id;
					} else {
						ret += "/cflow/Worklist.jsp?usrid=" + theUser.id;
					}
					ret += "\">";
				}
			}
		}

		return ret;
	}

	public String formDelegation(ProcessT thePrc, Work theWork, User theUser, String delegaterid, Vector mates) {
		StringBuffer sb = new StringBuffer();
		int optionNumber = 0;
		String acqs = CflowHelper.prcManager.getAcqStatus(thePrc, theWork, theUser.id, delegaterid);
		if (theWork.getAllowDelegate() && (acqs.equals(CflowHelper.ACQ_NOT_ACQUIRABLE) || acqs.equals(CflowHelper.ACQ_CAN_ACQUIRE))) {
			sb.append("<table> <tr> <td nowrap><input type=\"Button\" name=\"delegate\" value=\"").append(textHelper.getText("showtask.delegateto")).append("\" onClick=\"javascript:startDelegate();\" class=\"lsb\">");
			sb.append(" <div style=\"display: none\"><input type=\"text\" name=\"delegateflag\" id=\"delegateflag\" value=\"0\"></div>");
			sb.append(" <select name=\"delegatee\">");
			for (int u = 0; u < mates.size(); u++) {
				User aUser = (User) mates.elementAt(u);
				if (!aUser.id.equals(theUser.id)) {
					optionNumber++;
					sb.append("<option value=\"").append(aUser.id).append("\">").append(aUser.name).append("</option>");
				}
			}
			sb.append("</select></td> </tr> </table>");
			if (optionNumber == 0) {
				sb = new StringBuffer();
				sb.append(" <div style=\"display: none\"><input type=\"text\" name=\"delegateflag\" id=\"delegateflag\" value=\"0\"></div>");
			}
		} else {
			sb.append(" <div style=\"display: none\"><input type=\"text\" name=\"delegateflag\" id=\"delegateflag\" value=\"0\"></div>");
		}
		return sb.toString();
	}

	public String formWorkAttachments(Work theWork) {
		StringBuffer sb = new StringBuffer();
		Attachment[] attachments = CflowHelper.prcManager.getWorkAttachmentTemplate(theWork);
		if (attachments.length > 0) {
			sb.append(" <table border=\"0\" width=\"100%\">");
			sb.append("<tr> <td class=\"th\">").append(textHelper.getText("showtask.attachments")).append("</td> </tr>");
			for (int i = 0; i < attachments.length; i++) {
				sb.append("<tr><td nowrap>").append(attachments[i].getLabel()).append("</td></tr>");
				sb.append("<tr><td><input type=\"hidden\" name=\"attname_").append(i).append("\" value=\"").append(attachments[i].getAttname()).append("\">");
				if (attachments[i].getType().equals("text")) {
					sb.append(" <textarea rows=\"3\" style=\"width:100%\" name=\"attvalue_").append(i).append("\" id=\"attvalue_").append(i).append("\">").append(attachments[i].getValue()).append("</textarea>");
				} else if (attachments[i].getType().equals("url")) {
					sb.append(" <input type=\"text\" name=\"attvalue_").append(i).append("\" id=\"attvalue_").append(i).append("\" value=\"").append(attachments[i].getValue()).append("\">");
				} else if (attachments[i].getType().equals("int")) {
					sb.append(" <input type=\"text\" name=\"attvalue_").append(i).append("\" id=\"attvalue_").append(i).append("\" value=\"").append(attachments[i].getValue()).append("\">");
				} else if (attachments[i].getType().equals("float")) {
					sb.append(" <input type=\"text\" name=\"attvalue_").append(i).append("\" id=\"attvalue_").append(i).append("\" value=\"").append(attachments[i].getValue()).append("\">");
				} else if (attachments[i].getType().equals("netext")) {
					sb.append(" <input type=\"text\" name=\"attvalue_").append(i).append("\" id=\"attvalue_").append(i).append("\" value=\"").append(attachments[i].getValue()).append("\">");
				}
				sb.append(" </td> </tr>");
			}
			sb.append(" </table>");
		}
		sb.append("<input type=\"hidden\" name=\"numberOfAttachments\" value=\"").append(attachments.length).append("\">");
		return sb.toString();
	}

	public String formWorkHeader(ProcessT thePrc, Work theWork, User theUser, String delegaterid, boolean isMobile) {
		String workTitle = theWork.getTitle();
		// Replace context value
		Map<String, String> tMap = new HashMap<String, String>();
		if (workTitle.indexOf('$') >= 0) {
			Attachment[] temp = CflowHelper.prcManager.queryPrcAttachments(thePrc, "./work/log/attachment");
			for (int i = 0; i < temp.length; i++) {
				tMap.put("\\$" + temp[i].getAttname() + "\\$", temp[i].getValue());
			}
			temp = CflowHelper.prcManager.queryPrcAttachments(thePrc, "./attachment");
			for (int i = 0; i < temp.length; i++) {
				tMap.put("\\$" + temp[i].getAttname() + "\\$", temp[i].getValue());
			}
			for (Map.Entry entry : tMap.entrySet()) {
				workTitle = workTitle.replaceAll(entry.getKey().toString(), entry.getValue().toString());
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append(" <table> <tr><td colspan=\"2\">");
		sb.append(" <table width=\"100%\"> <tr> <td  class=\"cfLowBorderLine\">").append(theWork.getName()).append(" </td>");
		sb.append(" <td  class=\"cfLowBorderLine\" align=\"right\">").append(getAcqMenu(thePrc, theWork, theUser, delegaterid, isMobile)).append(" </td>");
		sb.append(" </tr> </table>");
		sb.append(" </td></tr> <tr> <td colspan=\"2\"> ").append(textHelper.getText("showtask.process.inwhich")).append(" [").append(thePrc.getWftname()).append("] ").append(textHelper.getText("showtask.process.startat")).append(CflowHelper.showTime(theWork.getCreatedAt())).append(
				" </td> </tr> <tr> <td colspan=\"2\">").append(textHelper.getText("showtask.instruction")).append("</td> </tr> <tr> <td colspan=\"2\">").append(bbcode(removeHTML(workTitle), CflowHelper.isScriptSafe(thePrc.getWftid()))).append("</td> </tr> </table>");
		// 使用Velocity模板后，下面的方法可以洗洗睡了
		sb.append(getExtraFormShowTask(theWork, theUser.id, CflowHelper.getWorkContext(thePrc, theWork)));
		return sb.toString();
	}

	public String formOptions(ProcessT thePrc, Work theWork, User theUser, boolean isMobile, String delegaterid) {
		boolean showDoneButton = true;
		StringBuffer sb = new StringBuffer();
		boolean showOptionsWithButton = CflowHelper.cfg.getBoolean("showOptionsWithButton", true);
		sb.append("<table> <!--  Start of Routine Option -->");
		String[] optionArray = CflowHelper.prcManager.getWorkOptions(theWork);
		if (optionArray.length > 0) {
			sb.append("<tr><td colspan='2' class='th'>").append(textHelper.getText("showtask.yourdecision"));
			if (showOptionsWithButton) {
				sb.append("<input type='hidden' name='option_1'>");
				showDoneButton = false;
			}
			sb.append("</td></tr>");
			sb.append("<tr><td colspan='2'>");
			for (int i = 0; i < optionArray.length; i++) {
				OptionString tmpOptS = new OptionString(optionArray[i]);
				if (showOptionsWithButton) {
					sb.append("<input type=\"button\" class=\"lsb\" value=\"").append(tmpOptS.getCleanOption()).append("\" ");
					sb.append(" onClick=\"javascript:this.form.option_1.value='").append(optionArray[i]).append("'; this.form.submit();\">");
				} else {
					sb.append("<input type='radio' name='option_1' value='").append(optionArray[i]).append("' ").append((tmpOptS.getIsDefault() ? "checked" : "")).append(">").append(tmpOptS.getCleanOption());
				}
			}
			sb.append("</td></tr>");
		}
		sb.append("<!--  End of Routine Option -->");
		if (showDoneButton) {
			sb.append("<tr> <td colspan=2 class=\"th\">").append(textHelper.getText("showtask.clicktodone")).append("</td> </tr>");
		}
		sb.append(" <tr> <td colspan=\"2\"> <input type=\"hidden\" name=\"nodeid\" value=\"").append(theWork.getNodeid()).append("\"> ");
		sb.append(" <input type=\"hidden\" name=\"sessid\" value=\"").append(theWork.getSessid()).append("\">");
		sb.append(" <input type=\"hidden\" name=\"prcId\" value=\"").append(thePrc.getId()).append("\"> ");
		sb.append(" <input type=\"hidden\" name=\"workName\" value=\"").append(theWork.getName()).append("\">");
		sb.append(" <input type=\"hidden\" name=\"usrid\" value=\"").append(theUser.id).append("\">");
		sb.append(" <input type=\"hidden\" name=\"isMobile\" value=\"").append(isMobile ? "true" : "false").append("\">");
		sb.append(" <input type=\"hidden\" name=\"dlg\" value=\"").append(delegaterid).append("\"> ");
		if (showDoneButton) {
			sb.append("<input type=\"button\" value=\"").append(textHelper.getText("showtask.done")).append("\" onClick=\"submitForm(this.form);\" class=\"doneButton\">");
		}
		sb.append(" </td> </tr> <tr><td colspan=\"2\">");
		sb.append(" <table><tr>");
		if (theWork.getAllowAdhoc())
			sb.append(" <td><a href=\"javascript:toggleAdHoc();\"><DIV id=\"adhocTaskControl\">").append(textHelper.getText("showtask.adhoclink")).append("</DIV></a></td>");
		sb.append(" <td><a href=\"javascript:toggleHistory();\"><DIV id=\"historyControl\">").append(textHelper.getText("showtask.historylink")).append("</DIV></a></td>");
		sb.append(" </tr></table>");
		sb.append(" <script langauge=\"JavaScript\">");
		sb.append(" var adhocHidden = true;");
		sb.append(" function toggleAdHoc()");
		sb.append(" { if(document.getElementById('adhocTasks') != null) {document.getElementById('adhocTasks').style.display=\"inline\";} if(document.getElementById('history')!=null) {document.getElementById('history').style.display=\"none\";}}");
		sb.append(" function toggleHistory()");
		sb.append(" { if(document.getElementById('history') !=null){ document.getElementById('history').style.display=\"inline\";} if(document.getElementById('adhocTasks')!=null) {document.getElementById('adhocTasks').style.display=\"none\";} }");
		sb.append(" </script>");
		sb.append(" </td></tr>");
		sb.append(" </table>");

		return sb.toString();
	}

	public static String pickLang(HttpServletRequest request) {
		String user_lang = CflowHelper.userLang(request);
		StringBuffer sb = new StringBuffer();
		sb.append("<select name='userLang' id='userLang'>").append("<option value='en-US' ").append(user_lang.equals("en-US") ? "selected='selected'" : "").append(">English</option>").append("<option value='zh-CN' ").append(user_lang.equals("zh-CN") ? "selected='selected'" : "").append(
				">Chinese Simplified</option>").append("<option value='zh-TW' ").append(user_lang.equals("zh-TW") ? "selected='selected'" : "").append(">Chinese Traditional</option>").append("<option value='fr-FR' ").append(user_lang.equals("fr-FR") ? "selected='selected'" : "").append(
				">French</option>").append("<option value='ja-JP' ").append(user_lang.equals("ja-JP") ? "selected='selected'" : "").append(">Japanese</option>").append("</select>");

		return sb.toString();
	}

	public static String pickTimezone(HttpServletRequest request, String defaultTzid) {
		StringBuffer sb = new StringBuffer();
		sb.append(" <select name='tzId' id='tzId'>");
		SimpleDateFormat formatter = new SimpleDateFormat(CflowHelper.cfg.getString("dateFormat", "yyyy-MM-dd HH:mm"));
		for (int i = 0; i < CflowHelper.timeZoneIds.length; i++) {
			TimeZone utz = TimeZone.getTimeZone(CflowHelper.timeZoneIds[i]);
			formatter.setTimeZone(utz);
			Calendar ucal = Calendar.getInstance(utz);

			sb.append("<option value='").append(CflowHelper.timeZoneIds[i]).append("'");
			if (CflowHelper.timeZoneIds[i].equals(defaultTzid))
				sb.append(" selected");
			sb.append(">").append("[").append(CflowHelper.timeZoneIds[i]).append("]  ").append(formatter.format(ucal.getTime())).append("</option>");
		}
		sb.append(" </select>");
		return sb.toString();
	}

	public static boolean useFrame(HttpServletRequest request) {
		String ufs = request.getParameter("uf");
		if (ufs == null)
			ufs = "false";
		boolean useFrame = ufs.equalsIgnoreCase("TRUE");

		return useFrame;
	}

	public static String workArea() {
		StringBuffer sb = new StringBuffer();
		sb.append("<DIV id=\"st_right\" class=\"rightStyle\">").append("<div id=\"workAreaTitle\"></div><div id=\"st_close\"><a href=\"javascript:closeWorkarea();\"><img src='/cflow/images/close.png' width='24' height='24' border='0'></a></div>").append(
				"<div id=\"st_toggle\"><a href=\"javascript:maximizeWorkarea();\"><img src='/cflow/images/maximize.jpg' width='24' height='24' border='0'></a></div>").append("<DIV id=\"CLEAR1\" style=\"clear:both;\"><!-- spacer --></DIV>")
		// .append("<DIV id=\"st_content\" class=\"rightContent\"> rightpanel</DIV>")
				.append("<iframe id=\"workArea\" name=\"workArea\" width=\"100%\" height=\"100%\" frameborder=\"1\"  > </iframe>").append("</DIV>");

		return sb.toString();
	}

	public static String workArea_start() {
		return "<div id=\"subcontent\">";
	}

	public static String workArea_end() {
		StringBuffer sb = new StringBuffer();
		sb.append("</div><Script language=\"Javascript\" type=\"text/javascript\">").append("var st_content = parent.document.getElementById(\"st_content\");").append("var st_right= parent.document.getElementById(\"st_right\");").append("if(st_content!= null){").append(
				"st_content.innerHTML = document.getElementById(\"subcontent\").innerHTML;").append("}").append("if(st_right!= null){").append("st_right.style.display=\"block\";").append("}").append("</Script>");

		return sb.toString();
	}

	public static String getShowTaskUrl(WorkItemInfo wii, String sid) {
		return getShowTaskUrl("/cflow/ShowTask.jsp", wii, sid);
	}

	public static String getShowTaskUrl(String page, WorkItemInfo wii, String sid) {
		StringBuffer sb = new StringBuffer();
		sb.append(page).append("?tid=").append(wii.tid).append("&sid=").append(sid);
		if (wii.delegaterid != null)
			sb.append("&dlg=").append(wii.delegaterid);

		String ret = sb.toString();
		/*
		 * try { ret = java.net.URLEncoder.encode(ret, "UTF8"); } catch
		 * (UnsupportedEncodingException e) { e.printStackTrace(); }
		 */
		return ret;
	}

}
