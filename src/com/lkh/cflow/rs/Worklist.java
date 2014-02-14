package com.lkh.cflow.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.WorkItemInfo;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

//Sets the path to base URL + /hello
@Path("/worklist")
public class Worklist {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(WorkData.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorklist(@QueryParam("token") String tokenString, @QueryParam("doer") String doer, @QueryParam("prcid") String prcId, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				JSONArray list = new JSONArray();
				// logger.debug("getWorklist: " +
				// CflowHelper.getRequestString(request));
				Object[] works = dbadmin.getWorklist(dev, doer, prcId);
				for (int i = 0; i < works.length; i++) {
					WorkItemInfo wii = (WorkItemInfo) works[i];
					JSONObject workitem = new JSONObject();
					workitem.put("NAME", wii.workName);
					workitem.put("PRCID", wii.prcId);
					workitem.put("NODEID", wii.nodeid);
					workitem.put("SESSID", wii.sessid);
					workitem.put("PRCNAME", wii.prcname);
					workitem.put("LIGHT", wii.lightURL);
					workitem.put("DELEGATERID", wii.delegaterid);
					workitem.put("DOER", doer);
					workitem.put("WFTID", wii.wftId);
					workitem.put("STARTBY", wii.startBy);
					workitem.put("TID", wii.tid);
					workitem.put("WORKNAME", wii.workName);
					workitem.put("WORKTITLE", wii.workTitle);
					workitem.put("STATUS", wii.status);
					workitem.put("PPID", wii.ppid);
					workitem.put("PPNODEID", wii.ppnodeid);
					workitem.put("PPSESSID", wii.ppsessid);
					list.add(workitem);
				}
				String ret = list.toJSONString();

				CacheControl cache = new CacheControl();
				cache.setNoCache(true);

				ResponseBuilderImpl builder = new ResponseBuilderImpl();
				builder.cacheControl(cache);
				builder.status(200);
				builder.entity(ret);
				Response r = builder.build();
				return r;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

}
