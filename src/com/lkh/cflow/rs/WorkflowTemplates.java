package com.lkh.cflow.rs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.WftInfo;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/workflow_templates")
public class WorkflowTemplates {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkflowTemplates(@MatrixParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONArray wfts = new JSONArray();
				try {
					WftInfo[] userworkflows = dbadmin.getWftInfoByDeveloper(dev);
					for (int j = 0; j < userworkflows.length; j++) {
						JSONObject wft = new JSONObject();
						wft.put("wftid", userworkflows[j].wftId);
						wft.put("wftname", userworkflows[j].wftname);
						wfts.add(wft);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				String ret = wfts.toJSONString();

				return Response.status(200).entity(ret).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Path("own")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOwnWorkflowTemplates(@MatrixParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				JSONArray wfts = new JSONArray();
				try {
					WftInfo[] userworkflows = dbadmin.getWftInfoByDeveloper(dev);
					for (int j = 0; j < userworkflows.length; j++) {
						JSONObject wft = new JSONObject();
						wft.put("wftid", userworkflows[j].wftId);
						wft.put("wftname", userworkflows[j].wftname);
						wfts.add(wft);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				String ret = wfts.toJSONString();

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
