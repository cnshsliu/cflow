package com.lkh.server.tcp;

public class ReturnValue {
	public Object serverToClient;
	public String serverControl;

	public ReturnValue(Object serverToClient, String serverControl) {
		this.serverToClient = serverToClient;
		this.serverControl = serverControl;
	}

	public String toString() {
		return this.serverToClient + ":" + this.serverControl;
	}
}
