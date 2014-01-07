package com.lkh.cflow;
import java.sql.*;

public class DelegationEntry {
	public long id;
	public String delegater;
	public String delegatee;
	public Timestamp starttime;
	public Timestamp endtime;
	public String prcId;
	public String nodeid;
	public String sessid;
	public String delegatetype;
	
	public static DelegationEntry newInstance(long id, String delegater, 
			String delegatee, Timestamp starttime, Timestamp endtime, 
			String prcId, String nodeid, String sessid, String delegatetype)
	{
		DelegationEntry ret = new DelegationEntry();
		ret.id = id;
		ret.delegater = delegater;
		ret.delegatee = delegatee;
		ret.starttime = starttime;
		ret.endtime = endtime;
		ret.prcId = prcId;
		ret.nodeid = nodeid;
		ret.sessid = sessid;
		ret.delegatetype = delegatetype;
		
		return ret;
		
	}
}
