package com.lkh.cflow.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.ProcessDocument;
import com.lkh.cflow.ProcessT;
import com.lkh.cflow.Work;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/work_data")
public class WorkData {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(WorkData.class);

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_XML)
	public Response getTeams(@PathParam("id") String prcId, @MatrixParam("sessid") String sessid, @MatrixParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				ProcessDocument prcDoc = CflowHelper.prcManager.getProcessDocument(dev, dbadmin, prcId);
				ProcessT prc = prcDoc.getProcess();
				// 根据sessid取当前的活动项。
				Work theWork = CflowHelper.prcManager.getWork(prc, sessid);
				JSONObject jobj = CflowHelper.getWorkJSON(theWork);
				String ret = jobj.toJSONString();

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
