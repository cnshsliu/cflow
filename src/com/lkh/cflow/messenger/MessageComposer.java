package com.lkh.cflow.messenger;

import java.util.HashMap;

import com.lkh.cflow.Node;
import com.lkh.cflow.db.DbAdmin;

/**
 * 
 * @author LKH<BR>
 * <BR>
 * 
 *         进程提醒邮件内容自定义
 * 
 *         进程邮件提醒内容定制类必须定义在配置文件中才会发挥作用 如果是Windows, 修改configruation_win.xml,
 *         如果是Linux系统，修改configuration_unix.xml, 添加：<BR>
 * 
 *         <pre>
 *  <message>
 * 	<composer>CLASSNAME</composer>
 * 		</message>
 * 	如：
 * 	<message>
 * 		<composer>com.gsh.test.MyComposer</composer>
 * 	</message>
 * </pre>
 */
public interface MessageComposer {
	/**
	 * 返回是否包含模板中所定义的邮件内容
	 * 
	 * @param dbadmin
	 *            DbAdmin对象
	 * @param prcId
	 *            当前进程的编号
	 * @param prc
	 *            当前进程对象
	 * @param theWf
	 *            当前工作流模板对象
	 * @param theNode
	 *            当前工作流模板节点对象
	 * @param roleAssignMap
	 *            用户角色匹配表
	 * @param usrid
	 *            当前用户的UserId
	 */
	public void initContext(DbAdmin dbadmin, String prcId, String wftId, Node theNode, HashMap<String, String> roleAssignMap, String usrid);

	/**
	 * 自定义邮件标题
	 * 
	 * @return 自定义的邮件标题
	 */
	public String getMessageSubject();

	/**
	 * 自定义文本格式的邮件正文
	 * 
	 * @return 自定义的邮件正文
	 */
	public String getMessageText();

	/**
	 * 自定义HTML格式的邮件正文
	 * 
	 * @return 自定义的HTML格式邮件正文
	 */
	public String getMessageHTML();

	/**
	 * 返回是否包含模板中所定义的邮件内容
	 * 
	 * @return true表示包含模板中所定义的邮件内容， false表示不包含
	 */
	public boolean includeDefaultMessage();

}