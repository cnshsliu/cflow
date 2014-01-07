package com.lkh.cflow;

public class Membership {
	private String teamid;
	private String usridentity;
	private String usrname;
	private String role;

	public static Membership newMembership(String teamid, String identity, String username, String role) {
		Membership ret = new Membership();
		ret.setTeamId(teamid);
		ret.setUserIdentity(identity);
		ret.setUserName(username);
		ret.setRole(role);

		return ret;
	}

	private void setTeamId(String teamid) {
		this.teamid = teamid;
	}

	private void setUserIdentity(String identity) {
		this.usridentity = identity;
	}

	private void setUserName(String username) {
		this.usrname = username;
	}

	private void setRole(String role) {
		this.role = role;
	}

	public String getTeamId() {
		return teamid;
	}

	public String getUserIdentity() {
		return usridentity;
	}

	public String getUserName() {
		return usrname;
	}

	public String getRole() {
		return role;
	}
}
