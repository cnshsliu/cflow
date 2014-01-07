package com.lkh.cflow;

public class RoleInfo {
	public String id;
	public String owner;
	public String role;
	
	public static RoleInfo newInstance(String id, String owner, String role){
		RoleInfo ret = new RoleInfo();
		ret.id = id;
		ret.owner = owner;
		ret.role = role;
		return ret;
	}
}
