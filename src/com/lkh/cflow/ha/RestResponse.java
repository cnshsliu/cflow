package com.lkh.cflow.ha;

public class RestResponse {
	public final static int SUCCESS = 200;
	public String msg = "";
	public int code = SUCCESS;

	public RestResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
