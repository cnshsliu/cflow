package com.lkh.cflow.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/workitem")
public class Workitem {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(Workitem.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkitem(@QueryParam("tid") String tid, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				String ret = "";
				/*
				 * ProcessDocument prcDoc =
				 * CflowHelper.prcManager.getProcessDocumentByID(dbadmin,
				 * prcId); ProcessT prc = prcDoc.getProcess(); //
				 * 根据sessid取当前的活动项。 Work theWork =
				 * CflowHelper.prcManager.getWork(prc, sessid); JSONObject jobj
				 * = CflowHelper.getWorkJSON(theWork, tzId); String ret =
				 * jobj.toJSONString();
				 * 
				 * WorkItemInfo wii = dbadmin.getWiiByTid(tid); JSONObject
				 * workitem = new JSONObject(); workitem.put("NAME",
				 * wii.workName); workitem.put("PRCID", wii.prcId);
				 * workitem.put("NODEID", wii.nodeid); workitem.put("SESSID",
				 * wii.sessid); workitem.put("PRCNAME", wii.prcname);
				 * workitem.put("LIGHT", wii.lightURL);
				 * workitem.put("DELEGATERID", wii.delegaterid);
				 * workitem.put("USERID", usrid); workitem.put("WFTID",
				 * wii.wftId); workitem.put("STARTBY", wii.startBy);
				 * workitem.put("TID", wii.tid); workitem.put("WORKNAME",
				 * wii.workName); workitem.put("WORKTITLE", wii.workTitle);
				 * workitem.put("STATUS", wii.status); ret =
				 * workitem.toJSONString();
				 */

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
