package com.lkh.cflow.test;

import org.json.simple.JSONObject;

import com.lkh.cflow.Adapter;

public class MyJavaAdapter implements Adapter {
	@Override
	public String adapt(JSONObject ctx) {
		// 从Context中取到Attachments集合
		// 从Attachments集合中按变量名称取得变量的值。
		String days = ctx.get("days").toString();
		int idays = Integer.valueOf(days).intValue();

		String ret = "";
		if (idays > 10)
			ret = "long";
		else
			ret = "short";

		JSONObject retObj = new JSONObject();
		retObj.put("RETURN", ret);
		// reason 在 模板中有，它的值将保留
		retObj.put("reason", "test value to change back to process attachment.");
		// ignored 在模板中没有，他的值将被忽略
		retObj.put("ignored", "this will be ignored.");

		ret = retObj.toJSONString();
		return ret;
	}
}