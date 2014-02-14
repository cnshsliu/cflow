package com.lkh.cflow;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;
import com.lkh.cflow.script.ScriptHandler;
import com.lkh.cflow.storage.StorageManagerItf;

public class CflowHelper {
	public static final String ASSOC_TYPE_USER = "USER";
	public static final String ASSOC_TYPE_STARTER = "STARTER";
	public static final String ROLE_STARTER = "starter";
	public static final String TASKTO_TYPE_ROLE = "ROLE";
	public static final String TASKTO_TYPE_REFER = "RefertoNode";
	public static final String TASKTO_TYPE_PERSON = "person";
	public static final String PARENT_PROCESS_ID_NULL = null;
	public static final String PARENT_PROCESS_NODEID_NULL = null;
	public static final String PARENT_PROCESS_SESSID_NULL = null;
	public static final String NOTIFY_NONE = "N";
	public static final String NOTIFY_EMAIL = "E";
	public static final boolean NEW_TASK_STATUS = false;
	public static final boolean UPDATE_TASK_STATUS = true;
	public static final int TIMEOUT_CONNECTION = 3000;
	public static final int TIMEOUT_SO = 3000;
	public static final int SLEEP_AFTER_TASK = 500;

	/**
	 * 工作项运行状态：结束
	 */
	public static final String WORK_FINISHED = "finished";
	/**
	 * 工作项运行状态：运行中
	 */
	public static final String WORK_RUNNING = "running";
	/**
	 * 工作项运行状态：暂停
	 */
	public static final String WORK_SUSPENDED = "suspended";
	/**
	 * 当工作已分派，而且是OR的前置节点，其中一个完成时，其它的被ignored.
	 */
	public static final String WORK_IGNORED = "ignored";
	public static final String TASK_RUNNING = "task_running";
	public static final String TASK_ACQUIRED = "task_acquired";
	public static final String TASK_FINISHED = "task_finished";
	public static final String TASK_DELEGATED = "task_delegated";
	public static final String TASK_DEFERRED = "task_deferred";
	public static final String DELEGATE_FROM_SYSTEM = "SYSTEM";

	public static final String ACQ_NOT_ACQUIRABLE = "NOT_ACQUIRABLE";
	public static final String ACQ_ALREADY_ACQUIRED = "ALREADY_ACQUIRED";
	public static final String ACQ_CAN_ACQUIRE = "CAN_ACQUIRE";
	public static final String ACQ_BY_OTHERS = "ACQUIRED_BY_OTHERS";

	/**
	 * 进程运行状态：运行中
	 */
	public static final String PROCESS_RUNNING = "running";
	/**
	 * 进程运行状态：暂停
	 */
	public static final String PROCESS_SUSPENDED = "suspended";
	/**
	 * 进程运行状态：已完成
	 */
	public static final String PROCESS_FINISHED = "finished";
	/**
	 * 进程运行状态：已取消
	 */
	public static final String PROCESS_CANCELED = "canceled";

	/**
	 * 节点类型： 普通任务
	 */
	public static final String NODE_TYPE_TASK = "task";
	/**
	 * 节点类型： 提醒
	 */
	public static final String NODE_TYPE_NOTIFY = "notify";
	/**
	 * 节点类型： 逻辑与
	 */
	public static final String NODE_TYPE_AND = "and";
	/**
	 * 节点类型： 逻辑或
	 */
	public static final String NODE_TYPE_OR = "or";
	/**
	 * 节点类型: 新一轮
	 */
	public static final String NODE_TYPE_ROUND = "round";
	/**
	 * 节点类型： 超时计时器
	 */
	public static final String NODE_TYPE_TIMER = "timer";
	/**
	 * 节点类型： 表达式
	 */
	public static final String NODE_TYPE_SCRIPT = "script";
	/**
	 * 节点类型： 开始节点
	 */
	public static final String NODE_TYPE_START = "start";
	/**
	 * 节点类型： 终止节点
	 */
	public static final String NODE_TYPE_END = "end";
	public static final String NODE_TYPE_GROUND = "ground";
	/**
	 * 节点类型： 子流程节点
	 */
	public static final String NODE_TYPE_SUB = "sub";

	/**
	 * 参数类型： 字符串
	 */
	public static final String ATT_TYPE_STRING = "string";
	/**
	 * 参数类型： URL
	 */
	public static final String ATT_TYPE_URL = "url";
	/**
	 * 参数类型： 整数
	 */
	public static final String ATT_TYPE_INT = "int";
	/**
	 * 参数类型： 浮点数
	 */
	public static final String ATT_TYPE_FLOAT = "float";
	/**
	 * 参数类型： 纯文本
	 */
	public static final String ATT_TYPE_NETEXT = "netext";
	/**
	 * 参数类型： 邮箱地址
	 */
	public static final String ATT_TYPE_EMAIL = "email";

	/**
	 * 缺省选项值
	 */
	public static final String DEFAULT_OPTION = "DEFAULT";
	public static final String ERROR_OPTION = "onerror";
	public static final String REPEAT_OPTION_DEFUALT = "DEFAULT";
	public static final String REPEAT_OPTION_FINISH = "FINISH";
	public static final String REPEAT_TYPE_NOREPEAT = "NOREPEAT";
	public static final String REPEAT_TYPE_DATE = "DATE";

	public static final String TIMER = "TIMER";

	public static final String PAGE_DOTASK_ERROR = "page_dotask_error";
	public static final String PAGE_DOTASK_POST = "page_dotask_post";
	public static final String PAGE_LOGIN = "page_login";

	public static final int PANEL_NONE = 0;
	public static final int PANEL_DEFAULT = 1;
	private static final String HEX_NUMS_STR = "0123456789ABCDEF";

	public final static ExecutorService scriptExecutor = Executors.newCachedThreadPool();
	public static WftManager wftManager = new WftManager();
	public static ScriptHandler scriptHandler = new ScriptHandler(scriptExecutor);
	public static ProcessManager prcManager = new ProcessManager();
	public static SchedulerManager schManager = new SchedulerManager();
	public static DelegateManager dlgManager = new DelegateManager();
	public static MessageManager gm = new MessageManager();
	public static StorageManagerItf storageManager = null;
	public static DruidConnector dbConnector = null;
	// cfg is initialized in QuartzInitializerListner.java
	public static XMLConfiguration cfg = null;
	private static int counter = 0;
	public static String[] enterpriseRoles = {};
	public static String[] administrators = {};
	public static String[] defaultRoles = {};
	public static String[] scriptSafe = {};
	public static int sessionCount = 0;
	public static int dbadminCount = 0;
	public static int customAttachmentLowIndex = 100;
	public static int customAttachmentHighIndex = 200;
	public static int greenThreshold = 1440;
	public static int yellowThreshold = 5;
	private static String[] appContexts = {};
	public static String[] timeZoneIds = { "GMT-12:00", "GMT-11:00", "GMT-10:00", "GMT-09:00", "GMT-08:00", "GMT-07:00", "GMT-06:00", "GMT-05:00", "GMT-04:30", "GMT-04:00", "GMT-03:30", "GMT-03:00", "GMT-02:00", "GMT-01:00", "GMT+00:00", "GMT+01:00", "GMT+02:00", "GMT+03:00", "GMT+03:30",
			"GMT+04:00", "GMT+04:30", "GMT+05:00", "GMT+05:30", "GMT+05:45", "GMT+06:00", "GMT+06:30", "GMT+07:00", "GMT+08:00", "GMT+09:00", "GMT+09:30", "GMT+10:00", "GMT+11:00", "GMT+12:00", "GMT+13:00" };
	public static String defaultTimeZone = "GMT+00:00";
	public static String dateFormat = "yyyy-MM-dd HH:mm";

	public static Logger logger = Logger.getLogger(CflowHelper.class);

	public static Hashtable<String, ResourceBundle> rbs = new Hashtable<String, ResourceBundle>();

	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private static String r = "";
	public static int DEFAULT_WFTLIMIT = 10;
	public static int DEFAULT_PRCLIMIT = 10;
	public static String SAAS = "PASS8TSAAS";
	public static int UM_UNKNOWN = 0;
	public static int UM_GROUP = 1;
	public static int UM_ENTERPRISE = 2;

	public static XmlOptions xmlOptions = new XmlOptions();
	public static LRUMap lruDocLock = null;
	public static LRUMap lruDevServer = null;

	private final static String[] h2TcpServer_args = new String[] { "-tcpPort", "8092" };

	private static org.h2.tools.Server h2TcpServer = null;
	public static String myHost = null;
	public static boolean isMaster = false;
	public static boolean useHA = false;
	public static HashMap<String, Long> masterTable = new HashMap<String, Long>();
	public static HashMap<String, Long> serverTable = new HashMap<String, Long>();
	public static HashMap<String, Long> syncedServerTable = new HashMap<String, Long>();

	public CflowHelper() throws Exception {

	}

	public static void setConfigFile(String filePath) throws ConfigurationException {
		cfg = new XMLConfiguration(filePath);
	}

	/**
	 * 初始化CflowHelper
	 * 
	 * @throws Exception
	 */
	public static void initialize(String webPath) throws Exception {
		myHost = cfg.getString("host");
		xmlOptions.setSavePrettyPrint();
		xmlOptions.setSavePrettyPrintIndent(4);
		defaultTimeZone = cfg.getString("calendar.defaultTimeZone", defaultTimeZone);
		dateFormat = cfg.getString("calendar.dateFormat", dateFormat);

		appContexts = StringUtils.split(cfg.getString("appContext", "web"), '/');
		// start h2 in-memo database
		TokenAdmin.initialize(cfg.getBoolean("inmemdb.tcp", false));

		dbConnector = DruidConnector.getInstance();
		customAttachmentLowIndex = cfg.getInt("attachment.lowIndex", 100);
		customAttachmentHighIndex = cfg.getInt("attachment.highIndex", 200);

		greenThreshold = cfg.getInt("timer.threshold.green", 1440);
		yellowThreshold = cfg.getInt("timer.threshold.yellow", 5);
		defaultRoles = getDefaultRoles();
		scriptSafe = getScriptSafe();

		String storageManagerClassname = cfg.getString("storage.manager.classname");
		Class cls = Class.forName(storageManagerClassname);
		Constructor ct = cls.getConstructor(null);
		storageManager = (StorageManagerItf) ct.newInstance((Object[]) null);
		storageManager.guard(webPath);

		lruDocLock = new LRUMap(cfg.getInt("doc.lock.number", 100000));
		lruDevServer = new LRUMap(cfg.getInt("devServerMap.number", 10000));

		for (int i = 0; i < 10; i++) {
			String masterKey = "ha.master-" + i;
			String masterValue = cfg.getString(masterKey);
			if (masterValue == null) {
				break;
			} else {
				masterTable.put(masterValue, -1L);
				if (masterValue.equals(cfg.getProperty("host"))) {
					isMaster = true;
				}
			}
		}
		if (masterTable.size() > 0) {
			useHA = true;
		} else {
			useHA = false;
		}
	}

	public static void shutdown() {
		if (scriptExecutor != null) {
			scriptExecutor.shutdownNow();
		}

		try {
			if (h2TcpServer != null)
				h2TcpServer.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * decrypt
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String f(String str) throws Exception {
		String licHex;
		byte[] licBytes;
		licBytes = hexStringToByte(str);
		licHex = new String(licBytes);
		licBytes = hexStringToByte(licHex);
		return new String(licBytes);
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] hexChars = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
		}
		return result;
	}

	public static String byteToHexString(byte[] b) {
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

	/**
	 * 答应连接池状态
	 * 
	 * @param pre
	 */
	public static void printDBCPStatus(String pre) {
		int numActive = dbConnector.getNumActive();
		int numIdle = dbConnector.getNumIdle();
		System.out.println(pre + ">> Session: " + sessionCount + " DbAdmin: " + dbadminCount + " Active: " + numActive + " Idle:" + numIdle);
	}

	/**
	 * 去配置项host
	 * 
	 * @return Host
	 */
	public static String getHost() {
		return cfg.getString("host");
	}

	/**
	 * 取工作流模板存放路径
	 * 
	 * @return 工作流模板存放vault目录
	 */
	public static String getWftVaultPath() {
		return cfg.getString("storage.wftVault");
	}

	/**
	 * 取进程文件存放路径
	 * 
	 * @return 进程vault目录路径
	 */
	public static String getPrcVaultPath() {
		return cfg.getString("storage.prcVault");
	}

	public static String getIstPath(String usrid, String templateId) {
		return cfg.getString("storage.istVault") + File.separatorChar + "I" + templateId + ".json";
	}

	/**
	 * 按模板路径取模板缩略图的全路径
	 * 
	 * @param wftId
	 * @return 缩略图存放路径
	 */
	public static String getSnapshotPath(String wftId) {
		return cfg.getString("storage.snapshotVault") + File.separatorChar + wftId + ".jpg";
	}

	/**
	 * 按进程ID去进程文件的全路径
	 * 
	 * @param prcId
	 * @return 进程文件路径
	 */

	/**
	 * 取网页地址
	 * 
	 * @param pagePath
	 * @return pages path
	 */
	public static String getPage(String pagePath) {
		if (pagePath.equals(PAGE_DOTASK_ERROR)) {
			return cfg.getString("page.doTask.error", "/cflow/doTaskErr.jsp");
		} else if (pagePath.equals(PAGE_DOTASK_POST)) {
			return cfg.getString("page.doTask.post", "/cflow/Worklist.jsp");
		} else if (pagePath.equals(PAGE_LOGIN)) {
			return cfg.getString("page.login", "/cflow/Login.jsp");
		} else
			return "/cflow";
	}

	/**
	 * 判断一个用户是否是管理员。企业版起作用。 管理员定义在configuration文件中，enterprise.administrators
	 * 
	 * @param uid
	 *            用户的UserID
	 * @return true，是管理员； false, 不是管理员；
	 */
	public static boolean isAdministrator(String uid) {
		boolean ret = false;
		for (int i = 0; i < administrators.length; i++) {
			if (administrators[i].equals(uid)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	private static String[] getEnterpriseRoles() {
		String tmpS = cfg.getString("enterprise.default.roles", "USER");
		return tmpS.split("/");
	}

	private static String[] getDefaultRoles() {
		String tmpS = cfg.getString("default.roles", "USER");
		return tmpS.split("/");
	}

	private static String[] getScriptSafe() {
		String tmpS = cfg.getString("scriptsafe.wftId", "xjd1@d&DjdaAda");
		return tmpS.split("/");
	}

	private static String[] getAdministrators() {
		String tmpS = cfg.getString("enterprise.administrators", "root");
		return tmpS.split("/");
	}

	/**
	 * 是否为企业版 通过配置项 enterprise.version来定制
	 * 
	 * @return 是否为企业版
	 */
	public static boolean isEnterpriseVersion() {
		return cfg.getString("enterprise.version", "false").equalsIgnoreCase("true");
	}

	// TODO check input values are validate or not.(must be number, isnull etc.)
	// TODO Can modify XSD? attachment type : text, int, float, url, not null
	// text?
	// TODO check with javascript? if javascript is disabled, don't show submit
	// button(button is displayed with script

	public static int byteToInt2(byte[] b) {

		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < 4; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}

	/**
	 * Pass8t是否运行在某个Context里面。Context的配置在configuration.xml文件中的
	 * appContext节，可以定义多个context, 用/分隔。
	 * 
	 * @param ctx
	 * @return Cflow的运行context
	 */
	public static boolean isInContext(String ctx) {
		for (int i = 0; i < appContexts.length; i++) {
			if (appContexts[i].equalsIgnoreCase(ctx)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		try {
			String e = myID();
			System.out.println(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean sessionExpired(HttpSession session) {
		if (session.getAttribute("USRID") == null || session.getAttribute("DBADMIN") == null || session.getAttribute("THEUSER") == null || session.getAttribute("THEORG") == null)
			return true;
		else
			return false;

	}

	public static boolean checkError(String message, String... str) {
		for (String chk : str) {
			if (message.indexOf(chk) < 0)
				return false;
		}

		return true;
	}

	public static boolean matchString(String message, String... str) {
		for (String chk : str) {
			if (message.equals(chk))
				return true;
		}

		return false;
	}

	public static boolean startsWithString(String message, String... str) {
		for (String chk : str) {
			if (message.startsWith(chk))
				return true;
		}

		return false;
	}

	public static boolean isMobile(HttpServletRequest request) {
		String isMobile = request.getParameter("isMobile");
		if (isMobile == null)
			return false;
		else {
			if (isMobile.equalsIgnoreCase("true"))
				return true;
			else
				return false;
		}
	}

	public static boolean getBoolean(HttpServletRequest request, String key, boolean defaultValue) {
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

	public static String getString(HttpServletRequest request, String key, String defaultValue) {
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

	public static int getInt(HttpServletRequest request, String key, int defaultValue) {
		String tmp = getString(request, key, "" + defaultValue);
		int ret = Integer.valueOf(tmp).intValue();
		return ret;
	}

	public static String clientAgentLang(HttpServletRequest request) {
		String lang = null;
		String languagesStr = request.getHeader("accept-language");
		if (StringUtils.isBlank(languagesStr)) {
			languagesStr = "en-US";
		}
		String[] languages = StringUtils.split(languagesStr, ',');
		lang = languages[0];
		return lang;
	}

	public static String wftMode(HttpServletRequest request) {
		String wftmode = null;

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			int cookieLength = cookies.length;
			for (int i = 0; i < cookieLength; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals("cflow_wftmode")) {
					wftmode = cookie.getValue();
					break;
				}
			}
		}
		if (wftmode == null) {
			wftmode = "todo";
		}
		return wftmode;
	}

	/**
	 * 获得客户端的语言设置。
	 * 首先，从request中取userLang值，如果没有，则从Cookie中取，如果没有Cookie，再从User-Agent中取accpet
	 * language.
	 * 
	 * @param request
	 * @return
	 */
	public static String userLang(HttpServletRequest request) {
		String cookie_lang = null;
		String browser_lang = null;
		String user_lang = null;

		user_lang = getString(request, "userLang", null);
		if (user_lang == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				int cookieLength = cookies.length;
				for (int i = 0; i < cookieLength; i++) {
					Cookie cookie = cookies[i];
					if (cookie.getName().equals("cflow_lang")) {
						cookie_lang = cookie.getValue();
						break;
					}
				}
			}
			browser_lang = clientAgentLang(request);
			int idx = browser_lang.indexOf('-');
			if (browser_lang.indexOf('-') > 0) {
				browser_lang = browser_lang.substring(0, idx + 1) + browser_lang.substring(idx + 1).toUpperCase();
			}
			if (cookie_lang == null) {
				user_lang = browser_lang;
			} else {
				user_lang = cookie_lang;
			}
		}

		if (StringUtils.isBlank(user_lang))
			user_lang = "en-US";

		// 目前仅支持en_US， zh_CN
		if (matchString(user_lang, "en-US", "zh-CN", "fr-FR", "ja-JP", "zh-TW")) {
			return user_lang;
		} else if (startsWithString(user_lang, "zh-")) {
			return "zh-TW";
		} else {
			return "en-US";
		}
	}

	public static String getIpAddr(HttpServletRequest request) {
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

	public static String composeSid(String clientIP, String usrid) {
		String sid = "";
		sid = clientIP + "$$" + usrid;
		return sid;
	}

	public static String getUserIdFromSid(String sid) {
		String uid = null;
		if (sid.indexOf("$$") > 0) {
			uid = sid.substring(sid.indexOf("$$") + 2);
		} else {
			uid = null;
		}

		return uid;
	}

	public static String getIpFromSid(String sid) {
		String ip = null;
		if (sid.indexOf("$$") > 0) {
			ip = sid.substring(0, sid.indexOf("$$"));
		} else {
			ip = null;
		}

		return ip;

	}

	public static String validateSid(HttpServletRequest request) {
		String sid = getString(request, "sid", null);
		if (sid == null)
			return null;
		else {
			String ret = getUserIdFromSid(sid);
			if (ret == null) {
				return null;
			} else {
				String ip = getIpFromSid(sid);
				if (ip == null)
					return null;
				else {
					String clientIp = getIpAddr(request);
					if (!clientIp.equals(ip)) {
						return null;
					} else {
						return ret;
					}
				}
			}
		}
	}

	public static String validateSid(HttpServletRequest request, String sid) {
		if (sid == null)
			return null;
		else {
			String ret = getUserIdFromSid(sid);
			if (ret == null) {
				return null;
			} else {
				String ip = getIpFromSid(sid);
				if (ip == null)
					return null;
				else {
					String clientIp = getIpAddr(request);
					if (!clientIp.equals(ip)) {
						return null;
					} else {
						return ret;
					}
				}
			}
		}
	}

	public static String showTime(Calendar cal) {
		return showTime(cal, CflowHelper.defaultTimeZone);
	}

	public static String showTime(Calendar cal, String tzId) {
		if (cal == null)
			return "";
		TimeZone tz = TimeZone.getTimeZone(tzId);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		formatter.setTimeZone(tz);
		return formatter.format(cal.getTime());
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAttachmentJSON(Attachment theAttachment) {
		JSONObject att = new JSONObject();
		att.put("NAME", theAttachment.getAttname());
		att.put("VALUE", theAttachment.getValue());
		return att;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getWorkJSON(Work theWork) {
		JSONObject ret = new JSONObject();
		ret.put("NAME", theWork.getName());
		ret.put("TITLE", theWork.getTitle());
		ret.put("CREATEDAT", showTime(theWork.getCreatedAt()));
		ret.put("FORM", theWork.getForm());
		ret.put("ALLOWDELEGATE", theWork.getAllowDelegate());
		if (theWork.getCompletedAt() != null)
			ret.put("COMPLETEDAT", showTime(theWork.getCompletedAt()));
		if (!StringUtils.isBlank(theWork.getCompletedBy()))
			ret.put("COMPLETEDBY", theWork.getCompletedBy());
		ret.put("STATUS", theWork.getStatus());
		ret.put("FORM", theWork.getForm());
		if (!StringUtils.isBlank(theWork.getDecision()))
			ret.put("DECISION", theWork.getDecision());
		ret.put("NODEID", theWork.getNodeid());
		JSONArray logList = new JSONArray();
		Log[] logs = theWork.getLogArray();
		for (int i = 0; i < logs.length; i++) {
			JSONObject logJS = new JSONObject();
			Attachment[] atts = logs[i].getAttachmentArray();
			JSONArray attArr = new JSONArray();
			for (int j = 0; j < atts.length; j++) {
				attArr.add(getAttachmentJSON(atts[j]));
			}
			logJS.put("USER", logs[i].getUsrid());
			logJS.put("OPTION", logs[i].getOption());
			logJS.put("ATTACHMENTS", attArr);
			logList.add(logJS);
		}
		ret.put("LOG", logList);

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getRoleJSON(Role theRole) {
		JSONObject ret = new JSONObject();
		ret.put("NAME", theRole.getName());
		ret.put("VALUE", theRole.getValue());

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getWorkContext(ProcessT thePrc, Work theWork) {
		JSONObject ret = new JSONObject();

		ret.put("PRCID", thePrc.getId());
		ret.put("PBO", thePrc.getPbo());
		ret.put("WORK", getWorkJSON(theWork));

		return ret;
	}

	public static HttpSession getSession(HttpServletRequest req) {
		HttpSession session = req.getSession();

		// Invalidate the session if it's more than a day old or has been
		// inactive for more than an hour.
		if (!session.isNew()) { // skip new sessions
			Date dayAgo = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
			Date hourAgo = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
			Date created = new Date(session.getCreationTime());
			Date accessed = new Date(session.getLastAccessedTime());

			if (created.before(dayAgo) || accessed.before(hourAgo)) {
				session.invalidate();
				session = req.getSession(); // get a new session
			}
		}
		return session;
	}

	/**
	 * Get DbAdmin from session. if session is null, create a new dbamdin and
	 * return it. if there is not a dbadmin in session, create a new DbAdmin,
	 * put it into session, and return it. If there is a dbamdin in session, get
	 * it and return it.
	 * 
	 * @param session
	 * @return
	 */
	public static DbAdmin getDbAdmin(HttpSession session) {
		DbAdmin dbadmin = null;
		if (session == null) {
			dbadmin = new DbAdmin();
		} else {
			dbadmin = (DbAdmin) session.getAttribute("DBADMIN");
			if (dbadmin == null) {
				dbadmin = new DbAdmin();
				session.setAttribute("DBADMIN", dbadmin);
			}
		}

		return dbadmin;
	}

	public static DbAdmin getDbAdmin(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null)
			return getDbAdmin(session);
		else {
			return new DbAdmin();
		}
	}

	public static boolean isScriptSafe(String wftId) {
		/*
		 * for(int i=0; i<scriptSafe.length; i++){
		 * if(wftId.startsWith(scriptSafe[i])) return true; } return false;
		 */

		return true;
	}

	@SuppressWarnings("unchecked")
	public static String getRequestString(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		String pname = "";
		for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			pname = (String) e.nextElement();
			sb.append("[").append(pname).append("=").append(request.getParameter(pname)).append("]");
		}

		return sb.toString();
	}

	public static Calendar getMonthInNextSeason(Calendar cTime, int nextS, int monthPos) {
		Calendar ret = Calendar.getInstance();
		ret.set(Calendar.YEAR, cTime.get(Calendar.YEAR));
		ret.set(Calendar.MONTH, cTime.get(Calendar.MONTH));
		int thisMonth = cTime.get(Calendar.MONTH);
		int y = nextS / 4;
		int s = nextS - 4 * y;
		ret.set(Calendar.YEAR, cTime.get(Calendar.YEAR) + y);
		if (thisMonth >= Calendar.JANUARY && thisMonth <= Calendar.MARCH) {
			if (s == 0) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.JANUARY);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.FEBRUARY);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.MARCH);
				}
			} else if (s == 1) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.APRIL);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.MAY);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.JUNE);
				}
			} else if (s == 2) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.JULY);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.AUGUST);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.SEPTEMBER);
				}
			} else if (s == 3) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.OCTOBER);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.NOVEMBER);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.DECEMBER);
				}
			}
		} else if (thisMonth >= Calendar.APRIL && thisMonth <= Calendar.JUNE) {
			if (s == 0) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.APRIL);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.MAY);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.JUNE);
				}
			} else if (s == 1) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.JULY);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.AUGUST);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.SEPTEMBER);
				}
			} else if (s == 2) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.OCTOBER);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.NOVEMBER);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.DECEMBER);
				}
			} else if (s == 3) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.JANUARY);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.FEBRUARY);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.MARCH);
				}
			}
		} else if (thisMonth >= Calendar.JULY && thisMonth <= Calendar.SEPTEMBER) {
			if (s == 0) {

				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.JULY);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.AUGUST);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.SEPTEMBER);
				}
			} else if (s == 1) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.OCTOBER);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.NOVEMBER);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.DECEMBER);
				}
			} else if (s == 2) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.JANUARY);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.FEBRUARY);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.MARCH);
				}
			} else if (s == 3) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.APRIL);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.MAY);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.JUNE);
				}
			}
		} else if (thisMonth >= Calendar.OCTOBER && thisMonth <= Calendar.DECEMBER) {
			if (s == 0) {

				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.OCTOBER);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.NOVEMBER);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.DECEMBER);
				}
			} else if (s == 1) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.JANUARY);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.FEBRUARY);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.MARCH);
				}
			} else if (s == 2) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.APRIL);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.MAY);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.JUNE);
				}
			} else if (s == 3) {
				if (monthPos == 0) {
					ret.set(Calendar.MONTH, Calendar.JULY);
				} else if (monthPos == 1) {
					ret.set(Calendar.MONTH, Calendar.AUGUST);
				} else if (monthPos == 2) {
					ret.set(Calendar.MONTH, Calendar.SEPTEMBER);
				}
			}
		}

		return ret;
	}

	public static String validateCronExpression(String usrid, String cronExpression) {
		String ret = cronExpression;
		if (!usrid.equals("U3306")) {
			String[] arr = StringUtils.split(cronExpression, " ");
			if (arr[0].equals("0")) {
				arr[0] = "0";
			}
			int minMinute = cfg.getInt("cron.interval.min", 5);
			// 不允许使用,和-
			if (arr[1].indexOf(',') > 0 || arr[1].indexOf('-') > 0) {
				arr[1] = "0";
			} else {
				if (arr[1].indexOf('/') > 0) {
					String[] tarr = StringUtils.split(arr[1], "/");
					if (Integer.valueOf(tarr[1]).intValue() < minMinute) {
						arr[1] = tarr[0] + "/" + minMinute;
					}
				}
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getPrcContextJSON(ProcessT prc, WorkflowDocument.Workflow theWf, Node theNode) {
		JSONObject prcObj = new JSONObject();
		prcObj.put("ID", "" + prc.getId());
		prcObj.put("WFTID", "" + prc.getWftid());
		prcObj.put("WFTNAME", "" + prc.getWftname());
		prcObj.put("PARENT_PRC_ID", "" + prc.getParentProcessId());
		prcObj.put("PARENT_PRC_NODE_ID", "" + prc.getParentProcessNodeId());
		prcObj.put("PARENT_PRC_SESS_ID", "" + prc.getParentProcessSessId());
		prcObj.put("PBO", "" + prc.getPbo());
		prcObj.put("STARTAT", "" + prc.getStartat());
		prcObj.put("STARTBY", "" + prc.getStartby());
		prcObj.put("STATUS", "" + prc.getStatus());
		prcObj.put("TEAMID", "" + prc.getTeamid());
		prcObj.put("NODEID", "" + theNode.getId());

		return prcObj;
	}

	public static String remoteCall(String remoteUrl, JSONObject ctx) throws Exception {
		String ret = "";
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

		HttpClient httpClient = new HttpClient(connectionManager);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SO);

		PostMethod postMethod = new PostMethod(remoteUrl);
		NameValuePair[] data = { new NameValuePair("CTX", ctx.toString()) };
		postMethod.setRequestBody(data);
		postMethod.getParams().setSoTimeout(TIMEOUT_SO);
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			byte[] responseBody = postMethod.getResponseBody();
			ret = new String(responseBody);
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
			JSONObject retObj = new JSONObject();
			retObj.put("RETURN", "timeout");
			retObj.put("error", "Call (" + remoteUrl + ") timeout or IO exception:" + ex.getLocalizedMessage());
			ret = retObj.toJSONString();
		} finally {
			postMethod.releaseConnection();
		}

		return ret;
	}

	public static String[] removeDuplicate(String[] strs) {
		List<String> list = Arrays.asList(strs);
		Set<String> set = new HashSet<String>(list);
		String[] temp = new String[set.size()];
		Iterator<String> ite = set.iterator();
		for (int i = 0; ite.hasNext(); i++) {
			temp[i] = ite.next();
		}
		return temp;
	}

	public static String[] splitEmails(String emailString) {
		String[] ret = {};
		ArrayList<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile("[;, \t]");
		String[] emails = p.split(emailString);
		for (int i = 0; i < emails.length; i++) {
			if (emails[i].trim().length() > 0)
				list.add(emails[i]);
		}
		if (list.isEmpty())
			return ret;
		else
			return (String[]) list.toArray(ret);
	}

	public static String myID() {
		String ret = java.util.UUID.randomUUID().toString().replaceAll("-", "");
		return ret;
	}

	public static ParsedValue getValueObject(Attachment att) {
		String val = att.getValue();
		ParsedValue ret = null;
		if (att.getType().equals(ATT_TYPE_INT)) {
			try {
				ret = new ParsedValue(Integer.parseInt(val), "", true);
			} catch (NumberFormatException ex) {
				ret = new ParsedValue(-1, "(" + att.getAttname() + "=" + val + ") NumberFormatException: " + ex.getLocalizedMessage() + ", returned: -1", false);
			}
		} else if (att.getType().equals(ATT_TYPE_FLOAT)) {
			try {
				ret = new ParsedValue(Float.parseFloat(val), "", true);
			} catch (NumberFormatException ex) {
				ret = new ParsedValue(-1.0, "(" + att.getAttname() + "=" + val + ") NumberFormatException: " + ex.getLocalizedMessage() + ", returned: -1.0", false);
			}
		} else if (att.getType().equals(ATT_TYPE_URL))
			ret = new ParsedValue(val, "", true);
		else if (att.getType().equals(ATT_TYPE_STRING))
			ret = new ParsedValue(val, "", true);
		else if (att.getType().equals(ATT_TYPE_NETEXT))
			ret = new ParsedValue(val, "", true);
		else if (att.getType().equals(ATT_TYPE_EMAIL))
			ret = new ParsedValue(val, "", true);
		else
			ret = new ParsedValue("", "(" + att.getAttname() + "=" + val + ") value type (" + att.getType() + ") is unsupported, returned empty string", false);

		return ret;
	}

	public static String getReturedValueType(Object val) {
		String ret = "";
		if (val instanceof java.lang.Long || val instanceof java.lang.Integer) {
			ret = ATT_TYPE_INT;
		} else if (val instanceof java.lang.Float || val instanceof java.lang.Double) {
			ret = ATT_TYPE_FLOAT;
		} else if (val instanceof java.lang.String) {
			String tmp = (java.lang.String) val;
			tmp = StringUtils.lowerCase(tmp);
			if (tmp.startsWith("http:")) {
				ret = ATT_TYPE_URL;
			} else {
				ret = ATT_TYPE_STRING;
			}
		} else {
			// 其它数据类型不支持。
			ret = ATT_TYPE_STRING;
		}

		return ret;
	}

	public static Developer getDevByToken(String token) {
		Developer dev = null;
		DbAdmin dbadmin = DbAdminPool.get();
		try {
			dev = dbadmin.getDevByToken(token);
			return dev;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			DbAdminPool.ret(dbadmin);
		}

	}
}
