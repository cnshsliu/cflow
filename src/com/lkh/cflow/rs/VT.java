package com.lkh.cflow.rs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.Ctool;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/vt")
public class VT {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response uploadVt(@FormParam("vtname") String vtname, @FormParam("content") String content, @FormParam("token") String tokenString, @Context HttpServletRequest rqs) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				JSONObject data = new JSONObject();
				data.put("VTNAME", vtname);
				Ctool.validateInput(data);

				dbadmin.addVtToDeveloper(dev, vtname);
				String vtPath = CflowHelper.storageManager.getVtPath(dev, vtname);
				CflowHelper.storageManager.upload(vtPath, content, "text/html");

				return Response.status(200).entity(vtname).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getVtContent(@QueryParam("vtname") String vtname, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
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
					String vtPath = CflowHelper.storageManager.getVtPath(dev, vtname);
					CflowHelper.storageManager.download(vtPath, baos);
					entityString = baos.toString("UTF-8");
					baos.close();
				}// end try
				catch (Exception e) {
					return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
				}// end catch

				return Response.status(200).entity(entityString).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@DELETE
	public Response deleteVt(@QueryParam("vtname") String vtname, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				dbadmin.deleteVt(dev, vtname);
				return Response.status(200).entity(vtname).build();
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
	public Response getVts(@QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONArray vts = dbadmin.getVtsByDeveloper(dev);
				String ret = vts.toJSONString();

				return Response.status(200).entity(ret).header("Cache-Control", "no-cache").header("Pragma", "no-cahce").header("Expires", 0).lastModified(java.util.Calendar.getInstance().getTime()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}
}
