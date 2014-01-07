package com.lkh.cflow.tool;

import org.json.simple.JSONObject;

public class MyJSON extends JSONObject {
	JSONObject obj = null;

	public MyJSON(JSONObject obj) {
		this.obj = obj;
	}

	public int getInt(String key) {
		Object v = obj.get(key);
		return Integer.valueOf(v.toString()).intValue();
	}

	public int getInt(String key, int defaultValue) {
		Object v = obj.get(key);
		if (v == null)
			return defaultValue;
		else
			return Integer.valueOf(v.toString()).intValue();
	}

	public long getLong(String key, long defaultValue) {
		Object v = obj.get(key);
		if (v == null)
			return defaultValue;
		else
			return Long.valueOf(v.toString()).longValue();
	}

	public String getString(String key) {
		return obj.getString(key);
	}

	public String getString(String key, String defaultValue) {
		Object v = obj.get(key);
		if (v == null)
			return defaultValue;
		else
			return v.toString();
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		Object v = obj.get(key);
		if (v == null)
			return defaultValue;
		else
			return Boolean.valueOf(v.toString()).booleanValue();
	}
}
