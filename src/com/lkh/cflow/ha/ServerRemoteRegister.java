package com.lkh.cflow.ha;

import java.util.HashMap;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import com.lkh.cflow.CflowHelper;

public class ServerRemoteRegister implements Runnable {
	String server = null, lastRegOn = null;
	private HashMap failedMasters = new HashMap();
	Logger logger = Logger.getLogger(ServerRemoteRegister.class);

	public ServerRemoteRegister(String server) {
		this.server = server;
	}

	public void run() {
		String regOn = null;
		String[] masters = CflowHelper.masterTable.keySet().toArray(new String[0]);
		if (CflowHelper.isMaster) {
			boolean lastSuccess = false;
			for (;;) {
				lastSuccess = registerToMaster(this.server, this.server);
				if (lastSuccess)
					mySleep(10000);
				else
					mySleep(10000);
			}
		} else {
			for (;;) {
				boolean success = false;
				failedMasters.clear();
				if (lastRegOn != null) {
					success = registerToMaster(lastRegOn, this.server);
					if (success == true) {
						failedMasters.remove(lastRegOn);
						regOn = lastRegOn;
					} else {
						failedMasters.put(lastRegOn, Boolean.TRUE);
					}
				}
				if (success == false) {
					for (; failedMasters.size() < masters.length;) {
						int elected = Elector.elect(masters.length);
						if (failedMasters.containsKey(masters[elected])) {
							continue;
						}
						success = registerToMaster(masters[elected], this.server);
						if (success == true) {
							regOn = masters[elected];
							failedMasters.remove(masters[elected]);
							break;
						} else {
							failedMasters.put(masters[elected], Boolean.TRUE);
						}
					}
				}

				if (success) {
					lastRegOn = regOn;
					logger.debug("Register to remote master successed: " + regOn);
				}
				if (success)
					mySleep(10000);
				else
					mySleep(10000);
			}
		}

	}

	private void mySleep(int duration) {
		try {
			Thread.currentThread().sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private synchronized boolean registerToMaster(String masterHost, String myHost) {
		boolean ret = false;
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost(masterHost).setPath("/cflow/rest/ha/srvreg").setParameter("server", myHost);
		try {
			RestResponse rr = RemoteAccess.myGet(builder.build());
			boolean result = true;
			if (rr.code == rr.SUCCESS) {
				ret = true;
			} else {
				ret = false;
			}
		} catch (Exception e) {
			logger.debug("RegisterToMaster " + masterHost + " exception occured.");
			ret = false;
		}
		return ret;
	}

}
