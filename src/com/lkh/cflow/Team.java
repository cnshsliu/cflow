package com.lkh.cflow;

public class Team {
	public String dev;
	public String id;
	public String name;
	public String devId;
	public String memo;

	public static Team newTeam(String devId, String id, String name, String memo) {
		Team aTeam = new Team();
		aTeam.dev = devId;
		aTeam.id = id;
		aTeam.name = name;
		aTeam.memo = memo;

		return aTeam;
	}

}
