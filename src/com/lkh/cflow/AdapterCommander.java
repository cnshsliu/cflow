package com.lkh.cflow;

import java.lang.reflect.Method;

import org.json.simple.JSONObject;

public class AdapterCommander {

	public static void main(String[] args) {

		JSONObject ctx = new JSONObject();
		ctx.put("PRCID", "123456");
		ctx.put("key1", "value1");
		ctx.put("key2", "value2");
		try {
			String ret = runAdaptor("com.lkh.cflow.test.MyLinker", ctx);
			System.out.println(ret);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public AdapterCommander() {

	}

	public static String runAdaptor(String linkerName, JSONObject ctx) throws Exception {
		String ret = "";
		Class<Adapter> linkerClass = (Class<Adapter>) Class.forName(linkerName);
		Adapter theLinker = (Adapter) linkerClass.newInstance();
		Method linkerProxyMethod = linkerClass.getDeclaredMethod("adapt", JSONObject.class);
		ret = (String) linkerProxyMethod.invoke(theLinker, ctx);

		return ret;
	}

}
