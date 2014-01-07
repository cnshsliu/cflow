package com.lkh.cflow;

public class UrlBuilder {
	String sid;
	boolean bodyOnly;
	boolean isMobile;
	
	public UrlBuilder(String sid, boolean isMobile, boolean bodyOnly ){
		this.sid = sid;
		this.bodyOnly = bodyOnly;
		this.isMobile = isMobile;
	}
	
	public String getUrl(String url){
		String ret = url;
		if(url.indexOf('?') < 0){
			ret = url + "?sid=" + sid + "&isMobile=" + isMobile + "&bodyOnly=" + bodyOnly;
		}else{
			ret = url + "&sid=" + sid + "&isMobile=" + isMobile + "&bodyOnly=" + bodyOnly;
		}
		return ret;
	}
}
