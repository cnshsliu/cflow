package com.lkh.cflow.rs;

import java.io.IOException;

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

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.lkh.cflow.Ctool;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.User;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /login
@Path("/user")
public class WebUser {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(WebUser.class);

	@POST
	@Path("new")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addUser(@FormParam("token") String tokenString, @FormParam("identity") String identity, @FormParam("username") String username, @FormParam("email") String email, @FormParam("lang") String lang, @FormParam("notify") String notify, @Context HttpServletRequest rqs)
			throws IOException {
		if (username.equals("starter")) {
			return Response.status(500).entity("User name should not be 'starter'").build();
		}
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject data = new JSONObject();
				data.put("IDENTITY", identity);
				data.put("USERNAME", username);
				data.put("EMAIL", email);
				data.put("LANG", lang);
				data.put("NOTIFY", notify);
				Ctool.validateInput(data);

				dbadmin.createUser(dev, identity, username, email, lang, notify);

				return Response.status(200).entity(identity).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@DELETE
	public Response deleteUser(@QueryParam("identity") String identity, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();

		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				dbadmin.removeUserByIdentity(dev, identity);
				return Response.status(200).entity(identity).build();
			}
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
	public Response getUserByIdentity(@QueryParam("token") String tokenString, @QueryParam("identity") String identity, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				User theUser = dbadmin.getUserByIdentity(dev, identity);
				JSONObject usr = new JSONObject();
				usr.put("ID", theUser.id);
				usr.put("IDENTITY", theUser.identity);
				usr.put("NAME", theUser.name);
				usr.put("EMAIL", theUser.email);
				usr.put("LANG", theUser.lang);
				return Response.status(200).entity(usr.toJSONString()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(@QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONArray list = dbadmin.getAllUsers(dev);
				String ret = JSONValue.toJSONString(list);

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
	@Path("actors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActors(@QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONArray list = dbadmin.getActors(dev);
				String ret = JSONValue.toJSONString(list);

				return Response.status(200).entity(ret).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@PUT
	@Path("update")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updateUser(@FormParam("token") String tokenString, @FormParam("identity") String identity, @FormParam("username") String username, @FormParam("email") String email, @FormParam("tzid") String tzId, @FormParam("lang") String lang, @FormParam("notify") String notify,
			@Context HttpServletRequest rqs) throws IOException {
		if (username.equals("starter")) {
			return Response.status(500).entity("User name should not be 'starter'").build();
		}
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				dbadmin.updateUser(dev, identity, username, email, lang, notify);

				return Response.status(200).entity(identity).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

}
