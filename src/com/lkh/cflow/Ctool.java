package com.lkh.cflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class Ctool {
	static Logger logger = Logger.getLogger(Ctool.class);
	private static HashMap varLenRule = new HashMap();
	private static String[] vars = { "DEVID", "DEVNAME", "ACCESSKEY", "EMAIL", "TZID", "LANG", "PRCID", "NODEID", "STARTBY", "INSTANCENAME", "DELEGATER", "DELEGATEE", "SESSID", "VTNAME", "IDENTITY", "USERNAME", "NOTIFY", "WFTNAME", "TEAMNAME", "TEAMMEMO" };
	private static int[] lens = { 24, 40, 20, 50, 9, 8, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 9, 40, 40, 40 };

	public static String newSerial() {
		return "hello";
	}

	public static void main(String[] args) throws SQLException {
		System.out.println("Ctool dummy.");
		System.out.println(Ctool.safeEmailAddress("12345@gmail.com"));
	}

	public static boolean isEmpty(String str) {
		if (str != null && str.length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isNull(String str) {
		if (str == null)
			return true;
		else if (str.equalsIgnoreCase("null"))
			return true;
		else
			return false;
	}

	public static boolean requestHasKV(HttpServletRequest request, String key, String value) {
		if (request.getParameter(key) == null) {
			return false;
		}
		if (request.getParameter(key).equals(value)) {
			return true;
		} else {
			return false;
		}
	}

	public static String safeEmailAddress(String email) {
		int atPos = email.indexOf('@');
		if (atPos < 0) {
			return "******";
		}

		char[] ret = email.toCharArray();
		if (atPos - 2 > 3)
			atPos = atPos - 2;
		for (int i = 0; i < atPos; i++) {
			ret[i] = '*';
		}

		return new String(ret);
	}

	public static String trimTo(String str, int len) {
		if (str.length() <= len)
			return str;
		else
			return str.substring(0, len);
	}

	public static boolean validateInput(JSONObject data) throws Exception {
		Iterator<String> itr = data.keySet().iterator();
		while (itr.hasNext()) {
			boolean ruleCompared = false;
			String key = itr.next();
			for (int i = 0; i < vars.length; i++) {
				if (key.equals(vars[i])) {
					if (data.getString(key).length() > lens[i]) {
						throw new Exception(key + " length should be less than " + lens[i]);
					}
					ruleCompared = true;
					break;
				}
			}
			if (ruleCompared == false) {
				throw new Exception(key + " length rule does not exist.");
			}
		}
		return true;
	}

	public void streamCopy(InputStream inps, OutputStream oups) {
		byte[] buffer = new byte[1024];
		int i = 0;
		try {
			while ((i = inps.read(buffer)) != -1) {
				oups.write(buffer, 0, i);
			}// end while
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				inps.close();
				oups.flush();
				oups.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static long getXmlObjectSize(org.apache.xmlbeans.XmlObject obj) {
		try {
			StringWriter sw = new StringWriter();
			obj.save(sw, CflowHelper.xmlOptions);
			sw.flush();
			byte[] bytes = sw.toString().getBytes("UTF-8");
			long contentLength = bytes.length;
			bytes = null;
			sw.close();
			sw = null;
			return contentLength;
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
			return 0;
		}
	}
}
