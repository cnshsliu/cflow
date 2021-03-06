package com.lkh.cflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

import com.lkh.cflow.db.DbAdmin;

public class WftManager {
	static Logger logger = Logger.getLogger(WftManager.class);

	public WorkflowDocument getWftDocumentByID(String devId, String wftId) throws Exception {
		WorkflowDocument wftDoc = null;
		try {
			String path = CflowHelper.storageManager.getWftPath(devId, wftId);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CflowHelper.storageManager.download(path, baos);
			String hello = baos.toString();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			wftDoc = WorkflowDocument.Factory.parse(bais);
			baos.close();
			bais.close();
		} catch (Exception ex) {
			logger.warn("Exception ", ex);
		}
		return wftDoc;
	}

	public WorkflowDocument.Workflow getWftByID(String devId, String wftId) throws Exception {
		WorkflowDocument workflowDoc = getWftDocumentByID(devId, wftId);
		if (workflowDoc == null)
			return null;
		else
			return workflowDoc.getWorkflow();

	}

	public WorkflowDocument getInstanceWftDocumentByID(String devId, String prcId) throws Exception {
		String path = CflowHelper.storageManager.getIstWftPath(devId, prcId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CflowHelper.storageManager.download(path, baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		WorkflowDocument ret = WorkflowDocument.Factory.parse(bais);
		baos.close();
		bais.close();

		return ret;
	}

	public WorkflowDocument.Workflow getInstanceWftByID(String devId, String prcId) throws Exception {
		WorkflowDocument workflowDoc = getInstanceWftDocumentByID(devId, prcId);
		return workflowDoc.getWorkflow();

	}

	public void makeIstWftFromWft(String devId, String wftId, String prcId) throws Exception {
		String wftPath = CflowHelper.storageManager.getWftPath(devId, wftId);
		String istWftPath = CflowHelper.storageManager.getIstWftPath(devId, prcId);
		CflowHelper.storageManager.copy(wftPath, istWftPath);

	}

	public String saveWft(String devId, DbAdmin dbadmin, String wft, String wftId, String wftname) throws Exception {
		// Wftid is null menas this is a new wft
		/*
		 * if(wftId == null){ DbAdmin.userWftExist(usrid, wftname); }
		 */
		if (wftId == null)
			wftId = CflowHelper.myID();

		try {
			String path = CflowHelper.storageManager.getWftPath(devId, wftId);
			CflowHelper.storageManager.upload(path, wft, "text/xml");
			dbadmin.addOrUpdateWftToDeveloper(devId, wftId, wftname);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return wftId;
	}

	/**
	 * @deprecated
	 * 
	 *             简易模式
	 * @param dbadmin
	 * @param template
	 * @param templateId
	 * @param usrid
	 * @param templateName
	 * @return
	 * @throws Exception
	 */
	public String saveIst(DbAdmin dbadmin, String template, String templateId, String usrid, String templateName) throws Exception {
		// Wftid is null menas this is a new wft
		/*
		 * if(wftId == null){ DbAdmin.userWftExist(usrid, wftname); }
		 */
		boolean newCreate;
		templateId = dbadmin.getIstIdByName(usrid, templateName);
		if (templateId == null) {
			templateId = CflowHelper.myID();
			newCreate = true;
		} else {
			newCreate = false;
		}

		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(CflowHelper.getIstPath(usrid, templateId)), "UTF-8");
		try {
			fw.write(template);
			fw.flush();
			fw.close();
			if (newCreate)
				dbadmin.addIstToUser(usrid, templateId, templateName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return templateId;
	}

	/**
	 * Save an instance of workflow tempalte
	 * 
	 * @param wft
	 * @param prcId
	 * @param devId
	 * @return
	 * @throws Exception
	 */
	public String saveInstanceWft(String wft, String prcId, String devId) throws Exception {
		try {
			String path = CflowHelper.storageManager.getIstWftPath(devId, prcId);
			CflowHelper.storageManager.upload(path, wft, "text/xml");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return prcId;
	}

	public Node getNode(WorkflowDocument.Workflow theWft, String nodeid) {
		String queryExpression = "./node[@id='" + nodeid + "']";
		Node[] tmpNodes = queryNodes(theWft, queryExpression);
		return tmpNodes[0];
	}

	public Node[] queryNodes(WorkflowDocument.Workflow wf, String queryString) {
		Node[] nodes = {};
		Object[] objs = wf.selectPath(queryString);
		if (objs == null || objs.length == 0) {
			return nodes;
		} else {
			return (Node[]) objs;
		}
	}

	public String copyModel(String devId, DbAdmin dbadmin, String modelid, String lang) {
		// License control
		// 如果时间过期
		try {
			String newWftid = CflowHelper.myID();
			String destPath = CflowHelper.storageManager.copyModel(devId, modelid, lang, newWftid);

			boolean kc = dbadmin.keepConnection(true);
			String modelName = modelid;
			dbadmin.addOrUpdateWftToDeveloper(devId, newWftid, modelName);
			dbadmin.keepConnection(kc);
			return newWftid;
		} catch (Exception ex) {
			logger.error("Exception message", ex);
			return null;
		}
	}

}
