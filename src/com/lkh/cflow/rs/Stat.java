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

import org.json.simple.JSONObject;

import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/stat")
public class Stat {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatistics(@QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();

		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject ret = dbadmin.statistic(dev, null, null, null);

				return Response.status(200).entity(ret.toJSONString()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

}
