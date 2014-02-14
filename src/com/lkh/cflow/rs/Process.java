package com.lkh.cflow.rs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.lkh.cflow.Attachment;
import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.Ctool;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.PrcInfo;
import com.lkh.cflow.ProcessDocument;
import com.lkh.cflow.ProcessT;
import com.lkh.cflow.Role;
import com.lkh.cflow.Work;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/process")
public class Process {
	static Logger logger = Logger.getLogger(Process.class);
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	// This method is called if TEXT_PLAIN is request
	@SuppressWarnings("unchecked")
	@GET
	public Response getProcessInfo(@QueryParam("prcid") String prcId, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();

		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				ProcessDocument prcDoc = CflowHelper.prcManager.getProcessDocument(dev, dbadmin, prcId);
				ProcessT prc = prcDoc.getProcess();
				JSONObject prcJSON = new JSONObject();
				prcJSON.put("PRCID", prc.getId());
				prcJSON.put("PBO", prc.getPbo());
				prcJSON.put("ROUNDID", prc.getRoundid());
				prcJSON.put("STARTBY", prc.getStartby());
				prcJSON.put("STARTAT", CflowHelper.showTime(prc.getStartat()));
				prcJSON.put("ENDAT", CflowHelper.showTime(prc.getEndat()));
				prcJSON.put("STATUS", prc.getStatus());
				prcJSON.put("WFTID", prc.getWftid());
				prcJSON.put("WFTNAME", prc.getWftname());
				prcJSON.put("CUSTOMIZER", prc.getCustomizer());
				prcJSON.put("TEAMID", prc.getTeamid());
				prcJSON.put("PARENTPROCESSID", prc.getParentProcessId());
				prcJSON.put("PARENTPROCESSNODEID", prc.getParentProcessNodeId());
				prcJSON.put("PARENTPROCESSSESSID", prc.getParentProcessSessId());
				JSONArray roles = new JSONArray();
				Role[] allRoles = CflowHelper.prcManager.queryPrcRoles(prc, "./role");
				for (int i = 0; i < allRoles.length; i++) {
					Role theRole = allRoles[i];
					JSONObject jobj = CflowHelper.getRoleJSON(theRole);
					roles.add(jobj);
				}
				prcJSON.put("ROLES", roles);

				JSONArray works = new JSONArray();
				Work[] allWorks = CflowHelper.prcManager.queryWorks(prc, "./work");
				for (int i = 0; i < allWorks.length; i++) {
					Work theWork = allWorks[i];
					JSONObject jobj = CflowHelper.getWorkJSON(theWork);
					works.add(jobj);
				}
				prcJSON.put("WORKS", works);
				// Bug fix
				// 取流程数据应包括取Context attachment.
				JSONArray ctx = new JSONArray();
				Attachment[] allAttachments = CflowHelper.prcManager.queryPrcAttachments(prc, "./attachment");
				for (int i = 0; i < allAttachments.length; i++) {
					Attachment theAttachment = allAttachments[i];
					JSONObject jobj = CflowHelper.getAttachmentJSON(theAttachment);
					ctx.add(jobj);
				}
				prcJSON.put("CTX", ctx);
				// End of bug fix.
				String ret = JSONValue.toJSONString(prcJSON);
				return Response.status(200).entity(ret).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
			dbadmin = null;
		}
	}

	// This method is called if TEXT_PLAIN is request
	@SuppressWarnings("unchecked")
	@GET
	@Path("variables")
	public Response getProcessVariables(@QueryParam("prcid") String prcId, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				ProcessDocument prcDoc = CflowHelper.prcManager.getProcessDocument(dev, dbadmin, prcId);
				ProcessT prc = prcDoc.getProcess();

				String queryExpression = "./work[@roundid='" + prc.getRoundid() + "']/log/attachment";
				Attachment[] atts = queryPrcAttachments(prc, queryExpression);
				JSONObject retJSON = new JSONObject();
				for (int i = 0; i < atts.length; i++) {
					if (!StringUtils.isBlank(atts[i].getAttname())) {
						retJSON.put(atts[i].getAttname(), atts[i].getValue());
					}
				}
				String ret = JSONValue.toJSONString(retJSON);
				return Response.status(200).entity(ret).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
			dbadmin = null;
		}
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("doc")
	public Response getProcessDoc(@QueryParam("prcid") String prcId, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		Object lock = null;
		synchronized (CflowHelper.lruDocLock) {
			lock = CflowHelper.lruDocLock.get(prcId);
			if (lock == null) {
				lock = new Object();
				CflowHelper.lruDocLock.put(prcId, lock);
			}

		}
		synchronized (lock) {
			DbAdmin dbadmin = DbAdminPool.get();
			dbadmin.keepConnection(true);
			try {
				String dev = TokenAdmin.getDevByToken(tokenString);
				if (dev == null) {
					return Response.status(401).entity("Session failed").build();
				} else {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					String entityString = "";
					try {
						String prcPath = CflowHelper.storageManager.getPrcPath(dev, prcId);
						CflowHelper.storageManager.download(prcPath, baos);
						entityString = baos.toString("UTF-8");
						baos.close();
					}// end try
					catch (Exception e) {

					}// end catch

					return Response.status(200).entity(entityString).build();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
			} finally {
				DbAdminPool.ret(dbadmin);
				dbadmin = null;
			}
		}
	}

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProcesses(@QueryParam("status") String status, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				JSONArray list = new JSONArray();
				PrcInfo[] prcinfos = dbadmin.getPrcInfoByStatus(dev, status);
				for (int i = 0; i < prcinfos.length; i++) {
					PrcInfo pinfo = prcinfos[i];
					JSONObject info = new JSONObject();
					info.put("NAME", pinfo.name);
					info.put("PRCID", pinfo.prcId);
					info.put("STARTBY", pinfo.startBy);
					info.put("WFTID", pinfo.wftId);
					info.put("WORKFLOWID", pinfo.workflowid);
					info.put("STATUS", pinfo.status);
					list.add(info);
				}
				String ret = JSONValue.toJSONString(list);

				return Response.status(200).entity(ret).build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Path("allbyuser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProcessesByStatus(@QueryParam("status") String status, @QueryParam("token") String tokenString, @QueryParam("usrid") String usrid, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				JSONArray list = new JSONArray();
				PrcInfo[] prcinfos = dbadmin.getPrcUserAssociations(usrid, status);
				for (int i = 0; i < prcinfos.length; i++) {
					PrcInfo pinfo = prcinfos[i];
					JSONObject info = new JSONObject();
					info.put("NAME", pinfo.name);
					info.put("PRCID", pinfo.prcId);
					info.put("STARTBY", pinfo.startBy);
					info.put("WFTID", pinfo.wftId);
					info.put("WORKFLOWID", pinfo.workflowid);
					info.put("STATUS", pinfo.status);
					list.add(info);
				}
				String ret = JSONValue.toJSONString(list);

				return Response.status(200).entity(ret).build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createProcess(@FormParam("wftid") String wftId, @FormParam("startby") String startBy, @FormParam("teamid") String teamid, @FormParam("instancename") String instanceName, @FormParam("ctx") String ctx, @FormParam("token") String tokenString, @Context HttpServletRequest rqs)
			throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject data = new JSONObject();
				data.put("STARTBY", startBy);
				data.put("INSTANCENAME", instanceName);
				Ctool.validateInput(data);

				JSONObject ctxObj = new JSONObject();
				if (ctx != null) {
					try {
						JSONParser parser = new JSONParser();
						ctxObj = (JSONObject) parser.parse(ctx);
					} catch (ParseException e) {
						logger.error(e.getLocalizedMessage());
						e.printStackTrace();
					}
				}
				String prcId = CflowHelper.prcManager.startWorkflow(dev, dbadmin, wftId, teamid, startBy, instanceName, ctxObj);
				if (prcId.startsWith("ERROR"))
					return Response.status(404).entity(prcId).build();
				else
					return Response.status(200).entity(prcId).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
			dbadmin = null;
		}
	}

	@POST
	@Path("/byname")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createProcessByName(@FormParam("wftname") String wftName, @FormParam("startby") String startBy, @FormParam("teamname") String teamName, @FormParam("instancename") String instanceName, @FormParam("ctx") String ctx, @FormParam("token") String tokenString,
			@Context HttpServletRequest rqs) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject data = new JSONObject();
				data.put("STARTBY", startBy);
				data.put("INSTANCENAME", instanceName);
				data.put("WFTNAME", wftName);
				data.put("TEAMNAME", teamName);
				Ctool.validateInput(data);

				JSONObject ctxObj = new JSONObject();
				if (ctx != null) {
					try {
						JSONParser parser = new JSONParser();
						ctxObj = (JSONObject) parser.parse(ctx);
					} catch (ParseException e) {
						logger.error(e.getLocalizedMessage());
						e.printStackTrace();
					}
				}
				String prcId = CflowHelper.prcManager.startWorkflowByName(dev, dbadmin, wftName, teamName, startBy, instanceName, ctxObj);
				if (prcId.startsWith("ERROR"))
					return Response.status(404).entity(prcId).build();
				else
					return Response.status(200).entity(prcId).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
			dbadmin = null;
		}
	}

	@DELETE
	public Response deleteProcess(@QueryParam("prcid") String prcId, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				try {
					CflowHelper.prcManager.deleteProcess(dev, dbadmin, prcId);
				} catch (Exception ex) {
					logger.error("Delete process id [" + prcId + "] failed: " + ex.getLocalizedMessage());
					ex.printStackTrace();
				}

				return Response.status(200).entity(prcId).build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
			dbadmin = null;
		}
	}

	@PUT
	@Path("status")
	public Response changeStatus(@QueryParam("prcid") String prcId, @QueryParam("token") String tokenString, @QueryParam("nodeid") String nodeid, @QueryParam("action") String action, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				if (action.equals("suspendProcess")) {
					CflowHelper.prcManager.suspendProcess(dev, dbadmin, prcId);
				} else if (action.equalsIgnoreCase("resumeProcess")) {
					CflowHelper.prcManager.resumeProcess(dev, dbadmin, prcId);
				} else if (action.equalsIgnoreCase("cancelProcess")) {
					CflowHelper.prcManager.cancelProcess(dev, dbadmin, prcId);
				} else if (action.equalsIgnoreCase("suspendWork")) {
					CflowHelper.prcManager.suspendWork(dev, dbadmin, prcId, nodeid);
				} else if (action.equalsIgnoreCase("resumeWork")) {
					CflowHelper.prcManager.resumeWork(dev, dbadmin, prcId, nodeid);
				}

				return Response.status(200).entity(prcId).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
			dbadmin = null;
		}
	}

	private Attachment[] queryPrcAttachments(ProcessT prc, String queryString) {
		Attachment[] atts = {};
		Object[] objs = prc.selectPath(queryString);
		if (objs == null || objs.length == 0) {
			return atts;
		} else {
			return (Attachment[]) objs;
		}
	}

}
