package com.lkh.cflow.rs;

import java.io.IOException;
import java.io.StringWriter;

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

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/form")
public class Form {
	Logger logger = Logger.getLogger(Form.class);

	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getForm(@QueryParam("tid") String tid, @QueryParam("vtname") String vtname, @QueryParam("token") String tokenString, @QueryParam("usrid") String usrid, @Context HttpServletRequest rqs) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				String formContent = null;

				formContent = CflowHelper.prcManager.parseVT(tokenString, dbadmin, dev, tid, usrid, vtname);

				return Response.status(200).entity(formContent).build();
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	private String getFormContent(String devId, String vtname, VelocityContext context) {
		String ret = "";

		Template tpl = CflowHelper.storageManager.getTemplate(devId, vtname);

		StringWriter sw = new StringWriter();
		tpl.merge(context, sw);

		ret = sw.toString();

		return ret;

	}

}
