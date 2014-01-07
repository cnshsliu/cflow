package com.lkh.cflow.rs;

import java.io.IOException;
import java.util.HashMap;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.ProcessDocument;
import com.lkh.cflow.ProcessT;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/process_team_member")
public class ProcessTeamMember {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(Delegation.class);

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updateTeamMembers(@FormParam("prcid") String prcId, @FormParam("nodeid") String nodeid, @FormParam("rtcs") String ctx, @FormParam("token") String tokenString, @Context HttpServletRequest rqs) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				logger.info("RS Team Members prc " + prcId + " node " + nodeid);

				ProcessDocument prcDoc = CflowHelper.prcManager.getProcessDocument(dev, dbadmin, prcId);
				ProcessT prc = prcDoc.getProcess();

				HashMap<String, String[]> rtcs = new HashMap<String, String[]>();
				JSONParser parser = new JSONParser();
				JSONArray jarr = (JSONArray) parser.parse(ctx);
				if (jarr.size() > 0) {
					for (int i = 0; i < jarr.size(); i++) {
						JSONObject rtc = (JSONObject) jarr.get(i);
						String[] participates = StringUtils.split((String) rtc.get("PARTICIPATE"), ',');
						rtcs.put((String) rtc.get("ROLE"), participates);
					}
					CflowHelper.prcManager.updateTeamMembers(prc, rtcs);
					CflowHelper.prcManager.saveProcessDoc(dev, prcDoc, prcId);
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
