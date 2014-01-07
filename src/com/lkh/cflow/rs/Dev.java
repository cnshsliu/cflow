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

import com.lkh.cflow.Developer;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /login
@Path("/dev")
public class Dev {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(Dev.class);

	// /rest/login post过来id和accesskey, 找到用户，则返回tokenString.
	// 无此用户，则返回错误 401： WebUser failed。
	@POST
	@Path("login")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response login(@FormParam("accessId") String devId, @FormParam("accessKey") String acsk, @Context HttpServletRequest rqs) throws IOException {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			Developer dev = dbadmin.getDev(devId, acsk);
			if (dev == null) {
				logger.info(devId + " login failed");
				return Response.status(401).entity("FAILED").build();
			} else {

				String token = TokenAdmin.newToken(dev.id, rqs.getRemoteAddr());
				return Response.status(200).entity(token).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Path("logout")
	@Produces(MediaType.TEXT_PLAIN)
	public Response logout(@QueryParam("token") String token, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			TokenAdmin.removeToken(token, rqs.getRemoteAddr());
			return Response.status(200).entity(token).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	// /rest/login/sesskey，如果sesskey合法且sesstime在半小时内，返回该用户，否则，返回null.

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getDeveloper(@QueryParam("token") String token, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			Developer dev = dbadmin.getDevByToken(token, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject usr = new JSONObject();
				usr.put("ID", dev.id);
				usr.put("NAME", dev.name);
				usr.put("EMAIL", dev.email);
				usr.put("LANG", dev.lang);
				return Response.status(200).entity(usr.toJSONString()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

}
