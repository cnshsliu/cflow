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
import com.lkh.cflow.WftInfo;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/wft")
public class Wft {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	@GET
	@Path("doc")
	@Produces(MediaType.APPLICATION_XML)
	public Response getWftDoc(@QueryParam("wftid") String wftId, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
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
					String wftPath = CflowHelper.storageManager.getWftPath(dev, wftId);
					CflowHelper.storageManager.download(wftPath, baos);
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
		}
	}

	@GET
	@Path("example")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExample(@QueryParam("modelid") String modelId, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				String newWftId = CflowHelper.wftManager.copyModel(dev, dbadmin, modelId, "en_US");

				return Response.status(200).entity(newWftId).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response uploadWft(@FormParam("wftname") String wftname, @FormParam("wft") String wft, @FormParam("token") String tokenString, @Context HttpServletRequest rqs) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject data = new JSONObject();
				data.put("WFTNAME", wftname);
				Ctool.validateInput(data);
				String wftId = CflowHelper.myID();
				wftId = CflowHelper.wftManager.saveWft(dev, dbadmin, wft, wftId, wftname);

				if (wftId == null) {
					return Response.status(401).entity("Failed.").build();
				} else {
					return Response.status(200).entity(wftId).build();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@DELETE
	public Response deleteWft(@QueryParam("wftid") String wftId, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				dbadmin.removeWft(dev, wftId);
				return Response.status(200).entity(wftId).build();
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
	public Response getWorkflowTemplates(@QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
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
						wft.put("WFTID", userworkflows[j].wftId);
						wft.put("WFTNAME", userworkflows[j].wftname);
						wfts.add(wft);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				String ret = wfts.toJSONString();

				return Response.status(200).entity(ret).header("Cache-Control", "no-cache").header("Pragma", "no-cahce").header("Expires", 0).lastModified(java.util.Calendar.getInstance().getTime()).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWftInfo(@QueryParam("token") String tokenString, @QueryParam("wftid") String wftId, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				JSONObject wft = new JSONObject();
				try {
					WftInfo wftinfo = dbadmin.getWftInfo(dev, wftId);
					if (wftinfo != null) {
						wft.put("WFTID", wftinfo.wftId);
						wft.put("WFTNAME", wftinfo.wftname);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				String ret = wft.toJSONString();

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
