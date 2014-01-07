package com.lkh.cflow.storage.oss;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.aliyun.openservices.ClientConfiguration;
import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

public class OssfileStorageManager implements com.lkh.cflow.storage.StorageManagerItf {
	public Logger logger = Logger.getLogger(OssfileStorageManager.class);
	private static String ACCESS_ID = null;
	private static String ACCESS_KEY = null;
	private static String OSS_ENDPOINT = null;
	OSSClient ossClient = null;
	private static String bucketName = null;

	public OssfileStorageManager() {
		ACCESS_ID = CflowHelper.cfg.getString("storage.oss.accessid");
		ACCESS_KEY = CflowHelper.cfg.getString("storage.oss.accesskey");
		OSS_ENDPOINT = CflowHelper.cfg.getString("storage.oss.endpoint", "http://oss.aliyuncs.com/");
		ClientConfiguration config = new ClientConfiguration();
		ossClient = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);
		logger.info("OSS storage manager is ready on " + OSS_ENDPOINT);
		bucketName = CflowHelper.cfg.getString("storage.oss.bucket", "cflow");

	}

	private void ensureBucket(String bucketName) throws OSSException, ClientException {

		if (ossClient.doesBucketExist(bucketName)) {
			return;
		}

		ossClient.createBucket(bucketName);

	}

	public String copyModel(String devId, String modelid, String lang, String toWftid) throws Exception {
		String modelKey = getModelPath(modelid, lang);
		String wftKey = getWftPath(devId, toWftid);
		ossClient.copyObject(bucketName, modelKey, bucketName, wftKey);
		return wftKey;
	}

	public void copy(String pathFrom, String pathTo) throws Exception {
		ossClient.copyObject(bucketName, pathFrom, bucketName, pathTo);
	}

	public boolean doesFileExist(String path) throws Exception {
		return doesOSSObjectExist(path);
	}

	public void delete(String path) {
		deleteOSSObject(path);
	}

	public void download(String path, OutputStream outstream) throws Exception {
		String urlString = OSS_ENDPOINT + bucketName + "/" + path;
		URL aurl = new URL(urlString);
		BufferedInputStream in = new BufferedInputStream(aurl.openStream());
		BufferedOutputStream out = new BufferedOutputStream(outstream);
		inpsToOups(in, out);
		in.close();
		out.flush();
	}

	public void upload(String path, File file, String contentType) throws Exception {
		InputStream inps = new FileInputStream(file);
		long contentLength = file.length();
		upload(path, contentLength, contentType, inps);
		inps.close();
	}

	public void upload(String path, String content, String contentType) throws Exception {
		byte[] bytes = content.getBytes("UTF-8");
		long contentLength = bytes.length;
		InputStream inps = new ByteArrayInputStream(bytes);
		upload(path, contentLength, contentType, inps);
		inps.close();
	}

	public void upload(String path, long contentLength, String contentType, InputStream inputStream) throws Exception {
		ObjectMetadata objMeta = new ObjectMetadata();
		objMeta.setContentLength(contentLength);
		objMeta.setContentType(contentType);
		ossClient.putObject(bucketName, path, inputStream, objMeta);
	}

	public String getPrcPath(String devId, String prcId) {
		return "process/" + devId + "/" + prcId + ".xml";
	}

	public String getWftPath(String devId, String wftId) {
		return "vault/" + devId + "/" + wftId + ".xml";
	}

	public String getIstWftPath(String devId, String prcId) {
		return "istwft/" + devId + "/" + prcId + ".xml";
	}

	public String getVtPath(String devId, String vtname) {
		return "vts/" + devId + "/" + vtname + ".html";
	}

	public String getModelPath(String modelId, String lang) {
		return "model/" + modelId + "_" + lang + ".xml";
	}

	private void inpsToOups(BufferedInputStream inps, BufferedOutputStream oups) {
		BufferedInputStream bufInps = new BufferedInputStream(inps);
		BufferedOutputStream bufOutps = new BufferedOutputStream(oups);
		byte[] buffer = new byte[1024];
		int i = 0;
		try {
			while ((i = bufInps.read(buffer)) != -1) {
				bufOutps.write(buffer, 0, i);
			}// end while
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
		} finally {
			try {
				bufInps.close();
				bufOutps.flush();
				bufOutps.close();
			} catch (IOException ex) {
				logger.error(ex.getLocalizedMessage());
				ex.printStackTrace();
			}
		}
	}

	private boolean doesOSSObjectExist(String key) throws Exception {
		try {
			OSSObject obj = ossClient.getObject(bucketName, key);
			return true;
		} catch (com.aliyun.openservices.ServiceException ex) {
			return false;
		}
	}

	private void deleteOSSObject(String filePath) {
		try {
			ossClient.deleteObject(bucketName, filePath);
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}

	private void ensureFolder(String folderPath) {
		try {
			java.io.File file = new java.io.File(folderPath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Template getTemplate(String devId, String vtname) {
		Properties p = new Properties();
		p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
		p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		p.setProperty(Velocity.RESOURCE_LOADER, "URLResourceLoader");
		p.setProperty("URLResourceLoader.resource.loader.class", "org.apache.velocity.runtime.resource.loader.URLResourceLoader");
		p.setProperty("URLResourceLoader.resource.loader.root", CflowHelper.cfg.getString("storage.oss.endpoint", "http://oss.aliyuncs.com/") + CflowHelper.cfg.getString("storage.oss.bucket", "cflow") + "/vts");

		VelocityEngine ve = new VelocityEngine();
		ve.init(p);
		Template t = null;
		String vtPath = null;
		if (vtname != null && vtname.equals("ERROR")) {
			vtPath = "/error.html";
		} else if (vtname == null || vtname.equals("") || vtname.equalsIgnoreCase("null")) {
			vtPath = "/default.html";
		} else {
			vtPath = "/" + devId + "/" + vtname + ".html";
		}
		try {
			t = ve.getTemplate(vtPath);
		} catch (org.apache.velocity.exception.ResourceNotFoundException rnfex) {
			vtPath = "/" + vtname + ".html";
			try {
				t = ve.getTemplate(vtPath);
			} catch (org.apache.velocity.exception.ResourceNotFoundException rnfex2) {
				t = ve.getTemplate("/default.html");
				logger.error("Get form template: " + rnfex2.getLocalizedMessage());
			}
		}

		return t;
	}

	public void guard(String webPath) {
		IntegrityGuard theGuard = new IntegrityGuard(webPath);
		new Thread(theGuard).start();

	}

	private class IntegrityGuard implements Runnable {
		private String webPath;

		public IntegrityGuard(String path) {
			this.webPath = path;

		}

		public void run() {
			try {
				ensureBucket(bucketName);
				logger.info("Bucket " + bucketName + " guarded.");
			} catch (OSSException e) {
				logger.info("Bucket " + bucketName + " guard failed:" + e.getLocalizedMessage());
			} catch (ClientException e) {
				logger.info("Bucket " + bucketName + " guard failed:" + e.getLocalizedMessage());
			}

			try {
				File vtsFolder = new File(webPath + File.separatorChar + "default" + File.separatorChar + "vts");
				File[] vts = vtsFolder.listFiles();
				for (int i = 0; i < vts.length; i++) {
					if (vts[i].getName().endsWith(".html")) {
						String key = "vts/" + vts[i].getName();
						try {
							OSSObject obj = ossClient.getObject(bucketName, key);
						} catch (Exception ex) {
							logger.warn(ex.getLocalizedMessage());
							uploadFile("vts/" + vts[i].getName(), vts[i], "text/html");
						}
					}
				}

				File modelsFolder = new File(webPath + File.separatorChar + "default" + File.separatorChar + "model");
				File[] models = modelsFolder.listFiles();
				for (int i = 0; i < models.length; i++) {
					if (models[i].getName().endsWith(".xml")) {
						String key = "model/" + models[i].getName();
						try {
							OSSObject obj = ossClient.getObject(bucketName, key);
						} catch (Exception ex) {
							logger.warn(ex.getLocalizedMessage());
							uploadFile("model/" + models[i].getName(), models[i], "text/xml");
						}
					}
				}

				DbAdmin dbadmin = DbAdminPool.get();
				boolean kc = dbadmin.keepConnection(true);
				try {
					dbadmin.getConnection();

					try {
						dbadmin.repaireDevDefaultUser();
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						dbadmin.keepConnection(kc);
					}
				} finally {
					dbadmin.keepConnection(kc);
					DbAdminPool.ret(dbadmin);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private void uploadFile(String key, File file, String contentType) throws OSSException, ClientException, FileNotFoundException {

			ObjectMetadata objectMeta = new ObjectMetadata();
			objectMeta.setContentLength(file.length());
			objectMeta.setContentType(contentType);

			InputStream input = new FileInputStream(file);
			ossClient.putObject(bucketName, key, input, objectMeta);
		}

	}

}
