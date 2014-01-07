package com.lkh.cflow.ha;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class RemoteAccess {
	private static HttpClient httpclient = new DefaultHttpClient();
	private static HttpContext localContext = new BasicHttpContext();

	public static RestResponse myGet(URI url) throws Exception {
		return myGet(url, 500, 500);
	}

	public static RestResponse myGet(URI url, int conn_timeout, int so_timeout) throws Exception {
		RestResponse rr = null;
		HttpGet httpGet = new HttpGet(url);
		httpGet.getParams().setParameter("http.socket.timeout", new Integer(so_timeout));
		synchronized (httpclient) {
			try {
				httpclient.getParams().setParameter("http.connection.timeout", new Integer(conn_timeout));
				HttpResponse response = httpclient.execute(httpGet, localContext);

				int statusCode = response.getStatusLine().getStatusCode();
				HttpEntity entity_return = response.getEntity();
				rr = new RestResponse(statusCode, EntityUtils.toString(entity_return));
				EntityUtils.consume(entity_return);
				httpGet.releaseConnection();
			} catch (Exception ex) {
				httpGet.releaseConnection();
				rr = new RestResponse(888, "Exception:" + ex.getLocalizedMessage());
			} finally {
				httpGet.releaseConnection();
			}
		}
		return rr;
	}
}
