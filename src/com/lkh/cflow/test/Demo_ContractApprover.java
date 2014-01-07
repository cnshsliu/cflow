package com.lkh.cflow.test;

import org.json.simple.JSONObject;

import com.lkh.cflow.Adapter;

public class Demo_ContractApprover implements Adapter {

	@Override
	public String adapt(JSONObject ctx) {

		String cvs = ctx.get("contract_value").toString();
		int cv = Integer.valueOf(cvs).intValue();

		String approver = "";
		if (cv < 100)
			approver = "manager";
		else if (cv < 1000)
			approver = "director";
		else
			approver = "gm";

		JSONObject retObj = new JSONObject();
		retObj.put("RETURN", approver);
		// reason 在 模板中有，它的值将保留
		retObj.put("reason", "test value to change back to process attachment.");
		// ignored 在模板中没有，他的值将被忽略
		retObj.put("ignored", "this will be ignored.");

		return retObj.toJSONString();
	}
}