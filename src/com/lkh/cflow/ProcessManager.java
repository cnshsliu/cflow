package com.lkh.cflow;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.xmlbeans.XmlObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import com.lkh.cflow.Work.Options;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;
import com.lkh.cflow.exception.CflowException;
import com.lkh.cflow.web.Input;

public class ProcessManager {
	static Logger logger = Logger.getLogger(ProcessManager.class);
	static Input myInput = new Input();

	private String acquireTask(String devId, DbAdmin dbadmin, String prcId, String wftId, Work theWork, String actor) {
		String msg = null;
		Taskto[] tasktos = theWork.getTasktoArray();
		int acqNumber = 0;
		for (int i = 0; i < tasktos.length; i++) {
			if (tasktos[i].getStatus().equals(CflowHelper.TASK_ACQUIRED)) {
				acqNumber++;
			}
		}
		if (acqNumber >= theWork.getAcqThreshold()) {
			msg = "There is no more acquire slot.";
		} else {
			for (int i = 0; i < tasktos.length; i++) {
				if (tasktos[i].getWhom().equals(actor) && tasktos[i].getStatus().equals(CflowHelper.TASK_RUNNING)) {
					tasktos[i].setStatus(CflowHelper.TASK_ACQUIRED);
					dispatchTask(devId, dbadmin, prcId, wftId, theWork, tasktos[i], "SYSTEM", CflowHelper.UPDATE_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
					acqNumber++;
					break;
				}
			}

			if (acqNumber == theWork.getAcqThreshold()) {
				for (int i = 0; i < tasktos.length; i++) {
					if (tasktos[i].getStatus().equals(CflowHelper.TASK_RUNNING)) {
						tasktos[i].setStatus(CflowHelper.TASK_DEFERRED);
						dispatchTask(devId, dbadmin, prcId, wftId, theWork, tasktos[i], "SYSTEM", CflowHelper.UPDATE_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
					}
				}
			}
			msg = null;
		}

		return msg;
	}

	public String acquireTask(String devId, DbAdmin dbadmin, String prcId, String sessid, String actor) {
		String ret = null;
		ProcessDocument prcDoc;
		try {
			prcDoc = getProcessDocument(devId, dbadmin, prcId);
			ProcessT prc = prcDoc.getProcess();
			String qe = "./work[@sessid='" + sessid + "']";
			Work[] works = (Work[]) queryChildren(prc, qe);
			for (int i = 0; i < works.length; i++) {
				ret = acquireTask(devId, dbadmin, prcId, works[i].getSessid(), actor);
			}
			saveProcessDoc(devId, prcDoc, prcId);
		} catch (Exception e1) {
			ret = "Server error: " + e1.getLocalizedMessage();
			logger.error(e1.getLocalizedMessage());
		}

		return ret;
	}

	// Called in createTask_TASK
	private void addTasktoToWork(String devId, DbAdmin dbadmin, String addby, String prcId, String wftId, String startBy, Work theWork, String nodeId, java.util.Date dueDate, String theOneWilldo) throws SQLException {
		Taskto taskto = theWork.addNewTaskto();
		taskto.setType(CflowHelper.TASKTO_TYPE_PERSON);
		taskto.setWhom(theOneWilldo);

		// 检查用户是否存在
		/*
		 * User participantUser = dbadmin.getDevById(participant); if
		 * (participantUser == null) { User addbyUser =
		 * dbadmin.getDevById(addby); dbadmin.createUserDirectly(participant,
		 * participant, participant, participant, participant, addbyUser.orgid,
		 * addbyUser.tzId, addbyUser.lang); }
		 */

		// Taskto的status对模板来说没有意义
		taskto.setStatus(CflowHelper.TASK_RUNNING);
		// inform to this person by add processid to his/her profile XML.
		String tid = dispatchTask(devId, dbadmin, prcId, wftId, theWork, taskto, "SYSTEM", CflowHelper.NEW_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
		/*
		 * String doers[] = new String[1]; doers[0] = participant;
		 * dbadmin.associatePrcToUsers(devId, doers, prcId, wftId,
		 * CflowHelper.ASSOC_TYPE_USER);
		 */
		try {
			String[] delegatee = yarkPeriodDelegate(devId, dbadmin, prcId, wftId, theWork, taskto);
			if (dueDate != null) {
				dbadmin.watchTimeControl(theOneWilldo, prcId, nodeId, dueDate);
				dbadmin.watchTimeControl(delegatee, prcId, nodeId, dueDate);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean alreadyAcquiredByMe(ProcessT prc, Work theWork, String actor, String delegaterid) {
		if (!theWork.getAcquirable())
			return false;

		Taskto[] tasktos = theWork.getTasktoArray();
		for (int i = 0; i < tasktos.length; i++) {
			if (delegaterid == null) {
				if (tasktos[i].getStatus().equals(CflowHelper.TASK_ACQUIRED) && (tasktos[i].getWhom().equals(actor))) {
					return true;
				}
			} else {
				if (tasktos[i].getStatus().equals(CflowHelper.TASK_ACQUIRED) && (tasktos[i].getWhom().equals(actor) || tasktos[i].getWhom().equals(delegaterid))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean canAcquireTask(ProcessT prc, Work theWork, String actor, String delegaterid) {
		Taskto[] tasktos = theWork.getTasktoArray();
		int acqNumber = 0;
		for (int i = 0; i < tasktos.length; i++) {
			if (tasktos[i].getStatus().equals(CflowHelper.TASK_ACQUIRED)) {
				acqNumber++;
			}
		}
		if (acqNumber >= theWork.getAcqThreshold()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 按用户，按进程状态 来计算进程数量
	 * 
	 * @param dbadmin
	 * @param actor
	 * @param status
	 * @return
	 */
	public int countUserProcesses(String devId, DbAdmin dbadmin, String usrId, String status) {
		PrcInfo[] prcInfos;
		try {
			// TODO: Optimize to use SQL Count(*)
			prcInfos = dbadmin.getPrcUserAssociations(devId, usrId, status);
			return prcInfos.length;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * 按用户，按进程状态，按用户与进程之间的关系（参与者还是启动者）来计算进程数量
	 * 
	 * @param dbadmin
	 * @param actor
	 * @param status
	 * @param usertype
	 * @return
	 */
	public int countUserProcesses(DbAdmin dbadmin, String actor, String status, String usertype) {
		PrcInfo[] prcinfo;
		try {
			// TODO: Optimize to use SQL Count(*)
			prcinfo = dbadmin.getPrcUserAssociations(actor, status, usertype);
			return prcinfo.length;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * 开始一个新进程节点
	 * 
	 * @param dbadmin
	 * @param prcId
	 * @param prc
	 * @param theWf
	 * @param theNode
	 * @param actor
	 * @param tzId
	 */
	private String createTask(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		if (theNode.getType().equals(CflowHelper.NODE_TYPE_START)) {
			ret = createTask_START(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_NOTIFY)) {
			ret = createTask_NOTIFY(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_AND)) {
			ret = createTask_AND(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_OR)) {
			ret = createTask_OR(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_ROUND)) {
			ret = createTask_ROUND(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_SCRIPT)) {
			ret = createTask_SCRIPT(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_END)) {
			ret = createTask_END(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_GROUND)) {
			ret = createTask_GROUND(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_SUB)) {
			ret = createTask_SUB(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_TIMER)) {
			ret = createTask_TIMER(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_TASK)) {
			ret = createTask_TASK(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		}

		return ret;
	}

	private String createTask_AND(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		// Find all works points to AND
		String queryExpression = "./node/next[@targetID='" + theNode.getId() + "']/..";
		Node[] nodes = CflowHelper.wftManager.queryNodes(theWf, queryExpression);
		boolean allFinished = true;
		for (int i = 0; i < nodes.length; i++) {
			// 按照nodeid来找work,在这里是可以的。
			queryExpression = "./work[@nodeid='" + nodes[i].getId() + "'][@roundid='" + prc.getRoundid() + "']";
			try {
				// Do NOT change it to CflowHelper.queryWorks
				XmlObject[] objects = prc.selectPath(queryExpression);
				if (objects == null) {
					allFinished = false;
					break;
				} else if (objects.length == 0) {
					allFinished = false;
					break;
				} else {
					Work[] works = (Work[]) objects;
					if (!works[0].getStatus().equals(CflowHelper.WORK_FINISHED)) {
						allFinished = false;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (allFinished) {
			yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
			ret = yarkNextTask(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		}

		return ret;

	}

	private String createTask_END(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		logger.debug("Ending a process, wftname:" + prc.getWftname() + ", prcid:" + prc.getId() + ",parentProcessId:" + prc.getParentProcessId());
		boolean kc = dbadmin.keepConnection(true);
		try {
			prc.setStatus(CflowHelper.PROCESS_FINISHED);
			prc.setEndat(Calendar.getInstance());
			try {
				logger.debug("\tUpdate database process log:" + prc.getId());
				dbadmin.updateProcessLog(prc.getId(), prc.getStartby(), prc.getStartat(), prc.getEndat(), prc.getStatus());
				// DbAdmin.removePrcIdFromUser(actor, prcId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
			logger.debug("\tClear tasks related to this process:" + prc.getId());
			dbadmin.removeProcessTaskto(prc.getId());
			// Clear cftimer
			try {
				logger.debug("\tClear cftimers:" + prc.getId());
				dbadmin.clearCfTimer(prc.getId());
			} catch (Exception e) {
				logger.error("\t\tException", e);
			}
			// Clear repeated cron job.
			try {
				logger.debug("\tClear processCronJob:" + prc.getId());
				CflowHelper.schManager.clearProcessCronJob(prc.getId());
			} catch (Exception e) {
				logger.error("\t\tException", e);
			}

			if (isSubProcess(prc)) {
				String parentProcessId = prc.getParentProcessId();
				String parentProcessNodeId = prc.getParentProcessNodeId();
				String parentProcessSessId = prc.getParentProcessSessId();
				logger.debug("\tThis is a child process, return to parent " + parentProcessId);

				JSONObject attMap = getProcessAttachments(prc);
				String option = CflowHelper.DEFAULT_OPTION;
				if (attMap.containsKey("RETURN_TO_PARENT")) {
					option = (String) attMap.get("RETURN_TO_PARENT");
				}

				// Return to parent process.
				try {
					logger.debug("\t\tReturn to parent:" + parentProcessId + " with value:" + option);
					doTask(devId, dbadmin, actor, parentProcessId, parentProcessNodeId, parentProcessSessId, option, attMap);
				} catch (CflowException ex) {
					ex.printStackTrace();
				}
				logger.debug("Ended and return to parent");
			} else {
				logger.debug("Ended (not a child process).");
			}
		} finally {
			dbadmin.keepConnection(kc);
		}

		return ret;
	}

	private String createTask_GROUND(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		logger.debug("createTask_GROUND");
		return ret;
	}

	private String createTask_NOTIFY(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		Sendmail sendmail = theNode.getSendmail();
		if (sendmail != null) {
			String[] emailAddresses = CflowHelper.gm.getEmailAddresses(dbadmin, prc, sendmail);
			// CflowHelper.gm.sendMail(devId, sendmail, emailAddresses);
		}

		yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
		ret = yarkNextTask(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		return ret;
	}

	private String createTask_OR(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = null;
		String queryExpression = "./node/next[@targetID='" + theNode.getId() + "']/..";
		Node[] nodes = CflowHelper.wftManager.queryNodes(theWf, queryExpression);
		boolean anyFinished = false;
		Vector<String> tmp = new Vector<String>();
		for (int i = 0; i < nodes.length; i++) {
			queryExpression = "./work[@nodeid='" + nodes[i].getId() + "'][@roundid='" + prc.getRoundid() + "']";
			Work[] works = queryWorks(prc, queryExpression);
			if (works.length > 0) {
				if (works[0].getStatus().equals(CflowHelper.WORK_FINISHED)) {
					anyFinished = true;
				} else {
					if (works[0].getStatus().equals(CflowHelper.WORK_RUNNING)) {
						works[0].setStatus(CflowHelper.WORK_IGNORED);
						if (works[0].getType().equals(CflowHelper.NODE_TYPE_TASK)) {
							tmp.add(works[0].getSessid());
						}
					}
				}
			}
		}
		if (anyFinished) {
			// Bug fix: any finished, then remove other works
			Iterator<String> itr = tmp.iterator();
			while (itr.hasNext()) {
				dbadmin.removeWorkTaskto((String) itr.next());
			}

			yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
			ret = yarkNextTask(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		}

		return ret;

	}

	private String createTask_ROUND(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		prc.setRoundid(CflowHelper.myID());

		if (theNode.getRepeatthresholdtype().equals(CflowHelper.REPEAT_TYPE_NOREPEAT)) {
			yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
			ret = yarkNextTaskWithOption(devId, dbadmin, prcId, prc, theWf, theNode, actor, CflowHelper.DEFAULT_OPTION);
		} else {
			Work theWork = yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
			startRepeater(devId, dbadmin, actor, prc.getId(), theWork, theNode);
		}

		/*
		 * if(reachThreshold.equals("REPEAT_FINISH")){ //结束Repeater
		 * yarkLogicTask(prc, theNode, CflowHelper.REPEAT_OPTION_FINISH);
		 * yarkNextTaskWithOption(dbadmin, prcId, prc, theWf, theNode, actor,
		 * CflowHelper.REPEAT_OPTION_FINISH); }
		 */

		return ret;
	}

	@SuppressWarnings("unchecked")
	private String createTask_SCRIPT(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		String caculatedDecision = CflowHelper.DEFAULT_OPTION;
		JSONObject retObj = null;
		try {
			String script = theNode.getScript().getStringValue();
			// 为Javascript准备数据。
			// 为Java准备数据。
			// 1. 先找<process><attachment>, 放到global_*中
			JSONObject ctx = new JSONObject();
			String queryExp = "./attachment";
			Attachment[] gatts = queryPrcAttachments(prc, queryExp);
			JSONObject globalAttach = new JSONObject();
			for (int i = 0; i < gatts.length; i++) {
				if (!StringUtils.isBlank(gatts[i].getAttname())) {
					ParsedValue pv = CflowHelper.getValueObject(gatts[i]);
					ctx.put("global_" + gatts[i].getAttname(), pv.value);
					if (!pv.noProblem) {
						prc.setLastError(StringUtils.defaultIfEmpty(prc.getLastError(), "") + "|" + pv.error);
					}
				}
			}
			// 找出本round中所有attachments.
			// 2. 再找<process><work roundid=CURRENT_ROUND><log><attachemnt>
			String queryExpression = "./work[@roundid='" + prc.getRoundid() + "']/log/attachment";
			Attachment[] atts = queryPrcAttachments(prc, queryExpression);
			for (int i = 0; i < atts.length; i++) {
				if (!StringUtils.isBlank(atts[i].getAttname())) {
					String val = atts[i].getValue();
					ParsedValue pv = CflowHelper.getValueObject(atts[i]);
					ctx.put(atts[i].getAttname(), pv.value);
					if (!pv.noProblem) {
						prc.setLastError(StringUtils.defaultIfEmpty(prc.getLastError(), "") + "|" + pv.error);
					}
				}
			}

			// 3. 最后，把PRC, 和PBO放进去
			JSONObject prcContextJSON = CflowHelper.getPrcContextJSON(prc, theWf, theNode);
			ctx.put("PRC", prcContextJSON.toJSONString());
			ctx.put("PBO", prc.getPbo());

			// 调用Java方法来实现
			String returnedJSONString = null;
			String scriptString = "";
			int scriptType = 0;
			if (script.startsWith("JAVA:")) { // Java方式
				scriptType = 0;
				try {
					BufferedReader reader = new BufferedReader(new StringReader(script));
					String str = reader.readLine();
					str = str.substring(5);
					scriptString = str;
					returnedJSONString = AdapterCommander.runAdaptor(str, ctx);
					logger.debug("call JAVA:" + str);
					logger.debug("return:[" + returnedJSONString + "]");
				} catch (Exception ex) {
					String tmp = ex.getLocalizedMessage();
					logger.error(tmp);
					tmp = "run Java[" + scriptString + "] failed." + StringUtils.replace(tmp, "\"", "'");
					prc.setLastError(tmp);
					returnedJSONString = "{\"RETURN\":\"onerror\",\"error\":\"" + tmp + "\"}";
				}
			} else if (script.startsWith("URL:")) {
				scriptType = 1;
				try {
					BufferedReader reader = new BufferedReader(new StringReader(script));
					String str = reader.readLine();
					str = str.substring(4);
					scriptString = str;
					returnedJSONString = CflowHelper.remoteCall(str, ctx);
					logger.debug("call URL:" + str);
					logger.debug("return:[" + returnedJSONString + "]");
				} catch (Exception ex) {
					String tmp = ex.getLocalizedMessage();
					logger.error(tmp);
					tmp = "run WS[" + scriptString + "] failed." + StringUtils.replace(tmp, "\"", "'");
					prc.setLastError(tmp);
					returnedJSONString = "{\"RETURN\":\"onerror\",\"error\":\"" + tmp + "\"}";
				}
			} else { // JavaScript
						// 使用JavaScript来实现。
						// checkScript(script, valMap);
				scriptType = 2;
				try {
					scriptString = script;
					returnedJSONString = CflowHelper.scriptHandler.runScriptNode(scriptString, ctx);
					logger.debug("call JS:" + scriptString);
					logger.debug("return:[" + returnedJSONString + "]");
				} catch (Exception ex) {
					String tmp = ex.getLocalizedMessage();
					logger.error(tmp);
					tmp = "run javascript failed." + StringUtils.replace(tmp, "\"", "'");
					prc.setLastError(tmp);
					returnedJSONString = "{\"RETURN\":\"onerror\",\"error\":\"" + tmp + "\"}";
				}
			}
			try {
				JSONParser parser = new JSONParser();
				retObj = (JSONObject) parser.parse(returnedJSONString);

				caculatedDecision = (String) retObj.get("RETURN");
				if (StringUtils.isEmpty(caculatedDecision))
					caculatedDecision = "DEFAULT";
				if (caculatedDecision.equals("onerror")) {
					String tmp = (String) retObj.get("error");
					prc.setLastError(tmp);
				} else if (caculatedDecision.equals("timeout")) {
					String tmp = (String) retObj.get("error");
					prc.setLastError(tmp);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.debug(ex.getLocalizedMessage());
				retObj = new JSONObject();
				retObj.put("RETURN", "onerror");
				String myerror = "Parse script return value exception.";
				if (scriptType == 0) {
					myerror += "JAVA:[" + scriptString + "]";
				} else if (scriptType == 1) {
					myerror += "WS:[" + scriptString + "]";
				} else {
					myerror += "JS:[check your Javascript code]";
				}
				retObj.put("error", myerror);
				prc.setLastError(myerror);
				caculatedDecision = "onerror";
			}

			Work theWork = yarkLogicTask(prc, theNode, caculatedDecision);
			Log theLog = theWork.addNewLog();
			theLog.setUsrid(actor);
			theLog.setTimestamp(Calendar.getInstance());
			theLog.setOption(caculatedDecision);

			// copy back to process
			for (Iterator i = retObj.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				if (!StringUtils.isBlank(key) && !key.equals("RETURN")) {
					boolean copied = false;
					for (int j = 0; j < atts.length; j++) {
						if (!StringUtils.isBlank(atts[j].getAttname())) {
							if (atts[j].getAttname().equals(key)) {
								atts[j].setValue(retObj.get(key).toString());
								copied = true;
								// TODO:
								// 此处可能存在retObj.get(key)的类型与atts[j]的类型不符，需要处理。
								// 不过，只要att定义了类型，传进来的数据就应该与其一直，转成String不应该有问题
								// 似乎可以不用处理。
							}
						}
					}
					if (!copied && theLog != null) {
						Attachment att = theLog.addNewAttachment();
						att.setUsrid(actor);
						att.setAttname(key);
						Object obj1 = retObj.get(key);
						att.setValue(obj1.toString());
						att.setType(CflowHelper.getReturedValueType(obj1));
						att.setLabel(key);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception", e);
			prc.setLastError(e.getLocalizedMessage());
			caculatedDecision = CflowHelper.ERROR_OPTION;
		}

		ret = yarkNextTaskWithOption(devId, dbadmin, prcId, prc, theWf, theNode, actor, caculatedDecision);
		return ret;

		/*
		 * boolean foundNextWithOption=false; NextType nexts[] =
		 * (NextType[])theNode.getNextArray(); for(int i=0; i<nexts.length;
		 * i++){ if(nexts[i].getOption().equals(caculatedDecision)){
		 * foundNextWithOption = true; break; } } if(foundNextWithOption){
		 * yarkLogicTask(prc, theNode, caculatedDecision);
		 * yarkNextTaskWithOption(devId, dbadmin, prcId, prc, theWf, theNode,
		 * actor, caculatedDecision); }
		 */
	}

	private String createTask_START(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
		ret = yarkNextTask(devId, dbadmin, prcId, prc, theWf, theNode, actor);
		return ret;
	}

	private String createTask_SUB(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		Work theWork = yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
		// 子活动与父活动使用同样的team
		// startWorkflow(dbadmin, theNode.getSubWftUID(), theNode.getSubWftWG(),
		// CflowHelper.myID();
		// actor, prc.getWftname() + "-" + theWf.getName(), null, prc.getId(),
		// theNode.getId(), CflowHelper.myID(),
		// tzId);
		String parentProcessId = prc.getId();
		String parentProcessNodeId = theNode.getId();
		String parentProcessSessId = theWork.getSessid();
		String subPrcId = startWorkflow(devId, dbadmin, theNode.getSubWftUID(), prc.getTeamid(), actor, prc.getWftname() + "-" + theWf.getName(), null, parentProcessId, parentProcessNodeId, parentProcessSessId);
		if (subPrcId.startsWith("ERROR:")) {
			// Start child process failed.
			logger.error("Start sub process [" + theNode.getSubWftUID() + "] failed." + subPrcId.substring(6));
			ret = yarkNextTaskWithOption(devId, dbadmin, prcId, prc, theWf, theNode, actor, "onerror");
		} else {
			logger.debug("Start sub process [" + theNode.getSubWftUID() + "] successed. sub process id: " + subPrcId);
			ret = subPrcId;
		}
		return ret;
	}

	private String createTask_TASK(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		Work theWork = null;

		theWork = prc.addNewWork();
		theWork.setRoundid(prc.getRoundid());
		theWork.setSessid(CflowHelper.myID());
		theWork.setName(theNode.getName());
		theWork.setTitle(theNode.getTitle());
		theWork.setType(theNode.getType());
		theWork.setNodeid(theNode.getId());
		theWork.setCreatedAt(Calendar.getInstance());
		theWork.setMpsm(theNode.getMpsm());
		theWork.setMpcdc(theNode.getMpcdc());
		theWork.setOec(theNode.getOec());
		theWork.setAcquirable(theNode.getAcquirable());
		theWork.setAcqThreshold(theNode.getAcqThreshold());
		theWork.setAllowDelegate(theNode.getAllowDelegate());
		theWork.setAllowAdhoc(theNode.getAllowAdhoc());
		theWork.setStatus(CflowHelper.WORK_RUNNING);
		theWork.setForm(theNode.getForm());
		if (!StringUtils.isBlank(theNode.getCustomizer()))
			theWork.setCustomizer(theNode.getCustomizer());
		//
		// Timing Control
		java.util.Date dueDate = null;
		if (theNode.isSetTimeingType()) {
			Calendar targetTime = Calendar.getInstance();
			if (theNode.getTimeingType().equals("process")) {
				targetTime = prc.getStartat();
			}
			if (theNode.getYy().intValue() != 0) {
				targetTime.add(Calendar.YEAR, theNode.getYy().intValue());
			}
			if (theNode.getMm().intValue() != 0) {
				targetTime.add(Calendar.MONTH, theNode.getMm().intValue());
			}
			if (theNode.getDd().intValue() != 0) {
				targetTime.add(Calendar.DATE, theNode.getDd().intValue());
			}
			if (theNode.getHh().intValue() != 0) {
				targetTime.add(Calendar.HOUR, theNode.getHh().intValue());
			}
			if (theNode.getMi().intValue() != 0) {
				targetTime.add(Calendar.MINUTE, theNode.getMi().intValue());
			}

			dueDate = targetTime.getTime();
		}

		Taskto[] nodeTasktos = theNode.getTasktoArray();
		// Decide the taskto (participants) for process.
		// A process can only have taskto with a type of
		// CflowHelper.TASKTO_TYPE_PERSON.
		for (int i = 0; i < nodeTasktos.length; i++) {
			Membership[] pats = getTaskDoers(dbadmin, prc, nodeTasktos[i]);
			for (int m = 0; m < pats.length; m++) {
				try {
					addTasktoToWork(devId, dbadmin, actor, prc.getId(), prc.getWftid(), prc.getStartby(), theWork, theNode.getId(), dueDate, pats[m].getUserIdentity());
				} catch (SQLException e) {
					logger.error("Exception:", e);
				}
			}
		}
		// Deal with preactions
		/*
		 * RobotActions preActions = theNode.getPreactions(); if (preActions !=
		 * null) { Sendmail sendmail = preActions.getSendmail(); if (sendmail !=
		 * null) { // CflowHelper.gm.initComposer(dbadmin, prcId, prc, theWf, //
		 * theNode, actor); CflowHelper.gm.sendMail(sendmail,
		 * CflowHelper.gm.getEmailAddresses(dbadmin, prc, sendmail)); } Sendsms
		 * sendsms = preActions.getSendsms(); if (sendsms != null) {
		 * CflowHelper.gm.sendSMS(sendsms); } }
		 */
		// Copy attachments from node to work.
		/*
		 * Log log = theWork.addNewLog(); log.setUsrid("SYSTEM");
		 * log.setTimestamp(Calendar.getInstance());
		 * log.setOption(CflowHelper.DEFAULT_OPTION); Attachment[] attachments =
		 * theNode.getAttachmentArray(); for(int i=0; i<attachments.length;
		 * i++){ Attachment attachment = log.addNewAttachment();
		 * attachment.setType(attachments[i].getType());
		 * attachment.setLabel(attachments[i].getLabel());
		 * attachment.setAttname(attachments[i].getAttname());
		 * attachment.setValue(attachments[i].getValue());
		 * attachment.setUsrid("SYSTEM"); }
		 */
		Attachment[] attachments = theNode.getAttachmentArray();
		for (int i = 0; i < attachments.length; i++) {
			Attachment attachment = theWork.addNewAttachment();
			attachment.setType(attachments[i].getType());
			attachment.setLabel(attachments[i].getLabel());
			attachment.setAttname(attachments[i].getAttname());
			attachment.setValue(attachments[i].getValue());
			attachment.setUsrid("SYSTEM");
		}

		// Determine what options should be put into work
		NextType[] nexts = (NextType[]) theNode.getNextArray();
		int nonDefaultOptionsCount = 0;
		for (int i = 0; i < nexts.length; i++) {
			if (!nexts[i].getOption().equals(CflowHelper.DEFAULT_OPTION)) {
				nonDefaultOptionsCount++;
			}
		}
		if (nonDefaultOptionsCount > 0) {
			Options options = theWork.addNewOptions();
			for (int i = 0; i < nexts.length; i++) {
				if (!nexts[i].getOption().equals(CflowHelper.DEFAULT_OPTION)) {
					options.addOption(nexts[i].getOption());
				}
			}
		}

		return theWork.getSessid();

	}

	private String createTask_TIMER(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		Work theWork = yarkLogicTask(prc, theNode, CflowHelper.DEFAULT_OPTION);
		startTimer(devId, dbadmin, actor, prc.getId(), theWork, theNode);
		return "";
		// 这里没有yarkNextTask, 所以不会安排后续活动，这样，流程就被停在这里。
		// 在Quartz中将检查Timer是否过期，如果过期，则执行后续工作

	}

	/**
	 * 将单个工作项委托给他人处理
	 * 
	 * @param dbadmin
	 *            DbAdmin实例
	 * @param prc
	 *            工作项所在的进程
	 * @param theWork
	 *            当前工作项
	 * @param delegater
	 *            委托人（当前工作项的执行者）
	 * @param delegatee
	 *            被委托人。委托成功后，该工作项由被委托人来处理
	 * @throws CflowException
	 */
	public void delegateOneTask(String devId, DbAdmin dbadmin, String prcId, String wftId, Work theWork, String delegater, String delegatee) throws CflowException {
		String qe = "";

		qe = "./taskto[@whom='" + delegater + "'][@status='" + CflowHelper.TASK_RUNNING + "']";
		Taskto[] taskto = (Taskto[]) queryChildren(theWork, qe);
		for (int i = 0; i < taskto.length; i++) {
			taskto[i].setStatus(CflowHelper.TASK_DELEGATED);
			dispatchTask(devId, dbadmin, prcId, wftId, theWork, taskto[i], "SYSTEM", CflowHelper.UPDATE_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
			Taskto newTaskto = theWork.addNewTaskto();
			newTaskto.setType(CflowHelper.TASKTO_TYPE_PERSON);
			newTaskto.setWhom(delegatee);
			newTaskto.setStatus(CflowHelper.TASK_RUNNING);
			String tid = dispatchTask(devId, dbadmin, prcId, wftId, theWork, newTaskto, delegater, CflowHelper.NEW_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
		}
	}

	public String dispatchTask(String devId, DbAdmin dbadmin, String prcId, String wftId, Work theWork, Taskto taskto, String delegaterid, boolean isUpdate, String userOrStarter) {
		String tid = dbadmin.logTaskto(devId, prcId, wftId, theWork, taskto, delegaterid, isUpdate, userOrStarter);
		if (taskto.getStatus().equals(CflowHelper.TASK_RUNNING)) {
			try {

				String[] recipients = new String[1];
				// TODO: sendmail
				// Get is email enabled for devId.
				// Get is email should be send for theWork
				// Get is email is enabled for taskto.getWhom()
				// Add sendemail quota for devId
				// need new table to how it.
				/*
				 * User toUser = dbadmin.getUserByIdentity(devId,
				 * taskto.getWhom()); boolean sendEmail = false; if (toUser ==
				 * null) { sendEmail = false; } else if
				 * (toUser.notify.equals(CflowHelper.NOTIFY_EMAIL)) {
				 * recipients[0] = toUser.email; sendEmail = true; }
				 * 
				 * if (sendEmail) { String subject =
				 * "Pass8t: You have new task to do";
				 * 
				 * String message = theWork.getName() +
				 * "\nVisit the following URL to see it:\n" + forwardUrl; String
				 * html = theWork.getName() + "<BR><a href='" + forwardUrl +
				 * "'>Click here to see it</a><BR><BR>" +
				 * "If your email client does not support link, " +
				 * "please copy the following address to your browser address to visit it: <BR>"
				 * + forwardUrl; CflowHelper.gm.sendMail(recipients, subject,
				 * message, html); }
				 */
			} catch (Exception ex) {
				logger.warn("Sending email: " + ex.getLocalizedMessage());
			}
		}
		return tid;
	}

	public void doCronJob(JobDataMap dataMap, String option) {
		String devId = dataMap.getString("DEVID");
		String actor = dataMap.getString("DOER");
		String prcId = dataMap.getString("PRCID");
		String sessid = dataMap.getString("SESSID");
		String nodeid = dataMap.getString("NODEID");
		String tzId = dataMap.getString("TZID");
		Object lock = null;
		synchronized (CflowHelper.lruDocLock) {
			lock = CflowHelper.lruDocLock.get(prcId);
			if (lock == null) {
				lock = new Object();
				CflowHelper.lruDocLock.put(prcId, lock);
			}

		}
		synchronized (lock) {
			DbAdmin dbadmin = DbAdminPool.get();
			try {
				dbadmin.keepConnection(true);
				try {
					String prcStatus = dbadmin.getProcessStatus(devId, prcId);
					if (prcStatus == null) {// Not exist in Database
						CflowHelper.schManager.clearProcessCronJob(prcId);
					} else if (prcStatus.equals(CflowHelper.PROCESS_RUNNING)) {
						boolean prcFileExist = CflowHelper.storageManager.doesFileExist(CflowHelper.storageManager.getPrcPath(devId, prcId));
						if (prcFileExist) {
							logger.debug("doRepeatedJob: USERID[" + actor + "] PRCID[" + prcId + "] SESSID[" + sessid + "] TZID[ " + tzId + "]");
							doTask(devId, dbadmin, actor, prcId, nodeid, sessid, option, null);
						} else { // PRC file does NOT exist
							CflowHelper.schManager.clearProcessCronJob(prcId);
						}
					} else if (prcStatus.equals(CflowHelper.PROCESS_FINISHED) || prcStatus.equals(CflowHelper.PROCESS_CANCELED)) {
						CflowHelper.schManager.clearProcessCronJob(prcId);
					}
				} catch (Exception e) {
					logger.error("Exception", e);
				}
			} finally {
				DbAdminPool.ret(dbadmin);
			}
		}
	}

	public String doTask(String devId, DbAdmin dbadmin, String actor, String prcId, String nodeid, String sessid, String optionPicked, JSONObject taskInput) throws CflowException {
		String ret = null;
		// ret = dbadmin.send_DoTask_toFrontDesk(devId, actor, prcId, nodeid,
		// sessid, optionPicked, taskInput);
		ret = _XXX_doTask(devId, dbadmin, actor, prcId, nodeid, sessid, optionPicked, taskInput);

		return ret;
	}

	public String _XXX_doTask(String devId, DbAdmin dbadmin, String actor, String prcId, String nodeid, String sessid, String optionPicked, JSONObject taskInput) throws CflowException {
		String doTaskRet = "";
		try {

			ProcessDocument prcDoc = getProcessDocument(devId, dbadmin, prcId);
			ProcessT prc = prcDoc.getProcess();
			// ////////
			String nodeName = "";
			WorkflowDocument.Workflow theWf = CflowHelper.wftManager.getInstanceWftByID(devId, prc.getId());
			logger.debug("\tUse tempalte name: " + theWf.getName());
			String checkString = "./node[@id='" + nodeid + "']";
			Node theNode = CflowHelper.wftManager.queryNodes(theWf, checkString)[0];
			nodeName = theNode.getName();
			logger.debug("Entering doTask DEV:" + devId + " actor:" + actor + " nodeName:" + nodeName + " prcId:" + prc.getId() + " option:" + optionPicked);
			NextType nexts[] = (NextType[]) theNode.getNextArray();
			int non_default_option_number = 0;
			for (int i = 0; i < nexts.length; i++) {
				if (!nexts[i].getOption().equals("DEFAULT")) {
					non_default_option_number++;
				}
			}
			if (non_default_option_number > 0 && StringUtils.isEmpty(optionPicked))
				return "ERROR: please choose your option.";

			// 检查进程中当前任务是否还存在
			// 编程时注意，对ROUND任务，只要其未结束，其状态需要保持为RUNNING.
			checkString = "./work[@nodeid='" + nodeid + "'][@sessid='" + sessid + "'][@status='" + CflowHelper.WORK_RUNNING + "']";
			logger.debug("DoTask: prcid:" + prc.getId() + " nodeid:" + nodeid + " name:" + theNode.getName());
			Work[] existingWorks = queryWorks(prc, checkString);
			if (existingWorks.length == 0) {
				// 如果工作已经不存在（比如其它用户已将其完成），则报错
				// CflowException ex = new
				// CflowException("The task is not running.");
				// Context ctx = new Context();
				// ctx.setWork(existingWorks[0]);
				// ex.setContext(ctx);
				// throw ex;
				doTaskRet = "ERROR: Task has expired. [" + sessid + "]";
				return doTaskRet;

			}
			// Update roles assignment.
			// TODO 执行过程中无需改变role. 删除showTask中修改Role映射的代码
			// TODO 进程监控中需要显示组名，并连接到组成员显示，如果是所有者，可以提供修改功能

			// Update work attachment.
			// String queryExpression = "./work[@nodeid='" + nodeid + "']";
			// Work theWork = CflowHelper.queryWorks(prc,
			// queryExpression)[0];
			Work theWork = existingWorks[0];
			// Update values of attachments of the work.
			// 处理参数信息
			boolean finalComplete = false;
			String finalOptionPicked = optionPicked;
			String mpsm = null;

			if (theWork.getType().equals(CflowHelper.NODE_TYPE_TASK)) {
				if (taskInput != null) {
					if (theWork.getType().equals(CflowHelper.NODE_TYPE_SUB)) {
						String[] attNames = new String[taskInput.size()];
						attNames = (String[]) taskInput.keySet().toArray(attNames);
						Log theLog = theWork.addNewLog();
						theLog.setUsrid(actor);
						theLog.setTimestamp(Calendar.getInstance());
						theLog.setOption(optionPicked);
						for (int i = 0; i < taskInput.size(); i++) {
							Attachment att = theLog.addNewAttachment();
							Object val = taskInput.get(attNames[i]);
							att.setType(CflowHelper.getReturedValueType(val));
							att.setLabel(attNames[i]);
							att.setAttname(attNames[i]);
							att.setUsrid(actor);
							att.setValue(val.toString());
						}
					} else {
						Log theLog = theWork.addNewLog();
						theLog.setUsrid(actor);
						theLog.setTimestamp(Calendar.getInstance());
						theLog.setOption(optionPicked);
						if (taskInput != null) {
							Attachment[] atttmpl = (Attachment[]) theWork.getAttachmentArray();
							String[] attNames = new String[taskInput.size()];
							attNames = (String[]) taskInput.keySet().toArray(attNames);
							for (int i = 0; i < attNames.length; i++) {
								Attachment att = theLog.addNewAttachment();
								att.setUsrid(actor);
								att.setAttname(attNames[i]);
								Object val = taskInput.get(attNames[i]);
								att.setValue(val.toString());
								boolean foundTmpl = false;
								// att的Type和Lable,
								// 先从Template中找，找到就用Template中的Type和Lable,
								// 找不到，则自动添加为String类型，且Label与Name一致
								for (int j = 0; j < atttmpl.length; j++) {
									if (atttmpl[j].getAttname().equals(attNames[i])) {
										foundTmpl = true;
										att.setType(atttmpl[j].getType());
										att.setLabel(atttmpl[j].getLabel());
										break;
									}
								}
								if (foundTmpl == false) {
									att.setType(CflowHelper.getReturedValueType(val));
									att.setLabel(attNames[i]);
								}
							}
						}

					}
				}

				Taskto[] tts = theWork.getTasktoArray();
				for (int i = 0; i < tts.length; i++) {
					if (tts[i].getWhom().equals(actor)) {
						tts[i].setStatus(CflowHelper.TASK_FINISHED);
						dispatchTask(devId, dbadmin, prc.getId(), prc.getWftid(), theWork, tts[i], "SYSTEM", CflowHelper.UPDATE_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
						logger.debug("\tTask finished by:" + actor);
					}
				}
				if (StringUtils.isEmpty(theWork.getMpsm()))
					theWork.setMpsm("1");

				finalComplete = false;
				mpsm = theWork.getMpsm();
				if (mpsm == null || mpsm.equals("null")) {
					mpsm = "1";
				}
				if (mpsm.equals("1")) { // 一个完成即完成
					logger.debug("\tOne-hand complete:");
					logger.debug("\t\tfinalOptionPicked:" + finalOptionPicked);
					finalComplete = true;
					finalOptionPicked = optionPicked;
				} else if (mpsm.equals("2")) { // 全部完成才算完成
					logger.debug("\tAll-hand complete:");
					boolean allfinished = true;
					for (int i = 0; i < tts.length; i++) {
						if (!(tts[i].getStatus().equals(CflowHelper.TASK_FINISHED) || tts[i].getStatus().equals(CflowHelper.TASK_DELEGATED))) {
							allfinished = false;
							break;
						}
					}
					if (allfinished) {
						logger.debug("\t\tAll finished");
						finalComplete = true;
						if (StringUtils.isEmpty(StringUtils.trimToEmpty(theWork.getOec()))) {
							finalOptionPicked = optionPicked;
							logger.debug("\t\tNo OEC");
							logger.debug("\t\tfinalOptionPkced:" + finalOptionPicked);
						} else {
							logger.debug("\t\tOEC:" + theWork.getOec());
							// Variable definition
							JSONObject ctxJS = CflowHelper.getWorkJSON(theWork);
							String script = "var CTX = " + ctxJS + ";";
							script += "\nvar total=" + tts.length + ";\n";
							script += "\n" + theWork.getOec();
							JSONObject valMap = new JSONObject();
							try {
								Object tmpret = CflowHelper.scriptHandler.runScript(script, valMap);
								String ret = tmpret.toString();
								finalOptionPicked = ret;
								logger.debug("\t\tfinalOptionPicked:" + finalOptionPicked);
							} catch (Exception e) {
								doTaskRet = "Option calculating expression error: " + e.getLocalizedMessage();
								finalOptionPicked = CflowHelper.cfg.getString("process.oec.optionOnError", "DEFAULT");
								logger.debug("\t\tfinalOptionPicked:" + finalOptionPicked);
							}
						}
					} else {
						logger.debug("\t\tNot all finished");
						finalComplete = false;
						doTaskRet = "newCreatedWorkSessIds";
					}
				} else { // 条件完成
					logger.debug("\tConditional complete:");
					boolean finished = false;
					int finishedCount = 0;
					int totCount = 0;
					for (int i = 0; i < tts.length; i++) {
						if (tts[i].getStatus().equals(CflowHelper.TASK_FINISHED)) {
							finishedCount++;
						}
						// Taskto总数计算中应去除DELEGATED.
						if (!tts[i].getStatus().equals(CflowHelper.TASK_DELEGATED)) {
							totCount++;
						}
					}
					String mpcdc = theWork.getMpcdc();
					logger.debug("\t\tMPCDC:" + mpcdc);
					JSONObject valMap = new JSONObject();
					valMap.put("total", "" + totCount);
					valMap.put("finished", "" + finishedCount);
					Object tmpret = CflowHelper.scriptHandler.runScript(mpcdc, valMap);
					logger.debug("\t\tResult:" + tmpret);
					String ret = tmpret.toString();
					if (ret.equalsIgnoreCase("true")) {
						logger.debug("\t\t\tfinished=true");
						finished = true;
					} else {
						logger.debug("\t\t\tfinished=false");
						finished = false;
					}
					if (finished) {
						finalComplete = true;
						if (StringUtils.isEmpty(StringUtils.trimToEmpty(theWork.getOec()))) {
							finalOptionPicked = optionPicked;
						} else {
							// Variable definition
							logger.debug("\tOption evaluation:" + theWork.getOec());
							JSONObject ctxJS = CflowHelper.getWorkJSON(theWork);
							String script = "var CTX = " + ctxJS + ";\n" + "var total=" + tts.length + ";\n" + "var finished=" + finishedCount + ";\n\n" + theWork.getOec();
							logger.debug("\tFull Javascript:" + script);
							try {
								finalOptionPicked = CflowHelper.scriptHandler.runScript(script, new JSONObject());
								logger.debug("\t\tEvaluation result:" + finalOptionPicked);
							} catch (Exception e) {
								doTaskRet = "Option caculating expression error: " + e.getLocalizedMessage();
								finalOptionPicked = CflowHelper.cfg.getString("process.oec.optionOnError", "DEFAULT");
							}
						}
					} else {
						finalComplete = false;
					}
				}
			} else {
				finalComplete = true;
				finalOptionPicked = optionPicked;
			}

			if (finalComplete) {
				// 将工作项设置为已完成
				theWork.setDecision(finalOptionPicked);
				theWork.setCompletedAt(Calendar.getInstance());
				theWork.setCompletedBy(actor);
				// 对ROUND进行doTask时，要看doTask带过来的option
				if (theWork.getType().equals(CflowHelper.NODE_TYPE_ROUND)) {
					// 如果option == FINISHED（当Trigger Finish时）,
					// 将该Work设为FINISHED.
					if (finalOptionPicked.equals(CflowHelper.REPEAT_OPTION_FINISH)) {
						theWork.setStatus(CflowHelper.WORK_FINISHED);
					} else {
						// 如果Option不是FINISHED, 则继续保持其状态为RUNNING,
						// 否则，在doTask时，找不到running状态的ROUND.
						theWork.setStatus(CflowHelper.WORK_RUNNING);
					}
				} else {
					// 非ROUND类型的WORK, 设置其状态为FINISHED.
					theWork.setStatus(CflowHelper.WORK_FINISHED);
				}
				if (theWork.getType().equals(CflowHelper.NODE_TYPE_TASK))
					dbadmin.removeWorkTaskto(theWork.getSessid());

				// 打开工作流模板，取出后面的活动，将符合Option值的后续节点创建活动项。

				boolean tobeCreate = false;
				String newCreatedWorkSessIds = "newCreatedWorkSessIds";
				if (StringUtils.isEmpty(finalOptionPicked)) {
					finalOptionPicked = "DEFAULT";
				}
				int createdNextNumber = 0;
				String nextOptions = "";
				for (int i = 0; i < nexts.length; i++) {
					if (i > 0)
						nextOptions += ",";
					nextOptions += nexts[i].getOption();
					if (finalOptionPicked.equals(nexts[i].getOption())) {
						tobeCreate = true;
					} else {
						tobeCreate = false;
					}
					if (tobeCreate) {
						createdNextNumber++;
						checkString = "./node[@id='" + nexts[i].getTargetID() + "']";
						Node[] nodes = CflowHelper.wftManager.queryNodes(theWf, checkString);

						String ret = createTask(devId, dbadmin, prc.getId(), prc, theWf, nodes[0], actor);
						if (ret != null) {
							newCreatedWorkSessIds += ":" + ret;
							logger.debug("\tAdvance to following successed: " + nodes[0].getName());
						} else {
							logger.debug("\tAdvance to following failed: " + nodes[0].getName());
						}
					}
				}

				if (createdNextNumber == 0) {
					String errMsg = "No next task created (" + theWf.getName() + ":" + theNode.getName() + ") OPTIONS:(" + nextOptions + ") but (" + optionPicked + ")";
					logger.error(errMsg);
					throw new Exception(errMsg);
				}
				doTaskRet = newCreatedWorkSessIds;
				// 如果是Round Node. 则继续压入该节点本身，实现Repeat.
				// 对于不需要Repeat的ROUND节点，在createTask_ROUND中，直接yark其自身为logicTask,
				// 并yark其后续节点，不会放到Timer中去等待时间到了执行doTask
				// 所以程序代码也就不会到达这里。只有需要repeat的ROUND节点，才会到达这里。
				/*
				 * if(theNode.getType().equals(CflowHelper.NODE_TYPE_ROUND)){
				 * createTask(devId, dbadmin, prc.getId(), prc, theWf, theNode,
				 * actor); }
				 */
				// 处理PostActions.
				/*
				 * RobotActions postActions = theNode.getPostactions(); if
				 * (postActions != null) { Sendmail sendmail =
				 * postActions.getSendmail(); if (sendmail != null) { //
				 * CflowHelper.gm.initComposer(dbadmin, prc.getId(), // prc,
				 * theWf, theNode, actor); CflowHelper.gm.sendMail(sendmail,
				 * CflowHelper.gm.getEmailAddresses(dbadmin, prc, sendmail)); }
				 * Sendsms sendsms = postActions.getSendsms(); if (sendsms !=
				 * null) { CflowHelper.gm.sendSMS(sendsms); } }
				 */
			}
			logger.debug("Done doTask DEV:" + devId + " actor:" + actor + " nodeName:" + nodeName + " prcId:" + prc.getId() + " option:" + optionPicked);
			// ////////
			saveProcessDoc(devId, prcDoc, prcId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("\t" + e.getLocalizedMessage());
			doTaskRet = "ERROR: Exception: " + e.getLocalizedMessage();

		}

		return doTaskRet;
	}

	public String getAcqStatus(ProcessT prc, Work theWork, String actor, String delegaterid) {
		String ret = "";
		if (!theWork.getAcquirable()) {
			ret = CflowHelper.ACQ_NOT_ACQUIRABLE;
		} else {
			if (alreadyAcquiredByMe(prc, theWork, actor, delegaterid)) {
				ret = CflowHelper.ACQ_ALREADY_ACQUIRED;
			} else {
				if (canAcquireTask(prc, theWork, actor, delegaterid)) {
					ret = CflowHelper.ACQ_CAN_ACQUIRE;
				} else {
					ret = CflowHelper.ACQ_BY_OTHERS;
				}
			}
		}
		return ret;
	}

	public Work[] getFinishedWorks(ProcessT prc, String status, String[] types) {

		String queryExpression = "";
		for (int i = 0; i < types.length; i++) {
			if (i > 0)
				queryExpression += "|";
			queryExpression += "./work[@status='" + status + "'][@type='" + types[i] + "']";
		}
		Work[] finishedWorks = queryWorks(prc, queryExpression);
		return finishedWorks;
	}

	public Work[] getFinishedWorks(ProcessT prc) {
		String queryExpression = "./work[@status='finished']";
		Work[] finishedWorks = queryWorks(prc, queryExpression);
		return finishedWorks;
	}

	//
	public JSONObject getProcessAttachments(ProcessT prc) {
		JSONObject attMap = new JSONObject();
		String queryExpression = "./work/log/attachment";
		Attachment[] atts = queryPrcAttachments(prc, queryExpression);
		for (int i = 0; i < atts.length; i++) {
			attMap.put(atts[i].getAttname(), atts[i].getValue());
		}

		return attMap;
	}

	/**
	 * @deprecated
	 * @param thePrc
	 * @param roleName
	 * @return
	 */
	public String getRoleValue(ProcessT thePrc, String roleName) {
		String roleValue = "";
		Role[] prcRoles = queryPrcRoles(thePrc, "./role[@name='" + roleName + "']");
		if (prcRoles.length > 0) {
			roleValue = prcRoles[0].getValue();
		} else {
			roleValue = "";
		}
		return roleValue;
	}

	/**
	 * 得到组中所定义的特定角色所对应的用户。一个角色可能对应到多个用户；如果一个角色在组中没有定义用户，则自动对应到流程的启动者。
	 * 
	 * @param dbadmin
	 * @param prc
	 * @param roleName
	 * @return
	 * @throws SQLException
	 */
	public Membership[] getTaskDoers(DbAdmin dbadmin, ProcessT prc, Taskto tt) {
		Membership[] ret = {};
		String ttType = null;
		String roleName = tt.getWhom();
		ttType = tt.getType();
		try {
			if (ttType.equalsIgnoreCase(CflowHelper.TASKTO_TYPE_PERSON)) {
				ArrayList<Membership> list = new ArrayList<Membership>();

				String[] actorIdentities = CflowHelper.splitEmails(tt.getWhom());
				/*
				 * boolean loadNick=false; for (int i = 0; i <
				 * actorIdentities.length; i++) {
				 * if(!actorIdentities[i].equalsIgnoreCase("me") &&
				 * !actorIdentities[i].equalsIgnoreCase("myself") &&
				 * !actorIdentities[i].equalsIgnoreCase("Whom") &&
				 * !actorIdentities[i].startsWith("CTX.")){ loadNick = true;
				 * break; } } if(loadNick == true){ nicks =
				 * dbadmin.getFriendsNicks(prc.getStartby(), prc.getTeamid()); }
				 */
				for (int i = 0; i < actorIdentities.length; i++) {
					if (actorIdentities[i].equalsIgnoreCase("me") || actorIdentities[i].equalsIgnoreCase("myself") || actorIdentities[i].equalsIgnoreCase("Whom")) {
						Membership membership = Membership.newMembership(prc.getTeamid(), prc.getStartby(), "PROCESS STARTER", "starter");
						list.add(membership);
					} else {
						// 如果以CTX.开头，则从进程数据中取
						if (actorIdentities[i].startsWith("CTX.")) {
							String ctxTmp = actorIdentities[i].substring("CTX.".length());
							Attachment[] temp = queryPrcAttachments(prc, "./attachment[@attname='" + ctxTmp + "']");
							if (temp.length > 0) {
								actorIdentities[i] = temp[0].getValue();
							} else {
								actorIdentities[i] = prc.getStartby();
							}
							/*
							 * }else{ //查找nickName, 将nickName替换为userid. for(
							 * Iterator<java.util.Map.Entry<String, String>> itr
							 * = nicks.entrySet().iterator(); itr.hasNext(); ){
							 * java.util.Map.Entry<String, String> entry =
							 * (java.util.Map.Entry<String, String>)itr.next();
							 * if(entry.getValue().equals(actorIdentities[i])){
							 * //替换 actorIdentities[i] = entry.getKey(); break;
							 * } }
							 */
						}
						Membership membership = Membership.newMembership(prc.getTeamid(), actorIdentities[i], actorIdentities[i], "USER");
						list.add(membership);
					}
				}

				ret = list.toArray(ret);
			} else if (ttType.equalsIgnoreCase(CflowHelper.TASKTO_TYPE_REFER)) {
				ArrayList<Membership> list = new ArrayList<Membership>();
				String noderef = roleName;
				String qe = "./work[@nodeid='" + noderef + "'][@roundid='" + prc.getRoundid() + "'][@status='" + CflowHelper.WORK_FINISHED + "']/log";
				Log[] logs = queryLogs(prc, qe);
				for (int i = 0; i < logs.length; i++) {
					Membership membership = Membership.newMembership(prc.getTeamid(), logs[i].getUsrid(), "REFEREDUSERNAME", "refered");
					list.add(membership);
				}
				ret = list.toArray(ret);
			} else if (ttType.equalsIgnoreCase(CflowHelper.TASKTO_TYPE_ROLE)) {
				String[] rnsa = CflowHelper.splitEmails(tt.getWhom());
				ArrayList<Membership> list = new ArrayList<Membership>();
				for (int r = 0; r < rnsa.length; r++) {
					roleName = rnsa[r];
					if (roleName.equalsIgnoreCase(CflowHelper.ROLE_STARTER)) {
						Membership membership = Membership.newMembership(prc.getTeamid(), prc.getStartby(), "PROCESS STARTER", "starter");
						list.add(membership);
					} else {
						// ret =
						// dbadmin.getTeamMembershipsByRole(prc.getTeamid(),
						// roleName);
						String qe = "./team/role[@name='" + roleName + "']";
						if (roleName.equalsIgnoreCase("ALL")) {
							qe = "./team/role";
						}
						Role[] roles = queryTeamRoles(prc, qe);
						for (int i = 0; i < roles.length; i++) {
							Membership membership = Membership.newMembership(prc.getTeamid(), roles[i].getValue(), roles[i].getValue(), roleName);
							list.add(membership);
						}
					}
				}
				ret = list.toArray(ret);
			}
			// Exceptme
			Exceptme[] exceptmearray = tt.getExceptmeArray();
			if (ret.length > 0 && exceptmearray.length > 0) {
				ArrayList<String> exceptlist = new ArrayList<String>();
				for (int e = 0; e < exceptmearray.length; e++) {
					String emnodeid = exceptmearray[e].getNodeid();
					String qe = "./work[@nodeid='" + emnodeid + "'][@roundid='" + prc.getRoundid() + "'][@status='" + CflowHelper.WORK_FINISHED + "']/log";
					Log[] logs = queryLogs(prc, qe);
					for (int i = 0; i < logs.length; i++) {
						exceptlist.add(logs[i].getUsrid());
					}
				}
				ArrayList<Membership> tmpList = new ArrayList<Membership>();
				Membership[] tmpret = {};
				// 从ret中去掉需要去掉的用户
				for (int i = 0; i < ret.length; i++) {
					Membership tms = (Membership) ret[i];
					if (!exceptlist.contains(tms.getUserIdentity())) {
						tmpList.add(tms);
					}
				}
				if (!tmpList.isEmpty())
					tmpret = tmpList.toArray(tmpret);
				ret = tmpret;
			}

			// 检查是否返回中包含用户，如果是个空列表，则把启动者添加进去。
			// 保证列表不空
			if (ret.length == 0) {
				ArrayList<Membership> list = new ArrayList<Membership>();
				Membership membership = Membership.newMembership(prc.getTeamid(), prc.getStartby(), "PROCESS STARTER", "starter");
				list.add(membership);
				ret = list.toArray(ret);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ret;
	}

	public Work getWork(ProcessT prc, String sessid) {
		String queryExpression = "./work[@sessid='" + sessid + "']";
		Work[] works = queryWorks(prc, queryExpression);
		if (works.length > 0)
			return works[0];
		else
			return null;
	}

	public Work getWork(ProcessT prc, String sessid, String status) {
		String queryExpression = "./work[@sessid='" + sessid + "'][@status='" + status + "']";
		Work[] works = queryWorks(prc, queryExpression);
		if (works.length > 0)
			return works[0];
		else
			return null;
	}

	public Attachment[] getWorkAttachmentTemplate(Work theWork) {
		return (Attachment[]) theWork.getAttachmentArray();
	}

	public Attachment[] getWorkLogAttachment(Work theWork) {
		String queryString = "./log/attachment";
		Attachment[] ret = {};
		Object[] objs = theWork.selectPath(queryString);
		if (objs == null || objs.length == 0) {
			return ret;
		} else {
			return (Attachment[]) objs;
		}
	}

	public String[] getWorkOptions(Work theWork) {
		String[] ret = {};
		Work.Options options = theWork.getOptions();
		if (options != null) {
			String[] optionArray = (String[]) options.getOptionArray();
			if (optionArray.length > 0) {
				java.util.Arrays.sort(optionArray);
			}
			ret = optionArray;
		}

		return ret;

	}

	/**
	 * Get all works derived from same nodeid, there may be many sessid.
	 * 
	 * @param prc
	 * @param nodeid
	 * @return
	 */
	public Work[] getWorks(ProcessT prc, String nodeid) {
		String queryExpression = "./work[@nodeid='" + nodeid + "']";
		Work[] works = queryWorks(prc, queryExpression);
		return works;
	}

	public Work[] getWorks(String devId, DbAdmin dbadmin, String prcId, String nodeid) throws Exception {
		ProcessDocument prcDoc = null;
		ProcessT prc = null;
		prcDoc = getProcessDocument(devId, dbadmin, prcId);
		prc = prcDoc.getProcess();
		return getWorks(prc, nodeid);
	}

	/**
	 * 在一个运行中的进程中一个活动节点后添加adHoc活动。<BR>
	 * adHoc活动的内容由adhocEntry定义，在adHocEntry的属性中，adhoc为工作的说明；
	 * adhocMat指定该工作由某个用户来执行， adhocRole指定该工作由某个角色来完成。
	 * 如果adhocMat已指定，则忽略adhocRole.
	 * 
	 * @param startBy
	 *            进程的启动者
	 * @param prcId
	 *            进程的编号
	 * @param nodeid
	 *            要在其后添加adhoc活动的节点号
	 * @param adhocs
	 *            adhocEntry数组。
	 * @throws Exception
	 */
	public void insertAdhoc(String devId, String prcId, String nodeid, adhocEntry[] adhocs) throws Exception {
		WorkflowDocument workflowDoc = CflowHelper.wftManager.getInstanceWftDocumentByID(devId, prcId);
		WorkflowDocument.Workflow workflow = workflowDoc.getWorkflow();
		String queryExpression = "./node[@id='" + nodeid + "']";

		Node[] nodes = CflowHelper.wftManager.queryNodes(workflow, queryExpression);
		if (nodes.length <= 0) {
			throw new CflowException("Node does not exist.");
		}

		NextType nexts[] = nodes[0].getNextArray();
		int row = 1;
		int col = 1;
		for (int i = 0; i < nexts.length; i++) {
			String lastNextNodeID = nexts[i].getTargetID();
			for (int j = adhocs.length - 1; j >= 0; j--) {
				Node aNode = workflow.addNewNode();
				aNode.setId(CflowHelper.myID());
				aNode.setType("task");
				aNode.setTitle(adhocs[j].adhoc);
				aNode.setName(adhocs[j].adhoc);
				aNode.setAcquirable(false);
				aNode.setAcqThreshold(1);
				aNode.setMpsm("");
				int tmpX = 500 - 70 * (col - 1);
				if (tmpX < 0) {
					row++;
					col = 1;
					tmpX = 500;
				}
				aNode.setX(tmpX);
				int tmpY = nodes[0].getY() + 30 * (row);
				aNode.setY(tmpY);
				col++;

				Taskto aTaskto = aNode.addNewTaskto();
				if (StringUtils.isEmpty(adhocs[j].adhocMat)) {
					aTaskto.setType(CflowHelper.TASKTO_TYPE_ROLE);
					aTaskto.setWhom(adhocs[j].adhocRole);
				} else {
					aTaskto.setType(CflowHelper.TASKTO_TYPE_PERSON);
					aTaskto.setWhom(adhocs[j].adhocMat);
				}
				NextType nextptr = aNode.addNewNext();
				nextptr.setOption(CflowHelper.DEFAULT_OPTION);
				nextptr.setTargetID(lastNextNodeID);
				lastNextNodeID = aNode.getId();

				if (j == 0) {
					nexts[i].setTargetID(aNode.getId());
				}
			}
		}

		String path = CflowHelper.storageManager.getIstWftPath(devId, prcId);
		String tmp = workflowDoc.toString();
		CflowHelper.storageManager.upload(path, tmp, "text/xml");
	}

	private boolean isAcquirable(ProcessT prc, Work theWork) {
		return theWork.getAcquirable();
	}

	public boolean isSubProcess(ProcessT prc) {
		if (Ctool.isEmpty(prc.getParentProcessId())) {
			return false;
		} else {
			return true;
		}
	}

	public Object[] queryChildren(XmlObject parent, String qe) {
		Object[] ret = {};
		Object[] objs = parent.selectPath(qe);
		if (objs == null || objs.length == 0) {
			return ret;
		} else {
			return (Object[]) objs;
		}
	}

	public Log[] queryLogs(ProcessT prc, String queryString) {
		Log[] logs = {};
		Object[] objs = prc.selectPath(queryString);
		if (objs == null || objs.length == 0) {
			return logs;
		} else {
			return (Log[]) objs;
		}
	}

	public Attachment[] queryPrcAttachments(ProcessT prc, String queryString) {
		Attachment[] atts = {};
		Object[] objs = prc.selectPath(queryString);
		if (objs == null || objs.length == 0) {
			return atts;
		} else {
			return (Attachment[]) objs;
		}
	}

	/**
	 * @deprecated
	 * @param prc
	 * @param queryString
	 * @return
	 */
	public Role[] queryPrcRoles(ProcessT prc, String queryString) {
		Role[] roles = {};
		Object[] objs = prc.selectPath(queryString);
		if (objs == null || objs.length == 0) {
			return roles;
		} else {
			return (Role[]) objs;
		}
	}

	public Taskto[] queryTaskto(ProcessT thePrc, String qe) {
		Taskto[] tasktos = {};
		Object[] objs = thePrc.selectPath(qe);
		if (objs == null || objs.length == 0) {
			return tasktos;
		} else {
			return (Taskto[]) objs;
		}
	}

	public Taskto[] queryTaskto(WorkflowDocument.Workflow theWorkflow, String qe) {
		Taskto[] tasktos = {};
		Object[] objs = theWorkflow.selectPath(qe);
		if (objs == null || objs.length == 0) {
			return tasktos;
		} else {
			return (Taskto[]) objs;
		}
	}

	public Role[] queryTeamRoles(ProcessT prc, String qe) {
		Role[] roles = {};
		Object[] objs = prc.selectPath(qe);
		if (objs == null || objs.length == 0) {
			return roles;
		} else {
			return (Role[]) objs;
		}
	}

	public Work[] queryWorks(ProcessT prc, String queryString) {
		Work[] works = {};
		Object[] objs = prc.selectPath(queryString);
		if (objs == null || objs.length == 0) {
			return works;
		} else {
			return (Work[]) objs;
		}
	}

	public void restoreUserAssociation(String devId, DbAdmin dbadmin, ProcessT prc, Work theWork) {
		Taskto[] tasktos = theWork.getTasktoArray();
		for (int i = 0; i < tasktos.length; i++) {
			try {
				dbadmin.associatePrcToUser(devId, tasktos[i].getWhom(), prc.getId(), prc.getWftid(), CflowHelper.ASSOC_TYPE_USER);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void saveProcessDoc(String devId, ProcessDocument prcDoc, String prcId) {
		Object lock = null;
		synchronized (CflowHelper.lruDocLock) {
			lock = CflowHelper.lruDocLock.get(prcId);
			if (lock == null) {
				lock = new Object();
				CflowHelper.lruDocLock.put(prcId, lock);
			}

		}
		synchronized (lock) {
			try {
				String path = CflowHelper.storageManager.getPrcPath(devId, prcId);
				String tmp = prcDoc.toString();
				CflowHelper.storageManager.upload(path, tmp, "text/xml");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void startRepeater(String devId, DbAdmin dbadmin, String actor, String prcId, Work theWork, Node theNode) {
		Trigger trigger;

		String threshold = StringUtils.trimToEmpty(theNode.getRepeatthreshold());
		String cronExpression = StringUtils.trimToEmpty(theNode.getRepeatcron());

		String jobName = actor + "_" + theWork.getSessid();
		String jobGroup = prcId;
		String triggerName = actor + "_" + theWork.getSessid();
		String triggerGroup = prcId;

		JobDetail job = new JobDetail(jobName, jobGroup, TaskRepeater.class);
		job.getJobDataMap().put("DEVID", devId);
		job.getJobDataMap().put("DOER", actor);
		job.getJobDataMap().put("PRCID", prcId);
		job.getJobDataMap().put("SESSID", theWork.getSessid());
		job.getJobDataMap().put("NODEID", theNode.getId());
		job.setDurability(true);

		try {
			CflowHelper.schManager.addJob(job, false);
			logger.debug("CronTrigger: " + cronExpression + " until [" + threshold + "]");
			cronExpression = CflowHelper.validateCronExpression(actor, cronExpression);
			logger.debug("CronTrigger: validated: " + cronExpression + " until [" + threshold + "]");
			long ts = TriggerUtils.getNextGivenSecondDate(null, 59).getTime();
			Date startDate = new Date(ts);
			Date endDate = null;
			if (StringUtils.isBlank(threshold)) {
				endDate = null;
			} else {
				endDate = CflowHelper.simpleDateFormat.parse(threshold);
			}
			trigger = new CronTrigger(triggerName, triggerGroup, jobName, jobGroup, startDate, endDate, cronExpression, TimeZone.getTimeZone(CflowHelper.defaultTimeZone));
			trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
			CflowHelper.schManager.scheduler.scheduleJob(trigger);
		} catch (Exception e) {
			logger.error("Exception", e);
		}

	}

	private void startTimer(String devId, DbAdmin dbadmin, String actor, String prcId, Work theWork, Node theNode) {
		Calendar currentTime = Calendar.getInstance();
		if (theNode.getYy().intValue() != 0) {
			currentTime.add(Calendar.YEAR, theNode.getYy().intValue());
		}
		if (theNode.getMm().intValue() != 0) {
			currentTime.add(Calendar.MONTH, theNode.getMm().intValue());
		}
		if (theNode.getDd().intValue() != 0) {
			currentTime.add(Calendar.DATE, theNode.getDd().intValue());
		}
		if (theNode.getHh().intValue() != 0) {
			currentTime.add(Calendar.HOUR, theNode.getHh().intValue());
		}
		if (theNode.getMi().intValue() != 0) {
			currentTime.add(Calendar.MINUTE, theNode.getMi().intValue());
		}

		java.util.Date theDate = currentTime.getTime();
		try {
			dbadmin.newTimer(devId, actor, prcId, theWork.getSessid(), theNode.getId(), theDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 启动一个新进程
	 * 
	 * @param dbadmin
	 *            DBAdmin实例
	 * @param wftId
	 *            模板ID
	 * @param wrkteam
	 *            工作组ID
	 * @param startBy
	 *            进程启动者UserID
	 * @returnO
	 */
	public String startWorkflow(String devId, DbAdmin dbadmin, String wftId, String teamTemplateId, String startBy, String instanceName, JSONObject ctx) {
		String parentProcessId = null;
		String parentProcessNodeId = null;
		String parentProcessSessId = null;
		String prcId = startWorkflow(devId, dbadmin, wftId, teamTemplateId, startBy, instanceName, ctx, parentProcessId, parentProcessNodeId, parentProcessSessId);

		return prcId;
	}

	public String startWorkflowByName(String devId, DbAdmin dbadmin, String wftName, String teamName, String startBy, String instanceName, JSONObject ctx) {
		return startWorkflowByName(devId, dbadmin, wftName, teamName, startBy, instanceName, ctx, null, null, null);
	}

	private String startWorkflowByName(String devId, DbAdmin dbadmin, String wftName, String teamName, String startBy, String instanceName, JSONObject ctx, String parentProcessId, String parentProcessNodeId, String parentProcessSessId) {
		String ret = null;
		boolean kc = dbadmin.keepConnection(true);
		try {
			String prcId = CflowHelper.myID();
			String wftId = dbadmin.getWftIdByName(devId, wftName);
			String teamTemplateId = dbadmin.getTeamIdByName(devId, teamName);
			ret = _XXX_startWorkflow(devId, dbadmin, prcId, wftId, teamTemplateId, startBy, instanceName, ctx, parentProcessId, parentProcessNodeId, parentProcessSessId);
		} catch (Exception e) {
			e.printStackTrace();
			ret = "ERROR: Exception: " + e.getLocalizedMessage();
		} finally {
			dbadmin.keepConnection(kc);
		}

		return ret;
	}

	private String startWorkflow(String devId, DbAdmin dbadmin, String wftId, String teamTemplateId, String startBy, String instanceName, JSONObject ctx, String parentProcessId, String parentProcessNodeId, String parentProcessSessId) {
		String ret = null;
		try {
			String prcId = CflowHelper.myID();
			// ret = dbadmin.send_StartWorkflow_toFrontDesk(devId, prcId, wftId,
			// teamTemplateId, startBy, instanceName, ctx, parentProcessId,
			// parentProcessNodeId, parentProcessSessId);
			ret = _XXX_startWorkflow(devId, dbadmin, prcId, wftId, teamTemplateId, startBy, instanceName, ctx, parentProcessId, parentProcessNodeId, parentProcessSessId);
		} catch (Exception e) {
			e.printStackTrace();
			ret = "ERROR: Exception: " + e.getLocalizedMessage();
		}

		return ret;
	}

	/**
	 * 根据工作流模板启动一个工作流进程
	 * 
	 * @param dbadmin
	 *            DBAdmin对象
	 * @param wftId
	 *            模板ID
	 * @param teamTemplate
	 *            工作组模板ID
	 * @param startBy
	 *            进程启动者UserID
	 * @param parentProcessId
	 *            父进程ID
	 * @param parentProcessNodeIds
	 *            父进程中子流程节点的ID
	 * @return
	 */
	public String _XXX_startWorkflow(String devId, DbAdmin dbadmin, String prcId, String wftId, String teamTemplateId, String startBy, String instanceName, JSONObject ctx, String parentProcessId, String parentProcessNodeId, String parentProcessSessId) {
		String info = "workflow DEV:" + devId + " wftId:" + wftId + " teamId:" + teamTemplateId + " startBy:" + startBy + " instanceName:" + instanceName + " ppid:" + parentProcessId + " ppnodeid:" + parentProcessNodeId + "ppsessid:" + parentProcessSessId;
		try {
			logger.debug("Starting " + info);
			User startByUser = dbadmin.getUserByIdentity(devId, startBy);
			if (startByUser == null) {
				logger.error("\tStartby: [" + startBy + "] does not exist!");
				return "ERROR: startBy:" + startBy + " does not exist";
			}
			WorkflowDocument.Workflow theWf = CflowHelper.wftManager.getWftByID(devId, wftId);
			if (theWf == null) {
				logger.error("\tgetWftById return null: " + wftId + " the vault file may not exist.");
				return "ERROR: getWftById return null:" + wftId + " the vault file may not exist.";
			}
			if (StringUtils.isNotEmpty(teamTemplateId)) {
				Team tobeCheckedTeam = dbadmin.getTeamById(devId, teamTemplateId);
				if (tobeCheckedTeam == null) {
					logger.error("\tgetTeamById return null:" + teamTemplateId);
					return "ERROR: getTeamById return null:" + teamTemplateId;
				}
			}

			// Create process document
			// 赋值模板到实例模板
			CflowHelper.wftManager.makeIstWftFromWft(devId, wftId, prcId);

			// 读取对应的工作流模板
			theWf = CflowHelper.wftManager.getInstanceWftByID(devId, prcId);

			ProcessDocument prcDoc = ProcessDocument.Factory.newInstance();
			ProcessT prc = prcDoc.addNewProcess();
			// Set other attribute for the process
			prc.setId(prcId);
			logger.debug("\tNew process id:" + prcId);
			// 如果parentProcessID和parentProcessNodeId不为空，
			// 则表示该流程是个子流程
			if (parentProcessId != null && parentProcessNodeId != null) {
				prc.setParentProcessId(parentProcessId);
				prc.setParentProcessNodeId(parentProcessNodeId);
				prc.setParentProcessSessId(parentProcessSessId);
			}
			prc.setWftid(prcId);
			prc.setOrigin(wftId);
			prc.setRoundid(CflowHelper.myID());
			prc.setStartat(Calendar.getInstance());
			prc.setStatus(CflowHelper.PROCESS_RUNNING);
			prc.setStartby(startBy);
			// TODO: generate process team
			// prc.setTeamid(instanceTeamId);
			prc.setTeamid(teamTemplateId);
			// 这里将组及角色信息装入Process
			// 要修改一个Process的团队成员，最终应该是修改在这里
			TeamType theTeam = prc.addNewTeam();
			if (StringUtils.isNotEmpty(teamTemplateId)) {
				Membership[] members = dbadmin.getTeamMemberships(devId, teamTemplateId);
				for (int i = 0; i < members.length; i++) {
					Role aRole = theTeam.addNewRole();
					aRole.setName(members[i].getRole());
					aRole.setValue(members[i].getUserIdentity());
				}
			}

			if (StringUtils.isEmpty(instanceName))
				prc.setWftname(theWf.getName());
			else
				prc.setWftname(instanceName);

			String wfCustomizer = theWf.getCustomizer();
			if (!StringUtils.isBlank(wfCustomizer))
				prc.setCustomizer(wfCustomizer);

			if (ctx != null) {
				// 特殊参数 PBO
				Object pbo = ctx.get("PBO");
				String pboString = null;
				if (pbo != null) {
					pboString = pbo.toString();
				}
				if (!StringUtils.isBlank(pboString)) {
					prc.setPbo(pboString);
				}
				// 所有参数一股脑放入prc.attachments下面。
				java.util.Set<String> ctxKeys = ctx.keySet();
				if (ctxKeys.size() > 0) {
					String[] keys = new String[ctxKeys.size()];
					keys = (String[]) ctxKeys.toArray(keys);
					for (int i = 0; i < keys.length; i++) {
						if (keys[i].equals("PBO"))
							continue;
						Attachment att = prc.addNewAttachment();
						att.setAttname(keys[i]);
						Object ctxObj = ctx.get(keys[i]);
						att.setValue(ctxObj.toString());
					}
				}
			}

			dbadmin.logNewProcess(devId, prc.getId(), prc.getStartby(), prc.getStartat(), null, CflowHelper.PROCESS_RUNNING, prc.getWftname(), wftId, parentProcessId, parentProcessNodeId, parentProcessSessId);
			logger.debug("\t" + prc.getId() + " logged");
			String queryExpression = "./node[@type='start']";
			logger.debug("\tCreating START task.");
			Node[] nodes = CflowHelper.wftManager.queryNodes(theWf, queryExpression);
			// Create the START task.
			createTask(devId, dbadmin, prcId, prc, theWf, nodes[0], startBy);

			saveProcessDoc(devId, prcDoc, prcId);
			// NotifyProcessToStarter(dbadmin, startBy, prc.getId(), wftId);
			logger.debug("\tCreated START task. process started.");
			return prcId;
			// dbadmin.addProcessLimit(startByUser.orgid, 1);
		} catch (Exception e) {
			logger.debug("Exception start " + info);
			logger.error("\t" + e.getLocalizedMessage());
			e.printStackTrace();
			deleteProcess(devId, dbadmin, prcId);
			return "ERROR: Exception: " + e.getLocalizedMessage();
		}
	}

	/**
	 * Add context data to process document
	 * 
	 * @param prcDoc
	 *            , the process document
	 * @param ctx
	 *            , the data to add.
	 */
	public void addProcessCtx(String devId, ProcessDocument prcDoc, JSONObject ctx) {
		try {
			ProcessT prc = prcDoc.getProcess();
			if (ctx != null) {
				// 特殊参数 PBO
				if (!StringUtils.isBlank((String) ctx.get("PBO")))
					prc.setPbo((String) ctx.get("PBO"));
				// 所有参数一股脑放入prc.attachments下面。
				java.util.Set<String> ctxKeys = ctx.keySet();
				if (ctxKeys.size() > 0) {
					String[] keys = new String[ctxKeys.size()];
					keys = (String[]) ctxKeys.toArray(keys);
					for (int i = 0; i < keys.length; i++) {
						Attachment att = prc.addNewAttachment();
						att.setAttname(keys[i]);
						att.setValue((String) ctx.get(keys[i]));
					}
				}
			}
			saveProcessDoc(devId, prcDoc, prc.getId());
		} catch (Exception e1) {
			logger.error(e1.getLocalizedMessage());
		}

	}

	private String unAcquireTask(String devId, DbAdmin dbadmin, ProcessT prc, Work theWork, String actor) {
		Taskto[] tasktos = theWork.getTasktoArray();
		int acqNumber = 0;

		for (int i = 0; i < tasktos.length; i++) {
			if (tasktos[i].getStatus().equals(CflowHelper.TASK_ACQUIRED)) {
				acqNumber++;
			}
			if (tasktos[i].getWhom().equals(actor) && tasktos[i].getStatus().equals(CflowHelper.TASK_ACQUIRED)) {
				tasktos[i].setStatus(CflowHelper.TASK_RUNNING);
				dispatchTask(devId, dbadmin, prc.getId(), prc.getWftid(), theWork, tasktos[i], "SYSTEM", CflowHelper.UPDATE_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
				acqNumber--;
			}
		}
		if (acqNumber < theWork.getAcqThreshold()) {
			for (int i = 0; i < tasktos.length; i++) {
				if (tasktos[i].getStatus().equals(CflowHelper.TASK_DEFERRED)) {
					tasktos[i].setStatus(CflowHelper.TASK_RUNNING);
					dispatchTask(devId, dbadmin, prc.getId(), prc.getWftid(), theWork, tasktos[i], "SYSTEM", CflowHelper.UPDATE_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
				}
			}
		}
		return null;
	}

	public String unAcquireTask(String devId, DbAdmin dbadmin, String prcId, String sessid, String actor) {
		String ret = null;
		ProcessDocument prcDoc;
		try {
			prcDoc = getProcessDocument(devId, dbadmin, prcId);
			ProcessT prc = prcDoc.getProcess();
			String qe = "./work[@sessid='" + sessid + "']";
			Work[] works = (Work[]) queryChildren(prc, qe);
			for (int i = 0; i < works.length; i++) {
				ret = unAcquireTask(devId, dbadmin, prc, works[i], actor);
			}
			saveProcessDoc(devId, prcDoc, prcId);
		} catch (Exception e1) {
			ret = "Server error: " + e1.getLocalizedMessage();
			logger.error(e1.getLocalizedMessage());
		}

		return ret;
	}

	public void updateTeamMembers(ProcessT prc, HashMap<String, String[]> rtcs) throws Exception {

		TeamType newTeam = TeamType.Factory.newInstance();
		if (prc.getTeam() == null)
			return;

		Role[] members = prc.getTeam().getRoleArray();
		String[] keys = {};
		keys = rtcs.keySet().toArray(keys);
		for (int i = 0; i < members.length; i++) {
			boolean needtoReset = false;
			for (int r = 0; r < keys.length; r++) {
				if (members[i].getName().equals(keys[r])) {
					needtoReset = true;
					break;
				}
			}
			if (!needtoReset) {
				Role aRole = newTeam.addNewRole();
				aRole.setName(members[i].getName());
				aRole.setValue(members[i].getValue());
			}
		}

		for (int r = 0; r < keys.length; r++) {
			String[] participates = rtcs.get(keys[r]);
			for (int i = 0; i < participates.length; i++) {
				Role aRole = newTeam.addNewRole();
				aRole.setName(keys[r]);
				aRole.setValue(participates[i]);
			}
		}

		prc.setTeam(newTeam);
	}

	/**
	 * 压入一个逻辑节点
	 * 
	 * @param prc
	 * @param theNode
	 * @param option
	 */
	private Work yarkLogicTask(ProcessT prc, Node theNode, String option) {
		Work theWork = prc.addNewWork();
		if (theNode.getType().equals(CflowHelper.NODE_TYPE_AND)) {
			theWork.setTitle("AND");
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_END)) {
			theWork.setTitle("End Point");
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_NOTIFY)) {
			theWork.setTitle("Notification");
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_OR)) {
			theWork.setTitle("OR");
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_ROUND)) {
			theWork.setTitle("ROUND");
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_SCRIPT)) {
			theWork.setTitle("Script");
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_START)) {
			theWork.setTitle("Start Point");
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_SUB)) {
			theWork.setTitle("Sub Process");
		} else if (theNode.getType().equals(CflowHelper.NODE_TYPE_TIMER)) {
			theWork.setTitle("Timer");
		} else {
			theWork.setTitle(theNode.getTitle());
		}
		theWork.setType(theNode.getType());
		theWork.setNodeid(theNode.getId());
		theWork.setSessid(CflowHelper.myID());
		theWork.setRoundid(prc.getRoundid());
		theWork.setCreatedAt(Calendar.getInstance());
		theWork.setCompletedAt(Calendar.getInstance());

		if (theNode.getType().equals(CflowHelper.NODE_TYPE_ROUND)) {
			theWork.setRepeatthresholdtype(theNode.getRepeatthresholdtype());
			theWork.setRepeatthreshold(theNode.getRepeatthreshold());
			theWork.setRepeatcron(theNode.getRepeatcron());
			if (theNode.getRepeatthresholdtype().equals(CflowHelper.REPEAT_TYPE_NOREPEAT)) {
				theWork.setStatus(CflowHelper.WORK_FINISHED);
			} else {
				theWork.setStatus(CflowHelper.WORK_RUNNING);
			}
		} else {
			if (theNode.getType().equals(CflowHelper.NODE_TYPE_SUB) || theNode.getType().equals(CflowHelper.NODE_TYPE_TIMER)) {
				theWork.setStatus(CflowHelper.WORK_RUNNING);
			} else {
				theWork.setStatus(CflowHelper.WORK_FINISHED);
			}
		}
		theWork.setDecision(option);
		Taskto taskto = theWork.addNewTaskto();
		taskto.setType(CflowHelper.TASKTO_TYPE_PERSON);
		taskto.setWhom("SYSTEM");
		taskto.setStatus(CflowHelper.TASK_FINISHED);
		return theWork;
	}

	/**
	 * 无选项模式压入后续节点
	 * 
	 * @param dbadmin
	 * @param prcId
	 * @param prc
	 * @param theWf
	 * @param theNode
	 * @param roleAssignMap
	 * @param actor
	 */
	private String yarkNextTask(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor) {
		String ret = "";
		NextType nexts[] = (NextType[]) theNode.getNextArray();
		for (int i = 0; i < nexts.length; i++) {
			String queryExpression = "./node[@id='" + nexts[i].getTargetID() + "']";
			Node[] nodes = CflowHelper.wftManager.queryNodes(theWf, queryExpression);
			String ctret = createTask(devId, dbadmin, prcId, prc, theWf, nodes[0], actor);
			if (ctret != null) {
				ret += ":" + ctret;
			}
		}
		return ret;
	}

	/**
	 * 根据选项，压入后续节点
	 * 
	 * @param dbadmin
	 * @param prcId
	 * @param prc
	 * @param theWf
	 * @param theNode
	 * @param roleAssignMap
	 * @param actor
	 * @param option
	 */
	private String yarkNextTaskWithOption(String devId, DbAdmin dbadmin, String prcId, ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode, String actor, String option) {
		String ret = "";
		boolean foundNext = false;
		NextType nexts[] = (NextType[]) theNode.getNextArray();
		for (int i = 0; i < nexts.length; i++) {
			if (nexts[i].getOption().equals(option)) {
				foundNext = true;
				break;
			}
		}
		if (!foundNext) {
			String errMsg = "Routing option (" + option + ") not found, set to onerror";
			option = "onerror";
			prc.setLastError(errMsg);
		}
		foundNext = false;
		for (int i = 0; i < nexts.length; i++) {
			if (nexts[i].getOption().equals(option)) {
				foundNext = true;
				String queryExpression = "./node[@id='" + nexts[i].getTargetID() + "']";
				Node[] nodes = CflowHelper.wftManager.queryNodes(theWf, queryExpression);
				String ctret = createTask(devId, dbadmin, prcId, prc, theWf, nodes[0], actor);
				if (ctret != null) {
					ret += ":" + ctret;
				}
			}
		}
		if (!foundNext) {
			// TODO: Write document to describe this behaviour.
			// 如果找不到后续节点，则结束整个流程。
			String queryExpression = "./node[@type='" + CflowHelper.NODE_TYPE_END + "']";
			Node[] nodes = CflowHelper.wftManager.queryNodes(theWf, queryExpression);
			ret = createTask(devId, dbadmin, prcId, prc, theWf, nodes[0], actor);
		}

		return ret;
	}

	private String[] yarkPeriodDelegate(String devId, DbAdmin dbadmin, String prcId, String wftId, Work theWork, Taskto taskto) throws SQLException {
		String[] delegatee = CflowHelper.dlgManager.getPeriodDelegatee(devId, dbadmin, taskto.getWhom());
		for (int i = 0; i < delegatee.length; i++) {
			Taskto tt = theWork.addNewTaskto();
			tt.setType(CflowHelper.TASKTO_TYPE_PERSON);
			tt.setWhom(delegatee[i]);
			tt.setStatus(CflowHelper.TASK_RUNNING);
			dispatchTask(devId, dbadmin, prcId, wftId, theWork, tt, taskto.getWhom(), CflowHelper.NEW_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
		}
		if (delegatee.length > 0) {
			taskto.setStatus(CflowHelper.TASK_DELEGATED);
			dispatchTask(devId, dbadmin, prcId, wftId, theWork, taskto, "SYSTEM", CflowHelper.UPDATE_TASK_STATUS, CflowHelper.ASSOC_TYPE_USER);
		}
		return delegatee;
	}

	public void suspendProcess(String devId, DbAdmin dbadmin, String prcId) throws Exception {
		ProcessDocument prcDoc = null;
		ProcessT prc = null;
		prcDoc = getProcessDocument(devId, dbadmin, prcId);

		prc = prcDoc.getProcess();

		if (prc.getStatus().equals(CflowHelper.PROCESS_RUNNING)) {
			prc.setStatus(CflowHelper.PROCESS_SUSPENDED);
			saveProcessDoc(devId, prcDoc, prcId);
			dbadmin.updateProcessLog(prc.getId(), prc.getStartby(), prc.getStartat(), prc.getEndat(), prc.getStatus());
			dbadmin.suspendProcessTaskto(prc.getId());
		}
	}

	public void resumeProcess(String devId, DbAdmin dbadmin, String prcId) throws Exception {
		ProcessDocument prcDoc = null;
		ProcessT prc = null;
		prcDoc = getProcessDocument(devId, dbadmin, prcId);
		prc = prcDoc.getProcess();

		if (prc.getStatus().equals(CflowHelper.PROCESS_SUSPENDED)) {
			prc.setStatus(CflowHelper.PROCESS_RUNNING);
			saveProcessDoc(devId, prcDoc, prcId);
			dbadmin.resumeProcessTaskto(prc.getId());
			dbadmin.updateProcessLog(prc.getId(), prc.getStartby(), prc.getStartat(), prc.getEndat(), prc.getStatus());
		}
	}

	public void cancelProcess(String devId, DbAdmin dbadmin, String prcId) throws Exception {
		ProcessDocument prcDoc = null;
		ProcessT prc = null;
		prcDoc = getProcessDocument(devId, dbadmin, prcId);
		prc = prcDoc.getProcess();

		prc.setStatus(CflowHelper.PROCESS_CANCELED);
		prc.setEndat(Calendar.getInstance());
		saveProcessDoc(devId, prcDoc, prcId);
		// DbAdmin.removePrcIdFromUser(actor, prcId);
		dbadmin.removeProcessTaskto(prc.getId());
		dbadmin.updateProcessLog(prc.getId(), prc.getStartby(), prc.getStartat(), prc.getEndat(), prc.getStatus());
		dbadmin.removeProcessTaskto(prc.getId());
	}

	public void suspendWork(String devId, DbAdmin dbadmin, String prcId, String nodeid) throws Exception {
		ProcessDocument prcDoc = null;
		ProcessT prc = null;
		prcDoc = getProcessDocument(devId, dbadmin, prcId);
		prc = prcDoc.getProcess();

		String queryExpression = "./work[@nodeid='" + nodeid + "'][@roundid='" + prc.getRoundid() + "']";
		Work[] works = queryWorks(prc, queryExpression);
		for (int i = 0; i < works.length; i++) {
			if (works[i].getStatus().equals(CflowHelper.WORK_RUNNING)) {
				works[i].setStatus(CflowHelper.WORK_SUSPENDED);
				dbadmin.suspendWorkTaskto(works[i].getSessid());
			}
		}
		saveProcessDoc(devId, prcDoc, prcId);
	}

	public void resumeWork(String devId, DbAdmin dbadmin, String prcId, String nodeid) throws Exception {
		ProcessDocument prcDoc = null;
		ProcessT prc = null;
		prcDoc = getProcessDocument(devId, dbadmin, prcId);
		prc = prcDoc.getProcess();

		String queryExpression = "./work[@nodeid='" + nodeid + "'][@roundid='" + prc.getRoundid() + "']";
		Work[] works = queryWorks(prc, queryExpression);
		for (int i = 0; i < works.length; i++) {
			if (works[i].getStatus().equals(CflowHelper.WORK_SUSPENDED)) {
				works[i].setStatus(CflowHelper.WORK_RUNNING);
				dbadmin.resumeWorkTaskto(works[i].getSessid());
				restoreUserAssociation(devId, dbadmin, prc, works[i]);
			}
		}
		saveProcessDoc(devId, prcDoc, prcId);

	}

	public void deleteProcess(String devId, DbAdmin dbadmin, String prcId) {
		Object lock = null;
		synchronized (CflowHelper.lruDocLock) {
			lock = CflowHelper.lruDocLock.get(prcId);
			if (lock == null) {
				lock = new Object();
				CflowHelper.lruDocLock.put(prcId, lock);
			}

		}
		synchronized (lock) {
			try {
				CflowHelper.schManager.clearProcessCronJob(prcId);
			} catch (Exception e) {
				logger.warn(e.getLocalizedMessage());
			}
			try {
				dbadmin.deleteProcess(devId, prcId);
			} catch (Exception ex) {
				logger.warn(ex.getLocalizedMessage());
			}
			try {
				// Remove process then
				CflowHelper.storageManager.delete(CflowHelper.storageManager.getPrcPath(devId, prcId));
			} catch (Exception ex) {
				logger.warn(ex.getLocalizedMessage());
			}
			try {
				// Remove instance WFT
				CflowHelper.storageManager.delete(CflowHelper.storageManager.getIstWftPath(devId, prcId));
			} catch (Exception ex) {
				logger.warn(ex.getLocalizedMessage());
			}
		}
	}

	public ProcessDocument getProcessDocument(String devId, DbAdmin dbadmin, String prcId) throws Exception {
		Object lock = null;
		synchronized (CflowHelper.lruDocLock) {
			lock = CflowHelper.lruDocLock.get(prcId);
			if (lock == null) {
				lock = new Object();
				CflowHelper.lruDocLock.put(prcId, lock);
			}

		}
		synchronized (lock) {
			try {
				logger.debug("getProcessDocument devId:" + devId + " prcId:" + prcId);
				String path = CflowHelper.storageManager.getPrcPath(devId, prcId);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				CflowHelper.storageManager.download(path, baos);
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				ProcessDocument prcDoc = ProcessDocument.Factory.parse(bais);
				baos.close();
				bais.close();

				return prcDoc;
			} catch (Exception ex) {
				logger.error("Download process file failed, devid:" + devId + " prcId:" + prcId);
				ex.printStackTrace();
				if (CflowHelper.cfg.getBoolean("file.prc.notfound.delete", false))
					CflowHelper.prcManager.deleteProcess(devId, dbadmin, prcId);
				deleteProcess(devId, dbadmin, prcId);
				return null;
			}
		}
	}

	public String parseVT(String tokenString, DbAdmin dbadmin, String dev, String tid, String usrid, String vtname) throws Exception {
		VelocityContext context = new VelocityContext();
		WorkItemInfo wii = dbadmin.getWiiByTid(tid);
		if (wii == null) {
			context.put("error", "Get workitem failed");
		}

		ProcessDocument prcDoc = getProcessDocument(dev, dbadmin, wii.prcId);
		if (prcDoc == null) {
			throw new Exception("get processdocument " + wii.prcId + " failed.");
		}
		ProcessT thePrc = prcDoc.getProcess();
		/*
		 * WorkflowDocument.Workflow theWorkflow = CflowHelper.wftManager
		 * .getInstanceWftByID(thePrc.getStartby(), wii.prcId); if (theWorkflow
		 * == null) { throw new Exception("get InstanceWftByID " + wii.prcId +
		 * " failed."); }
		 */
		// 根据sessid取当前的活动项。
		Work theWork = getWork(thePrc, wii.sessid, CflowHelper.WORK_RUNNING);
		if (theWork == null) {
			throw new Exception("getWork prc:" + wii.prcId + "sessid:" + wii.sessid + "  failed.");
		}

		if (theWork.getForm() != null && theWork.getForm().equals("ERROR")) {
			vtname = "ERROR";
		} else {
			if (vtname == null || vtname.equals("") || vtname.equalsIgnoreCase("null")) {
				vtname = theWork.getForm();
			}
		}

		StringBuffer sb = null;
		String workTitle = theWork.getTitle();
		Map<String, String> tMap = new HashMap<String, String>();
		if (workTitle.indexOf('$') >= 0) {
			Attachment[] temp = queryPrcAttachments(thePrc, "./work/log/attachment");
			for (int i = 0; i < temp.length; i++) {
				tMap.put("\\$" + temp[i].getAttname() + "\\$", temp[i].getValue());
			}
			temp = queryPrcAttachments(thePrc, "./attachment");
			for (int i = 0; i < temp.length; i++) {
				tMap.put("\\$" + temp[i].getAttname() + "\\$", temp[i].getValue());
			}
			for (Map.Entry entry : tMap.entrySet()) {
				workTitle = workTitle.replaceAll(entry.getKey().toString(), entry.getValue().toString());
			}
		}
		String instruction = HtmlHelper.bbcode(HtmlHelper.removeHTML(workTitle), false);
		String acqStatus = getAcqStatus(thePrc, theWork, usrid, wii.delegaterid);
		boolean allowDelegate = theWork.getAllowDelegate();
		String acqmenu_html = "";
		if (acqStatus.equals("NOT_ACQUIRABLE")) {
			sb = new StringBuffer();
			if (acqStatus.equals("ALREADY_ACQUIRED")) {
				sb.append("<a href=\"/cflow/Acq").append("?prcid=").append(wii.getPrcid()).append("&nodeid=").append(theWork.getNodeid()).append("&sessid=").append(theWork.getSessid()).append("&action=unacq").append("&token=").append(tokenString).append("\">Unacquired</a>");
			} else {
				if (acqStatus.equals("ACQ_CAN_ACQUIRE")) {
					sb.append("<a href=\"/cflow/Acq").append("?prcid=").append(wii.getPrcid()).append("&nodeid=").append(theWork.getNodeid()).append("&sessid=").append(theWork.getSessid()).append("&action=acq").append("&token=").append(tokenString);
					if (wii.getDelegaterid() != null) {
						sb.append("&dlg=").append(wii.getDelegaterid());
					}
					sb.append("\">Acquire</a>");
				} else {
					sb.append("<!-- todo: hide dotask form -->");
				}
			}
			acqmenu_html = sb.toString();
		}
		JSONArray mates = dbadmin.getAllUsers(dev);
		String delegation_html = "";
		if (allowDelegate && (acqStatus.equals("ACQ_NOT_ACQUIRABLE") || acqStatus.equals("ACQ_CAN_ACQUIRE"))) {
			sb = new StringBuffer();
			sb.append("<input type='button' name='delegateButton' value='Delegate to' onClick='javascript:startDelegate();'>");
			sb.append("<input type='hidden' name='delegateflag' id='delegateflag' value='0'>");
			sb.append("<select name='delegatee'>");
			for (int i = 0; i < mates.size(); i++) {
				JSONObject aMate = (JSONObject) mates.get(i);
				sb.append("<option value='").append(aMate.get("ID")).append("'>").append(aMate.get("IDENTIY")).append("-").append(aMate.get("NAME")).append("</option>");
			}
			sb.append("</select>");
			delegation_html = sb.toString();
		} else {
			delegation_html = "<input type='hidden' name='delegateflag' id='delegateflag' value='0'>";
		}

		// form attachments
		sb = new StringBuffer();
		sb.append("<table id='form_variables'>");
		String atts_html = "";
		Attachment[] attachments = getWorkAttachmentTemplate(theWork);
		for (int i = 0; i < attachments.length; i++) {
			sb.append("<tr>");
			sb.append("<td>").append(attachments[i].getLabel()).append("</td>");
			sb.append("<td>");
			String att_input = getInputHtml(attachments[i], attachments[i].getValue());
			sb.append(att_input);
			context.put("input_" + attachments[i].getAttname(), att_input);
			sb.append("</td></tr>");
		}
		sb.append("</table>");
		atts_html = sb.toString();

		String options_html = "";
		sb = new StringBuffer();
		String[] optionArray = getWorkOptions(theWork);
		optionArray = CflowHelper.removeDuplicate(optionArray);
		for (int i = 0; i < optionArray.length; i++) {
			OptionString tmpOptS = new OptionString(optionArray[i]);
			sb.append("<input type='radio' name='option_1' value='").append(optionArray[i]).append("'");
			if (tmpOptS.getIsDefault()) {
				sb.append(" checked");
			}
			sb.append(">").append(tmpOptS.getCleanOption());
		}
		options_html = sb.toString();

		Attachment[] gatts = queryPrcAttachments(thePrc, "./attachment");
		for (int g = 0; g < gatts.length; g++) {
			context.put("global_" + gatts[g].getAttname(), gatts[g].getValue());
		}

		String history_html = "";
		sb = new StringBuffer();
		Work[] finishedWorks = getFinishedWorks(thePrc);
		// TODO SORT BY DATE
		sb.append("<table id='history_table'><thead><tr><th>TASK</th><th>BY</th><th>TIME</th><th>DECISION</th></tr></thead><tbody>");
		for (int i = 0; i < finishedWorks.length; i++) {
			Work aWork = finishedWorks[i];
			sb.append("<tr><td>").append(aWork.getName()).append("</td>").append("<td>").append(aWork.getCompletedBy()).append("</td>").append("<td>").append(aWork.getCompletedAt()).append("</td>").append("<td>").append(aWork.getDecision()).append("</td>").append("</tr>").append(
					"<tr><td colspan=3>Context</td></tr>").append("<tr><td colspan=3><table><tbody>");
			Attachment[] atts = getWorkLogAttachment(finishedWorks[i]);
			for (int j = 0; j < atts.length; j++) {
				Attachment att = atts[j];
				sb.append("<tr><td>").append(att.getLabel()).append("</td>");
				sb.append("<td>").append(att.getType()).append("</td>");
				sb.append("<td>").append(att.getValue()).append("</td></tr>");
				context.put("value_" + att.getAttname(), att.getValue());
				logger.debug("value_" + att.getAttname() + "=" + att.getValue());
				if (att.getType() == null) {
					logger.error(att.getAttname() + " type is null");
				} else {
					if (att.getType().equals("url"))
						context.put("openurl_" + att.getAttname(), "<a href='" + att.getValue() + "'>" + att.getValue() + "</a>");
				}
			}
			sb.append("</tbody></table></td></tr>");

		}
		sb.append("</tbody></table>");
		history_html = sb.toString();

		String form_header = "<form id='form1' name='form1' action='/cflow/doTask' method='post'>";
		String form_content = "";
		sb = new StringBuffer();
		sb.append("<div id='workname'>").append(theWork.getName()).append("</div>");
		sb.append(acqmenu_html);
		sb.append("<table id='workcontext'>").append("<tr><td>").append("In:").append("</td>").append("<td>").append(thePrc.getWftname()).append("</td></tr>");
		sb.append("<tr><td>Start at:</td><td>").append(theWork.getCreatedAt()).append("</td></tr>");
		sb.append("<tr><td>Instruction:</td><td>").append(instruction).append("</td></tr>");
		sb.append("<tr><td colspan=2>").append(delegation_html).append("</td></tr>");
		sb.append("</table>");
		String form_content_header = sb.toString();
		if (!atts_html.equals(""))
			sb.append(atts_html);
		if (!options_html.equals(""))
			sb.append("Your Decision: ").append(options_html);
		form_content = sb.toString();

		sb = new StringBuffer();
		sb.append("<input type='hidden' id='token' name='token' value='").append(tokenString).append("'>");
		sb.append("<input type='hidden' id='prcid' name='prcid' value='").append(wii.getPrcid()).append("'>");
		sb.append("<input type='hidden' id='nodeid' name='nodeid' value='").append(wii.getNodeid()).append("'>");
		sb.append("<input type='hidden' id='sessid' name='sessid' value='").append(wii.getSessid()).append("'>");
		sb.append("<input type='hidden' id='option' name='option'>");
		sb.append("<input type='hidden' id='tid' name='tid' value='").append(tid).append("'>");
		sb.append("<input type='Submit' id='btn_submit' name='btn_submit' value='Done'>");
		sb.append("</form>");

		String form_footer = sb.toString();
		context.put("wii", wii);
		context.put("work", theWork);
		context.put("process", thePrc);
		context.put("wftname", thePrc.getWftname());
		context.put("instruction", instruction);
		context.put("acq_status", acqStatus);
		context.put("html_acqmenu", acqmenu_html);
		context.put("mates", mates);
		context.put("html_delegation", delegation_html);
		context.put("html_atts", atts_html);
		context.put("html_options", options_html);
		context.put("html_history", history_html);
		context.put("form_header", form_header);
		context.put("form_content", form_content);
		context.put("form_footer", form_footer);
		context.put("form_content_header", form_content_header);
		context.put("input", myInput);
		if (thePrc.getPbo() == null)
			context.put("PBO", "");
		else
			context.put("PBO", thePrc.getPbo());
		if (thePrc.getLastError() == null)
			context.put("LASTERROR", "");
		else
			context.put("LASTERROR", thePrc.getLastError());

		context.put("MATES", dbadmin.getAllUsers(dev));

		for (int i = 0; i < attachments.length; i++) {
			String key = (String) attachments[i].getAttname();
			String value = (String) context.get("value_" + key);
			if (value != null) {
				context.put("input_" + key, getInputHtml(attachments[i], value));
			}
		}
		String ret = "";

		Template tpl = CflowHelper.storageManager.getTemplate(dev, vtname);

		StringWriter sw = new StringWriter();
		tpl.merge(context, sw);

		ret = sw.toString();

		return ret;
	}

	private String getInputHtml(Attachment att, String value) {
		String ret = "";
		String attType = att.getType();
		if (attType.equals("text")) {
			ret = myInput.input(att.getAttname(), value);
		} else if (attType.equals("url")) {
			ret = myInput.url(att.getAttname(), value);
		} else if (attType.equals("int")) {
			ret = myInput.input(att.getAttname(), value);
		} else if (attType.equals("float")) {
			ret = myInput.input(att.getAttname(), value);
		} else if (attType.equals("netext")) {
			ret = myInput.input(att.getAttname(), value);
		}

		return ret;
	}
}
