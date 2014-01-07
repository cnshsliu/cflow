package com.lkh.cflow.rs;

import java.io.IOException;
import java.sql.SQLException;

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

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.lkh.cflow.Ctool;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.Membership;
import com.lkh.cflow.Team;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

//Sets the path to base URL + /hello
@Path("/team")
public class Teaming {
	@Context
	UriInfo uriinfo;

	@Context
	Request request;

	public static Logger logger = Logger.getLogger(Teaming.class);

	@GET
	@Path("byname")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeam(@QueryParam("teamname") String teamName, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		com.lkh.cflow.Team team = null;

		DbAdmin dbadmin = DbAdminPool.get();

		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				team = dbadmin.getTeamByName(dev, teamName);

				if (team != null) {
					JSONObject aTeam = new JSONObject();
					aTeam.put("ID", team.id);
					aTeam.put("NAME", team.name);
					aTeam.put("MEMO", team.memo);
					return Response.status(200).entity(JSONValue.toJSONString(aTeam)).build();
				} else {
					return Response.status(404).entity("Object not found").build();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Path("byid")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeamById(@QueryParam("teamid") String teamId, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		com.lkh.cflow.Team team = null;

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				team = dbadmin.getTeamById(dev, teamId);

				if (team != null) {
					JSONObject aTeam = new JSONObject();
					aTeam.put("ID", team.id);
					aTeam.put("NAME", team.name);
					aTeam.put("MEMO", team.memo);
					return Response.status(200).entity(JSONValue.toJSONString(aTeam)).build();
				} else {
					return Response.status(404).entity("Object not found").build();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeams(@QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {

				JSONArray list = new JSONArray();
				Object[] teams = dbadmin.getTeams(dev);
				for (int i = 0; i < teams.length; i++) {
					Team team = (Team) teams[i];
					JSONObject aTeam = new JSONObject();
					aTeam.put("ID", team.id);
					aTeam.put("NAME", team.name);
					aTeam.put("MEMO", team.memo);
					list.add(aTeam);
				}
				String ret = JSONValue.toJSONString(list);

				return Response.status(200).entity(ret).build();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	// Create team
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createTeam(@FormParam("teamname") String teamname, @FormParam("token") String tokenString, @FormParam("memo") String memo, @Context HttpServletRequest rqs) throws IOException {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONObject data = new JSONObject();
				data.put("TEAMNAME", teamname);
				data.put("TEAMMEMO", memo);
				Ctool.validateInput(data);

				String teamId = dbadmin.createTeam(dev, teamname, memo);

				return Response.status(200).entity(teamId).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@DELETE
	public Response deleteTeam(@QueryParam("teamid") String teamid, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				dbadmin.removeTeam(dev, teamid);
				return Response.status(200).entity(teamid).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@GET
	@Path("members")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeamMembers(@QueryParam("teamid") String teamid, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONArray list = new JSONArray();
				Membership[] memberships = dbadmin.getTeamMemberships(dev, teamid);
				for (int i = 0; i < memberships.length; i++) {
					JSONObject membership = new JSONObject();
					membership.put("TEAMID", memberships[i].getTeamId());
					membership.put("IDENTITY", memberships[i].getUserIdentity());
					membership.put("USRNAME", memberships[i].getUserName());
					membership.put("ROLE", memberships[i].getRole());
					list.add(membership);
				}
				String ret = list.toJSONString();

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
	@Path("membersbyrole")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTeamMembersByRole(@QueryParam("teamid") String teamid, @QueryParam("role") String role, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {

		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				JSONArray list = new JSONArray();
				Membership[] memberships = dbadmin.getTeamMembershipsByRole(dev, teamid, role);
				for (int i = 0; i < memberships.length; i++) {
					JSONObject membership = new JSONObject();
					membership.put("TEAMID", memberships[i].getTeamId());
					membership.put("IDENTITY", memberships[i].getUserIdentity());
					membership.put("USRNAME", memberships[i].getUserName());
					membership.put("ROLE", memberships[i].getRole());
					list.add(membership);
				}
				String ret = list.toJSONString();

				return Response.status(200).entity(ret).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@POST
	@Path("members")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addTeamMembers(@FormParam("teamid") String teamid, @FormParam("memberships") String memberships, @FormParam("token") String tokenString, @Context HttpServletRequest rqs) throws IOException {
		JSONParser parser = new JSONParser();
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				try {
					JSONObject msa = (JSONObject) parser.parse(memberships);
					dbadmin.addTeamMemberByIdentity(dev, teamid, msa);
				} catch (Exception ex) {
					logger.error(ex.getLocalizedMessage());
				}

				return Response.status(200).entity(teamid).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

	@DELETE
	@Path("member")
	public Response removeTeamMember(@QueryParam("teamid") String teamid, @QueryParam("memberid") String memberid, @QueryParam("token") String tokenString, @Context HttpServletRequest rqs) {
		DbAdmin dbadmin = DbAdminPool.get();
		dbadmin.keepConnection(true);
		try {
			String dev = TokenAdmin.getDevByToken(tokenString, rqs.getRemoteAddr());
			if (dev == null) {
				return Response.status(401).entity("Session failed").build();
			} else {
				dbadmin.removeTeamMember(dev, teamid, memberid);
				return Response.status(200).entity(memberid).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Exception " + e.getLocalizedMessage()).build();
		} finally {
			DbAdminPool.ret(dbadmin);
		}
	}

}
