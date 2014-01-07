package com.lkh.cflow.rs;

import java.sql.SQLException;

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

import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.Team;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/joined_teams")
public class JoinedTeams {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	@GET
	@Produces(MediaType.TEXT_XML)
	public Response getTeams(@QueryParam("token") String tokenString, @QueryParam("usrid") String usrid, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();

		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				String ret = "";
				ret += "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
				ret += "<teams>";
				try {
					Team[] teams = dbadmin.getTeamsByMemberId(dev, usrid);
					for (int i = 0; i < teams.length; i++) {
						ret += "<team>";
						ret += "<teamid>" + teams[i].id + "</teamid>";
						ret += "<name>" + teams[i].name + "</name>";
						ret += "<memo>" + teams[i].memo + "</email>";
						ret += "</team>";
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				ret += "</teams>";

				return Response.status(200).entity(ret).build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

}
