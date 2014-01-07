package com.lkh.cflow.test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class testJSONString {
	public static void main(String[] args) {

		JSONArray members = new JSONArray();
		JSONObject approver_member = new JSONObject();
		JSONObject auditor_member = new JSONObject();
		approver_member.put("ROLE", "Approver");
		approver_member.put("USRID", "U3307");
		auditor_member.put("ROLE", "Auditor");
		auditor_member.put("USRID", "U3308");
		members.add(approver_member);
		members.add(auditor_member);

		System.out.println(members.toJSONString());
	}
}
