package com.lkh.cflow;

public class WorkItemInfo {
	public String devId = "";
	public String prcId = "";
	public String prcname = "";
	public String startBy = "";
	public String wftId = "";
	public String nodeid = "";
	public String sessid = "";
	public String workName = "";
	public String workTitle = "";
	public String doer = "";
	public String delegaterid = "";
	public String color;
	public String lightURL = "/cflow/images/nonlight.png";
	public String status = "";
	public String tid = "";
	public String ppid = "";
	public String ppnodeid = "";
	public String ppsessid = "";
	public static String COLOR_NONE = "NONE";
	public static String COLOR_RED = "RED";
	public static String COLOR_YELLOW = "YELLOW";
	public static String COLOR_GREEN = "GREEN";

	public static WorkItemInfo newInstance(String devId, String pid, String prcname, String startBy, String wftId, String nid, String sid, String status, String wn, String wt, String color, String doer, String delegaterid, String tid, String ppid, String ppnodeid, String ppsessid) {
		WorkItemInfo wii = new WorkItemInfo();
		wii.devId = devId;
		wii.prcId = pid;
		wii.prcname = prcname;
		wii.startBy = startBy;
		wii.wftId = wftId;
		wii.nodeid = nid;
		wii.sessid = sid;
		wii.workName = wn;
		wii.workTitle = wt;
		wii.doer = doer;
		wii.status = status;
		wii.delegaterid = delegaterid;
		wii.color = color;
		if (color.equals(COLOR_RED)) {
			wii.lightURL = "/cflow/images/redlight.png";
		} else if (color.equals(COLOR_GREEN)) {
			wii.lightURL = "/cflow/images/greenlight.png";
		} else if (color.equals(COLOR_YELLOW)) {
			wii.lightURL = "/cflow/images/yellowlight.png";
		} else {
			wii.lightURL = "/cflow/images/nonlight.gif";
		}
		wii.tid = tid;
		wii.ppid = ppid;
		wii.ppnodeid = ppnodeid;
		wii.ppsessid = ppsessid;

		return wii;
	}

	public String getWorkName() {
		return workName;
	}

	public String getDevid() {
		return devId;
	}

	public String getPrcid() {
		return prcId;
	}

	public String getPrcname() {
		return prcname;
	}

	public void setPrcname(String prcname) {
		this.prcname = prcname;
	}

	public String getStartBy() {
		return startBy;
	}

	public void setStartBy(String startBy) {
		this.startBy = startBy;
	}

	public String getWftid() {
		return wftId;
	}

	public void setWftid(String wftId) {
		this.wftId = wftId;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getSessid() {
		return sessid;
	}

	public void setSessid(String sessid) {
		this.sessid = sessid;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getDoer() {
		return doer;
	}

	public void setUsrid(String doer) {
		this.doer = doer;
	}

	public String getDelegaterid() {
		return delegaterid;
	}

	public void setDelegaterid(String delegaterid) {
		this.delegaterid = delegaterid;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getLightURL() {
		return lightURL;
	}

	public void setLightURL(String lightURL) {
		this.lightURL = lightURL;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public void setPrcid(String prcId) {
		this.prcId = prcId;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public void setDevid(String devId) {
		this.devId = devId;
	}

	public void setPpid(String ppid) {
		this.ppid = ppid;
	}

	public String getPpid() {
		return ppid;
	}

	public void setPpnodeid(String ppnodeid) {
		this.ppnodeid = ppnodeid;
	}

	public String getPpnodeid() {
		return ppnodeid;
	}

	public void setPpsessid(String ppsessid) {
		this.ppsessid = ppsessid;
	}

	public String getPpsessid() {
		return ppsessid;
	}

}
