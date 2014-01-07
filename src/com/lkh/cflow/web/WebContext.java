package com.lkh.cflow.web;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.lkh.cflow.Org;
import com.lkh.cflow.TextHelper;
import com.lkh.cflow.User;
import com.lkh.cflow.db.DbAdmin;

public class WebContext {
	public static final String CTX_CLIENTADDR = "ca";
	public static final String CTX_CLIENTUSER = "cu";
	public static final String CTX_ISMOBILE = "im";
	public static final String CTX_BODYONLY = "bo";
	public static final String CTX_MILLIS = "mi";
	public static final String CTX_DOMAIN = "dm";
	public static final String CTX_FB_TOKEN = "access_token";
	public static final String CTX_FB_EXPIRES = "expires";

	private static final String HEX_NUMS_STR = "0123456789ABCDEF";
	public String sid = null;
	public String usrid = null;
	private String decryptedSid = null;
	HttpServletRequest request;
	private HashMap<String, String> kvs = new HashMap<String, String>();
	public User theUser = null;
	public Org theOrg = null;
	public TextHelper textHelper = null;
	public DbAdmin dbadmin = null;
	public String langSuffix = null;

	public static void main(String[] args) {
		String tt = "@ hello";
		String part = tt.substring(1, tt.indexOf(' '));
		System.out.println("|" + part + "|");
	}

	public WebContext(HttpServletRequest request) {
		this.request = request;
		sid = getString(request, "sid", null);
		if (sid != null) {
			try {
				decryptedSid = f(sid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			StringTokenizer st = new StringTokenizer(decryptedSid, "&");
			while (st.hasMoreTokens()) {
				String temp = st.nextToken();
				String b[] = temp.split("=");
				kvs.put(b[0].trim(), b[1].trim());
				if (b[0].trim().equals(CTX_CLIENTUSER)) {
					usrid = b[1].trim();
				}
			}

			String isMobile = getString(request, CTX_ISMOBILE, null); // isMobile
			if (isMobile != null) {
				kvs.put(CTX_ISMOBILE, isMobile);
			}
			String bodyOnly = getString(request, CTX_BODYONLY, null); // bodyOnly
			if (bodyOnly != null) {
				kvs.put(CTX_BODYONLY, bodyOnly);
			}
			String domain = getString(request, CTX_DOMAIN, null); // domain
			if (domain != null) {
				kvs.put(CTX_DOMAIN, domain);
			}
			String tmp1 = getString(request, "access_token", null);
			if (tmp1 != null) {
				kvs.put(CTX_FB_TOKEN, tmp1);
			}
			String tmp2 = getString(request, "expires", null);
			if (tmp2 != null) {
				kvs.put(CTX_FB_EXPIRES, tmp2);
			}
			sid = refreshSid(request);
		}

		// access_token=210437082305397%7C2.ag9n_iMRv_Ts1Z949qjP3Q__.3600.1302426000-844009296%7CKDXLeU6RZxku_2Gd0EVa6GrImD0&expires=4775

	}

	public String getLoginPage() {
		String domain = domain();
		String ret = "/cflow/Login.jsp";
		if (domain.equalsIgnoreCase("PASS8T")) {
			ret = "/cflow/Login.jsp";
		} else if (domain.equalsIgnoreCase("facebook")) {
			ret = "/cflow/facebook/";
		}

		return ret;
	}

	public String refreshSid(HttpServletRequest request) {
		String clientAddress = getClientAddress(request);
		kvs.put(CTX_CLIENTADDR, clientAddress);
		String clientUser = kvs.get(CTX_CLIENTUSER);
		String isMobile = kvs.get(CTX_ISMOBILE);
		String bodyOnly = kvs.get(CTX_BODYONLY);
		String domain = kvs.get(CTX_DOMAIN);
		String fbToken = kvs.get(CTX_FB_TOKEN);
		String fbExpires = kvs.get(CTX_FB_EXPIRES);
		String newSid = composeSid(clientAddress, clientUser, isMobile, bodyOnly, domain, fbToken, fbExpires);

		return newSid;

	}

	public String get(String key) {
		return kvs.get(key);
	}

	public boolean isMobile() {
		boolean ret = false;
		String value = kvs.get(CTX_ISMOBILE);
		if (value == null)
			ret = false;
		else {
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
				ret = true;
			else
				ret = false;
		}

		return ret;
	}

	public boolean bodyOnly() {
		boolean ret = false;
		String value = kvs.get(CTX_BODYONLY);
		if (value == null)
			ret = false;
		else {
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
				ret = true;
			else
				ret = false;
		}

		return ret;
	}

	public String domain() {
		String value = kvs.get(CTX_DOMAIN);
		if (value == null) {
			return "PASS8T";
		} else {
			return value;
		}
	}

	public String composeSid(String clientAddress, String clientUser, String isMobile, String bodyOnly, String domain) {
		long currentMillis = System.currentTimeMillis();
		return _composeSid(clientAddress, clientUser, isMobile, bodyOnly, domain, null, null, currentMillis);
	}

	public String composeSid(String clientAddress, String clientUser, String isMobile, String bodyOnly, String domain, String fbToken, String fbExpires) {
		long currentMillis = System.currentTimeMillis();
		return _composeSid(clientAddress, clientUser, isMobile, bodyOnly, domain, fbToken, fbExpires, currentMillis);
	}

	private String _composeSid(String clientAddress, String clientUser, String isMobile, String bodyOnly, String domain, String fbToken, String fbExpires, long millis) {
		String ret = "";
		ret = CTX_CLIENTADDR + "=" + clientAddress + "&" + CTX_CLIENTUSER + "=" + clientUser + "&" + CTX_ISMOBILE + "=" + isMobile + "&" + CTX_BODYONLY + "=" + bodyOnly + "&" + CTX_DOMAIN + "=" + domain + "&" + CTX_MILLIS + "=" + millis;
		if (fbToken != null && fbExpires != null) {
			ret += "&" + CTX_FB_TOKEN + "=" + fbToken + "&" + CTX_FB_EXPIRES + "=" + fbExpires;
		}
		try {
			ret = t(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String dumpRequest() {
		StringBuffer ret = new StringBuffer();
		Enumeration<String> params = this.request.getParameterNames();
		for (; params.hasMoreElements();) {
			String key = (String) params.nextElement();
			ret.append(key).append("=").append(this.request.getParameter(key)).append("<BR>");
		}
		return ret.toString();
	}

	public String validateSidWithoutExpiration() {
		if (sid == null)
			return null;
		else {
			String clientUser = kvs.get(CTX_CLIENTUSER);
			if (clientUser == null) {
				return null;
			} else {
				String ip = kvs.get(CTX_CLIENTADDR);
				if (ip == null)
					return null;
				else {
					String clientIp = getClientAddress(request);
					if (!clientIp.equals(ip)) {
						return null;
					} else {
						return sid;
					}
				}
			}
		}
	}

	public String validateSid() {
		if (sid == null)
			return null;
		else {
			String clientUser = kvs.get(CTX_CLIENTUSER);
			if (clientUser == null) {
				return null;
			} else {
				String ip = kvs.get(CTX_CLIENTADDR);
				if (ip == null)
					return null;
				else {
					String clientIp = getClientAddress(request);
					if (!clientIp.equals(ip)) {
						return null;
					} else {
						String temp = kvs.get(CTX_MILLIS);
						if (temp == null)
							return null;
						else {
							long lastMillis = Long.valueOf(temp).longValue();
							long currentMillis = System.currentTimeMillis();
							if ((currentMillis - lastMillis) > 1800000) {
								return "Expired";
							} else {
								sid = _composeSid(clientIp, clientUser, kvs.get(CTX_ISMOBILE), kvs.get(CTX_BODYONLY), kvs.get(CTX_DOMAIN), kvs.get(CTX_FB_TOKEN), kvs.get(CTX_FB_EXPIRES), currentMillis);
								return sid;
							}
						}
					}
				}
			}
		}
	}

	public void setUser(User user) {
		this.theUser = user;
	}

	public void setOrg(Org org) {
		this.theOrg = org;
	}

	public void setTextHelper(TextHelper th) {
		this.textHelper = th;
	}

	public void setDbAdmin(DbAdmin dbadmin) {
		this.dbadmin = dbadmin;
	}

	public void setLangSuffix(String langSuffix) {
		this.langSuffix = langSuffix;
	}

	public String getString(String key, String defaultValue) {
		return getString(this.request, key, defaultValue);
	}

	public String getString(HttpServletRequest request, String key, String defaultValue) {
		String ret = defaultValue;
		try {
			request.setCharacterEncoding("UTF8");
			ret = request.getParameter(key);
			if (ret == null)
				ret = defaultValue;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return ret;
	}

	public int getInt(String key, int defaultValue) {
		return getInt(this.request, key, defaultValue);
	}

	public int getInt(HttpServletRequest request, String key, int defaultValue) {
		String tmp = getString(request, key, "" + defaultValue);
		int ret = Integer.valueOf(tmp).intValue();
		return ret;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return getBoolean(this.request, key, defaultValue);
	}

	public boolean getBoolean(HttpServletRequest request, String key, boolean defaultValue) {
		boolean ret = defaultValue;
		String value = request.getParameter(key);
		if (value == null)
			ret = defaultValue;
		else {
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
				ret = true;
			else
				ret = false;
		}

		return ret;
	}

	public String getClientAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public String getUrl(String url) {
		String ret = url;
		if (url.indexOf('?') < 0) {
			ret = url + "?sid=" + sid;
		} else {
			ret = url + "&sid=" + sid;
		}
		return ret;
	}

	/**
	 * decrypt
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String f(String str) throws Exception {
		byte[] licBytes;
		licBytes = hexStringToByte(str);
		// licHex = new String(licBytes);
		// licBytes = hexStringToByte(licHex);
		return new String(licBytes);
	}

	/**
	 * encrypt
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String t(String str) throws Exception {
		String licHex;
		byte[] licBytes;

		licBytes = str.getBytes("UTF-8");
		licHex = byteToHexString(licBytes);
		// licBytes = licHex.getBytes("UTF-8");
		// licHex = byteToHexString(licBytes);

		return licHex;
	}

	/**
	 * 将16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return k
	 */
	private static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] hexChars = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
		}
		return result;
	}

	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	private static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

}
