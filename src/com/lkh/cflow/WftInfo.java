package com.lkh.cflow;

public class WftInfo {
	public String wftId;
	public String wftname;

	public static WftInfo newInstance(String wftId, String wftname)
	{
		WftInfo ret = new WftInfo();
		ret.wftId = wftId;
		ret.wftname = wftname;
		
		return ret;
		
	} 
	
	
	public boolean equals(Object obj){   // 重写 equals 方法
		WftInfo param = (WftInfo)obj;
		if(this.wftId.equals(param.wftId)){
			return true;
		} else{
			return false;
		}
	}
}
