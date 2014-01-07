package com.lkh.cflow.ha;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.lkh.cflow.CflowHelper;

public class ServerAdmin implements Runnable {
	int checkInterval = 3000;
	long deadDuration = 10000;
	private final static Logger logger = Logger.getLogger(ServerAdmin.class);

	public ServerAdmin(int checkInterval) {
		this.checkInterval = checkInterval;
	}

	private void updateServerStatus(String server, boolean isAlive) {
		synchronized (CflowHelper.serverTable) {
			if (isAlive) {
				CflowHelper.serverTable.put(server, System.currentTimeMillis());
			} else {
				CflowHelper.serverTable.remove(server);
			}
		}
	}

	public void run() {
		JSONObject serverStatus = new JSONObject();
		for (;;) {
			String[] servers = CflowHelper.serverTable.keySet().toArray(new String[0]);
			serverStatus.clear();
			for (int i = 0; i < servers.length; i++) {
				boolean checkIsSuccess = false;
				// CflowHelper.regOnServerTable
				checkIsSuccess = checkServerAliveness(servers[i]);
				updateServerStatus(servers[i], checkIsSuccess);
				serverStatus.put(servers[i], checkIsSuccess ? "Y" : "N");
			}

			String[] masters = CflowHelper.masterTable.keySet().toArray(new String[0]);
			for (int i = 0; i < masters.length; i++) {
				if (masters[i].equals(CflowHelper.myHost))
					continue;
				syncToMaster(masters[i], serverStatus.toString());
			}

			synchronized (CflowHelper.serverTable) {
				servers = CflowHelper.syncedServerTable.keySet().toArray(new String[0]);
				for (int i = 0; i < servers.length; i++) {
					Long lastSeen = CflowHelper.syncedServerTable.get(servers[i]);
					if (lastSeen.longValue() < System.currentTimeMillis() - deadDuration) {
						CflowHelper.syncedServerTable.remove(servers[i]);
					}
				}
			}

			showServers();
			try {
				Thread.currentThread().sleep(checkInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void showServers() {
		String[] servers = CflowHelper.serverTable.keySet().toArray(new String[0]);
		for (int i = 0; i < servers.length; i++) {
			logger.info("I supervise " + servers[i]);
		}
		servers = CflowHelper.syncedServerTable.keySet().toArray(new String[0]);
		for (int i = 0; i < servers.length; i++) {
			logger.info("I provide " + servers[i]);
		}
	}

	private boolean checkServerAliveness(String server) {
		boolean ret = false;
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost(server).setPath("/cflow/rest/ha/check");
		try {
			RestResponse rr = RemoteAccess.myGet(builder.build());
			boolean result = true;
			if (rr.code == rr.SUCCESS) {
				ret = true;
			} else {
				ret = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	private boolean syncToMaster(String masterHost, String serverStatus) {
		boolean ret = false;
		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost(masterHost).setPath("/cflow/rest/ha/srvsync").setParameter("status", serverStatus);
		try {
			RestResponse rr = RemoteAccess.myGet(builder.build());
			boolean result = true;
			if (rr.code == rr.SUCCESS) {
				ret = true;
			} else {
				ret = false;
			}
		} catch (Exception e) {
			logger.debug("syncToMaster " + masterHost + " exception occured.");
			ret = false;
		}
		return ret;
	}

}
