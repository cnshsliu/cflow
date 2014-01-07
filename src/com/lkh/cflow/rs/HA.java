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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.ha.Elector;

//Sets the path to base URL + /hello
@Path("/ha")
public class HA {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	@GET
	@Path("/srvreg")
	@Produces(MediaType.TEXT_HTML)
	public Response regServer(@QueryParam("server") String server, @Context HttpServletRequest rqs) {
		synchronized (CflowHelper.serverTable) {
			CflowHelper.serverTable.put(server, System.currentTimeMillis());
			String ret = "Done";
			// TODO:
			return Response.status(200).entity(ret).header("Cache-Control", "no-cache").header("Pragma", "no-cahce").header("Expires", 0).lastModified(java.util.Calendar.getInstance().getTime()).build();
		}
	}

	@GET
	@Path("/srvsync")
	@Produces(MediaType.TEXT_HTML)
	public Response syncServer(@QueryParam("status") String status, @Context HttpServletRequest rqs) {
		synchronized (CflowHelper.serverTable) {
			JSONParser parser = new JSONParser();
			JSONObject tmp;
			try {
				tmp = (JSONObject) parser.parse(status);
				String[] tmpServers = (String[]) tmp.keySet().toArray(new String[0]);
				for (int i = 0; i < tmpServers.length; i++) {
					if (tmp.getString(tmpServers[i]).equals("Y")) {
						CflowHelper.syncedServerTable.put(tmpServers[i], System.currentTimeMillis());
					} else {
						CflowHelper.syncedServerTable.remove(tmpServers[i]);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String ret = "Done";
			return Response.status(200).entity(ret).header("Cache-Control", "no-cache").header("Pragma", "no-cahce").header("Expires", 0).lastModified(java.util.Calendar.getInstance().getTime()).build();
		}
	}

	@GET
	@Path("/server")
	@Produces(MediaType.TEXT_HTML)
	public Response getServer(@QueryParam("dev") String devId, @Context HttpServletRequest rqs) {
		synchronized (CflowHelper.serverTable) {
			String lastUsedServer = (String) CflowHelper.lruDevServer.get(devId);
			boolean needToChooseServer = false;
			if (lastUsedServer == null) {
				needToChooseServer = true;
			} else {
				if (CflowHelper.serverTable.get(lastUsedServer) == null && CflowHelper.syncedServerTable.get(lastUsedServer) == null) {
					needToChooseServer = true;
				} else {
					needToChooseServer = false;
				}
			}

			if (needToChooseServer) {
				String[] serverArr1 = CflowHelper.serverTable.keySet().toArray(new String[0]);
				String[] serverArr2 = CflowHelper.syncedServerTable.keySet().toArray(new String[0]);
				String[] allServers = new String[serverArr1.length + serverArr2.length];
				for (int i = 0; i < serverArr1.length; i++) {
					allServers[i] = serverArr1[i];
				}
				for (int i = 0; i < serverArr2.length; i++) {
					allServers[serverArr1.length + i] = serverArr2[i];
				}
				int elected = Elector.elect(allServers.length);
				lastUsedServer = allServers[elected];
				CflowHelper.lruDevServer.put(devId, lastUsedServer);
			}
			return Response.status(200).entity(lastUsedServer).header("Cache-Control", "no-cache").header("Pragma", "no-cahce").header("Expires", 0).lastModified(java.util.Calendar.getInstance().getTime()).build();
		}
	}

	@GET
	@Path("/check")
	@Produces(MediaType.TEXT_HTML)
	public Response checkAlive(@Context HttpServletRequest rqs) {
		String ret = "Done";
		return Response.status(200).entity(ret).header("Cache-Control", "no-cache").header("Pragma", "no-cahce").header("Expires", 0).lastModified(java.util.Calendar.getInstance().getTime()).build();
	}

}
