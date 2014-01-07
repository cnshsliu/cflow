package com.lkh.cflow;

public class PrcInfo {
	public String prcId;
	public String name;
	public String startBy;
	public String wftId;
	public String roundid;
	public String workflowid;
	public String status;

	public static PrcInfo newInstance(String id, String name, String startBy, String wftId, String roundid, String workflowid, String status) {
		PrcInfo ret = new PrcInfo();
		ret.prcId = id;
		ret.name = name;
		ret.startBy = startBy;
		ret.wftId = wftId;
		ret.roundid = roundid;
		ret.workflowid = workflowid;
		ret.status = status;

		return ret;

	}

}
