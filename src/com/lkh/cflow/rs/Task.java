package com.lkh.cflow.rs;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.lkh.cflow.Attachment;
import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.Taskto;
import com.lkh.cflow.Work;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/task")
public class Task {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(Delegation.class);

	@SuppressWarnings("rawtypes")
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response doTask(@FormParam("prcid") String prcId, @FormParam("nodeid") String nodeid, @FormParam("sessid") String sessid, @FormParam("option") String optionPicked, @FormParam("attachments") String ctx, @FormParam("token") String tokenString, @FormParam("actor") String actor,
			@Context HttpServletRequest rqs) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();

		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				if (optionPicked == null)
					optionPicked = CflowHelper.DEFAULT_OPTION;

				JSONObject taskInput = new JSONObject();

				JSONParser parser = new JSONParser();

				if (ctx != null && ctx.length() > 0) {
					/*
					 * JSONArray jarr = (JSONArray) parser.parse(ctx); if
					 * (jarr.size() > 0) { for (int i = 0; i < jarr.size(); i++)
					 * { JSONObject jobj = (JSONObject) jarr.get(i);
					 * attachmentsMap.put((String) jobj.get("NAME"), (String)
					 * jobj.get("VALUE")); } }
					 */
					taskInput = (JSONObject) parser.parse(ctx);
				}

				String ret = CflowHelper.prcManager.doTask(dev, dbadmin, actor, prcId, nodeid, sessid, optionPicked, taskInput);

				return Response.status(200).entity(ret).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response
	 * getTaskInfo(@QueryParam("tid") String tid, @QueryParam("token") String
	 * tokenString) { DbAdmin dbadmin = DbAdminPool.get();
	 * dbadmin.keepConnection(true); JSONObject ret = new JSONObject(); try {
	 * Developer dev = dbadmin.getDevByToken(tokenString); if (dev == null) {
	 * return Response.status(401).entity("Session failed").build(); } else {
	 * dbadmin.updateToken(dev);
	 * 
	 * String tzId = dev.tzId; WorkItemInfo wii = dbadmin.getWiiByTid(tid); if
	 * (wii == null) { throw new Exception("WorkiItemInfo " + tid + " is null");
	 * } JSONObject wii_info = new JSONObject(); wii_info.put("NAME",
	 * wii.workName); wii_info.put("PRCID", wii.prcId); wii_info.put("NODEID",
	 * wii.nodeid); wii_info.put("SESSID", wii.sessid); wii_info.put("PRCNAME",
	 * wii.prcname); wii_info.put("LIGHT", wii.lightURL);
	 * wii_info.put("DELEGATERID", wii.delegaterid); wii_info.put("DOER",
	 * wii.doer); wii_info.put("WFTID", wii.wftId); wii_info.put("STARTBY",
	 * wii.startBy); wii_info.put("TID", wii.tid); wii_info.put("WORKNAME",
	 * wii.workName); wii_info.put("WORKTITLE", wii.workTitle);
	 * wii_info.put("STATUS", wii.status); wii_info.put("PPID", wii.ppid);
	 * wii_info.put("PPNODEID", wii.ppnodeid); wii_info.put("PPSESSID",
	 * wii.ppsessid); ret.put("WII", wii_info);
	 * 
	 * ProcessDocument prcDoc =
	 * CflowHelper.prcManager.getProcessDocumentByID(dev, dbadmin, wii.prcId);
	 * if (prcDoc == null) { throw new Exception("get processdocument " +
	 * wii.prcId + " failed."); } ProcessT thePrc = prcDoc.getProcess();
	 * WorkflowDocument.Workflow theWorkflow =
	 * CflowHelper.wftManager.getInstanceWftByID(thePrc.getStartby(),
	 * wii.prcId); if (theWorkflow == null) { throw new
	 * Exception("get InstanceWftByID " + wii.prcId + " failed."); } //
	 * 根据sessid取当前的活动项。 Work theWork = CflowHelper.prcManager.getWork(thePrc,
	 * wii.sessid, CflowHelper.WORK_RUNNING); if (theWork == null) { throw new
	 * Exception("getWork prc:" + wii.prcId + "sessid:" + wii.sessid +
	 * "  failed."); } ret.put("WORK", CflowHelper.getWorkJSON(theWork,
	 * dev.tzId));
	 * 
	 * String workTitle = theWork.getTitle(); Map<String, String> tMap = new
	 * HashMap<String, String>(); if (workTitle.indexOf('$') >= 0) {
	 * Attachment[] temp = CflowHelper.prcManager.queryPrcAttachments(thePrc,
	 * "./work/log/attachment"); for (int i = 0; i < temp.length; i++) {
	 * tMap.put("\\$" + temp[i].getAttname() + "\\$", temp[i].getValue()); }
	 * temp = CflowHelper.prcManager.queryPrcAttachments(thePrc,
	 * "./attachment"); for (int i = 0; i < temp.length; i++) { tMap.put("\\$" +
	 * temp[i].getAttname() + "\\$", temp[i].getValue()); } for (Map.Entry entry
	 * : tMap.entrySet()) { workTitle =
	 * workTitle.replaceAll(entry.getKey().toString(),
	 * entry.getValue().toString()); } } ret.put("WFTNAME",
	 * thePrc.getWftname()); ret.put("INSTRUCTION",
	 * HtmlHelper.bbcode(HtmlHelper.removeHTML(workTitle),
	 * CflowHelper.isScriptSafe(thePrc.getWftid()))); ret.put("MATES",
	 * dbadmin.getAllUsers(dev)); ret.put("ACQS",
	 * CflowHelper.prcManager.getAcqStatus(thePrc, theWork, wii.doer,
	 * wii.delegaterid));
	 * 
	 * Attachment[] attachments =
	 * CflowHelper.prcManager.getWorkAttachmentTemplate(theWork); JSONArray
	 * js_attachments = new JSONArray(); for (int i = 0; i < attachments.length;
	 * i++) { JSONObject js_attach = new JSONObject(); js_attach.put("LABEL",
	 * attachments[i].getLabel()); js_attach.put("NAME",
	 * attachments[i].getAttname()); js_attach.put("TYPE",
	 * attachments[i].getType()); js_attach.put("VALUE",
	 * attachments[i].getValue()); js_attachments.add(js_attach); }
	 * 
	 * ret.put("ATTACHMENTS", js_attachments);
	 * 
	 * JSONArray js_options = new JSONArray(); String[] optionArray =
	 * CflowHelper.prcManager.getWorkOptions(theWork); for (int i = 0; i <
	 * optionArray.length; i++) { OptionString tmpOptS = new
	 * OptionString(optionArray[i]); JSONObject option = new JSONObject();
	 * option.put("VALUE", optionArray[i]); if (tmpOptS.getIsDefault())
	 * option.put("ISDEFAULT", "true"); else option.put("ISDEFAULT", "false");
	 * option.put("CLEANTEXT", tmpOptS.getCleanOption());
	 * js_options.add(option); } ret.put("OPTIONS", js_options); try { Work[]
	 * finishedWorks = CflowHelper.prcManager.getFinishedWorks(thePrc); // TODO
	 * SORT BY DATE JSONArray fworks = new JSONArray(); for (int i = 0; i <
	 * finishedWorks.length; i++) { JSONObject fwork = new JSONObject();
	 * fwork.put("NAME", finishedWorks[i].getName()); fwork.put("FINISHEDBY",
	 * finishedWorks[i].getCompletedBy()); fwork.put("FINISHEDAT",
	 * CflowHelper.showTime(finishedWorks[i].getCompletedAt(), dev.tzId));
	 * JSONArray attsArray = new JSONArray(); Attachment[] atts =
	 * CflowHelper.prcManager.getWorkLogAttachment(finishedWorks[i]); for (int j
	 * = 0; j < atts.length; j++) { JSONObject attobj = new JSONObject();
	 * attobj.put("LABEL", atts[j].getLabel()); attobj.put("TYPE",
	 * atts[j].getType()); attobj.put("VALUE", atts[j].getValue());
	 * attsArray.add(attobj); } fwork.put("ATTACHMENTS", attsArray);
	 * fwork.put("DECISION", finishedWorks[i].getDecision());
	 * 
	 * fworks.add(fwork); } ret.put("HISTORY", fworks); } catch
	 * (java.lang.ClassCastException e) { e.printStackTrace(); }
	 * 
	 * // History
	 * 
	 * // Node theNode = CflowHelper.wftManager.getNode(theWorkflow, //
	 * wii.nodeid); return Response.status(200).entity(ret).build();
	 * 
	 * } } catch (Exception e) { e.printStackTrace(); return
	 * Response.status(500).entity("Exception " +
	 * e.getLocalizedMessage()).build(); } finally { DbAdminPool.ret(dbadmin); }
	 * }
	 */
	@GET
	@Path("nodelog")
	@Produces(MediaType.TEXT_HTML)
	public Response getNodeLog(@QueryParam("prcid") String prcId, @QueryParam("nodeid") String nodeid, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		String ret = "";
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				StringBuffer sb = new StringBuffer();
				sb.append("<table>");
				Work[] works = CflowHelper.prcManager.getWorks(dev, dbadmin, prcId, nodeid);
				for (int k = 0; k < works.length; k++) {
					sb.append("<tr><td colspan='2'>").append(works[k].getTitle()).append("</td></tr>");
					sb.append("<tr><td>").append("Status").append("<td>").append(works[k].getStatus()).append("</td></tr>");
					sb.append("<tr><td>by</td><td>");
					if (works[k].getType().equals("task")) {
						Taskto[] tos = works[k].getTasktoArray();
						for (int m = 0; m < tos.length; m++) {
							sb.append(tos[m].getWhom());
						}
					} else {
						sb.append("System");
					}
					sb.append("</td></tr>");
					sb.append("<tr><td>Create at</td><td>").append(CflowHelper.showTime(works[k].getCreatedAt())).append("</td></tr>");
					if (works[k].getCompletedAt() != null) {
						sb.append("<tr><td>Complete at</td><td>").append(CflowHelper.showTime(works[k].getCompletedAt())).append("</td></tr>");
					}
					if (works[k].getType().equals("task")) {
						sb.append("<tr><td>Done by</td><td>").append(works[k].getCompletedBy()).append("</td></tr>");
						sb.append("<tr><td>Decision:</td><td>").append(works[k].getDecision()).append("</td></tr>");
						sb.append("<tr><td colspan=2>Attachments:</td></tr>");
						Attachment[] atts = works[k].getAttachmentArray();
						for (int j = 0; j < atts.length; j++) {
							sb.append("<tr><td>").append(atts[j].getLabel()).append("</td><td>").append(atts[j].getValue()).append("</td>");
						}

					} else {
						sb.append("<tr><td>Done by</td><td>").append("System").append("</td></tr>");
					}
				}
				ret = sb.toString();
				return Response.status(200).entity(ret).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}
}
