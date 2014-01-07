package com.lkh.cflow;

import java.util.Calendar;

public class Org {
	public String orgid;
	public String orgname;
	public boolean shareWft;
	public String owner;
	public Calendar valid;
	public int wftlimit;
	public int prclimit;
	
	public static Org newOrg(String orgid, String orgname, boolean shareWft, String owner, Calendar valid, int wftlimit, int prclimit){
		Org ret = new Org();
		ret.orgid = orgid;
		ret.orgname = orgname;
		ret.shareWft = shareWft;
		ret.owner = owner;
		ret.valid = valid;
		ret.wftlimit = wftlimit;
		ret.prclimit = prclimit;
		
		return ret;
	}
	
	public static Org newOrg(String orgid, String orgname, boolean shareWft, String owner){
		Org ret = new Org();
		ret.orgid = orgid;
		ret.orgname = orgname;
		ret.shareWft = shareWft;
		ret.owner = owner;
		//缺省时间有效性为一个月；
		ret.valid = Calendar.getInstance();
		ret.valid.add(Calendar.MONTH, 1);
		//设置缺省WFTLIMIT和缺省PRCLIMIT;
		ret.wftlimit = CflowHelper.DEFAULT_WFTLIMIT;
		ret.prclimit = CflowHelper.DEFAULT_PRCLIMIT;
		
		return ret;
	}

}
