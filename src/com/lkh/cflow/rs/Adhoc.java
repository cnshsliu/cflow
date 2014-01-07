package com.lkh.cflow.rs;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.Ctool;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.ProcessDocument;
import com.lkh.cflow.ProcessT;
import com.lkh.cflow.adhocEntry;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/adhoc")
public class Adhoc {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(Delegation.class);

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response insertAdhoc(@FormParam("prcid") String prcId, @FormParam("nodeid") String nodeid, @FormParam("adhocs") String ctx, @FormParam("token") String tokenString, @Context HttpServletRequest servletRequest) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		boolean kc = dbadmin.keepConnection(true);
		try {
			String devId = TokenAdmin.getDevByToken(tokenString, servletRequest.getRemoteAddr());
			if (devId == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject data = new JSONObject();
				data.put("PRCID", prcId);
				data.put("NODEID", nodeid);
				Ctool.validateInput(data);

				logger.info("RS Adhoc");

				ProcessDocument prcDoc = CflowHelper.prcManager.getProcessDocument(devId, dbadmin, prcId);
				ProcessT prc = prcDoc.getProcess();

				JSONParser parser = new JSONParser();
				JSONArray jarr = (JSONArray) parser.parse(ctx);
				if (jarr.size() > 0) {
					adhocEntry[] adhocs = new adhocEntry[jarr.size()];
					for (int i = 0; i < jarr.size(); i++) {
						JSONObject adhoc = (JSONObject) jarr.get(i);
						adhocs[i] = new adhocEntry((String) adhoc.get("TASKNAME"), (String) adhoc.get("PERSON"), (String) adhoc.get("ROLE"));
					}
					CflowHelper.prcManager.insertAdhoc(devId, prcId, nodeid, adhocs);
					CflowHelper.prcManager.saveProcessDoc(devId, prcDoc, prcId);
				}
				String ret = "OK";

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
