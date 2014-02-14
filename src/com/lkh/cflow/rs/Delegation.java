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
import org.json.simple.JSONObject;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.Ctool;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.ProcessDocument;
import com.lkh.cflow.ProcessT;
import com.lkh.cflow.Work;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/delegation")
public class Delegation {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	static Logger logger = Logger.getLogger(Delegation.class);

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response delegate(@FormParam("delegater") String delegater_id, @FormParam("delegatee") String delegatee_id, @FormParam("prcid") String prcId, @FormParam("sessid") String sessid, @FormParam("token") String tokenString, @Context HttpServletRequest rqs) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		boolean kc = dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString);
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject data = new JSONObject();
				data.put("DELEGATER", delegater_id);
				data.put("DELEGATEE", delegatee_id);
				data.put("PRCID", prcId);
				data.put("SESSID", sessid);
				Ctool.validateInput(data);

				ProcessDocument prcDoc = CflowHelper.prcManager.getProcessDocument(dev, dbadmin, prcId);
				ProcessT prc = prcDoc.getProcess();
				// 根据sessid取当前的活动项。
				Work theWork = CflowHelper.prcManager.getWork(prc, sessid);
				CflowHelper.prcManager.delegateOneTask(dev, dbadmin, prc.getId(), prc.getWftid(), theWork, delegater_id, delegatee_id);
				CflowHelper.prcManager.saveProcessDoc(dev, prcDoc, prcId);
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
