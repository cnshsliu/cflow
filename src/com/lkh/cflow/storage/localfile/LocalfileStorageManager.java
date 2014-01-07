package com.lkh.cflow.storage.localfile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

public class LocalfileStorageManager implements com.lkh.cflow.storage.StorageManagerItf {
	public Logger logger = Logger.getLogger(com.lkh.cflow.storage.localfile.LocalfileStorageManager.class);

	public LocalfileStorageManager() {
		logger.info("Local storage manager is ready");
	}

	public String copyModel(String devId, String modelid, String lang, String toWftid) throws Exception {
		String modelPath = getModelPath(modelid, lang);
		File srcFile = new File(modelPath);
		File destFile = new File(getWftPath(devId, toWftid));
		logger.info("copy " + modelPath + " to " + destFile.getPath());
		org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
		return destFile.getPath();
	}

	public void copy(String pathFrom, String pathTo) throws Exception {
		File srcFile = new File(pathFrom);
		File destFile = new File(pathTo);
		org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
	}

	public boolean doesFileExist(String path) throws Exception {
		return new File(path).exists();
	}

	public void delete(String path) {
		File f = new File(path);
		f.delete();
	}

	public void download(String path, OutputStream outstream) throws Exception {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
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
		OutputStream oups = new FileOutputStream(path);
		inpsToOups(inputStream, oups);
		oups.close();
	}

	public String getVtPath(String devId, String vtname) {
		String ret = "";
		String folderPath = "";
		folderPath = CflowHelper.cfg.getString("storage.local.vt") + File.separatorChar + devId;
		ensureFolder(folderPath);
		ret = folderPath + File.separatorChar + vtname + ".html";
		return ret;
	}

	public String getPrcPath(String devId, String prcId) {
		String ret = "";
		String folderPath = CflowHelper.cfg.getString("storage.local.prc") + File.separatorChar + devId;
		ensureFolder(folderPath);
		ret = folderPath + File.separatorChar + prcId + ".xml";
		return ret;
	}

	public String getModelPath(String modelId, String lang) {
		String ret = CflowHelper.cfg.getString("storage.local.model") + File.separatorChar + modelId + "_" + lang + ".xml";
		return ret;
	}

	public String getWftPath(String devId, String wftId) {
		String ret = "";
		String folderPath = CflowHelper.cfg.getString("storage.local.wft") + File.separatorChar + devId;
		ensureFolder(folderPath);
		ret = folderPath + File.separator + wftId + ".xml";
		return ret;
	}

	public String getIstWftPath(String devId, String prcId) {
		String ret = "";
		String folderPath = "";
		folderPath = CflowHelper.cfg.getString("storage.local.istwft") + File.separatorChar + devId;
		ensureFolder(folderPath);
		ret = folderPath + File.separatorChar + prcId + ".xml";
		return ret;
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

	private void inpsToOups(InputStream inps, OutputStream oups) {
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

	public Template getTemplate(String devId, String vtname) {
		Properties p = new Properties();
		p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
		p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		p.setProperty(Velocity.RESOURCE_LOADER, "file");
		p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		p.setProperty("file.resource.loader.path", CflowHelper.cfg.getString("storage.local.vt"));
		p.setProperty("file.resource.loader.cache", "false");
		p.setProperty("file.resource.loader.modificationCheckInterval", "0");

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
				ensureFolder(CflowHelper.cfg.getString("storage.local.wft"));
				ensureFolder(CflowHelper.cfg.getString("storage.local.ist"));
				ensureFolder(CflowHelper.cfg.getString("storage.local.prc"));
				ensureFolder(CflowHelper.cfg.getString("storage.local.istwft"));
				ensureFolder(CflowHelper.cfg.getString("storage.local.vt"));
				ensureFolder(CflowHelper.cfg.getString("storage.local.model"));
				FileUtils.copyDirectory(new File(webPath + File.separatorChar + "default" + File.separatorChar + "vts"), new File(CflowHelper.cfg.getString("storage.local.vt")));
				FileUtils.copyDirectory(new File(webPath + File.separatorChar + "default" + File.separatorChar + "model"), new File(CflowHelper.cfg.getString("storage.local.model")));

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
	}
}
