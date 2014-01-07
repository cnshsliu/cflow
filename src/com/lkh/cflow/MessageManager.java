package com.lkh.cflow;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.messenger.MailThread;
import com.lkh.cflow.messenger.MessageComposer;

public class MessageManager {
	MessageComposer composer = null;

	public MessageManager() {

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initComposer(DbAdmin dbadmin, String prcId, String wftId, Node theNode, HashMap<String, String> roleAssignMap, String usrid) {
		try {
			if (this.composer == null) {
				String composerClassName = CflowHelper.cfg.getString("message.composer");
				Class cls = Class.forName(composerClassName);
				Constructor ct = cls.getConstructor(null);
				composer = (MessageComposer) ct.newInstance(null);
			}
		} catch (Throwable e) {
			System.err.println(e);
		}
		if (this.composer != null)
			composer.initContext(dbadmin, prcId, wftId, theNode, roleAssignMap, usrid);
	}

	public void sendMail(Sendmail mail, String[] address) {
		String content = "";
		String html = "";
		String subject = "";
		MessageManager msgMgr = new MessageManager();

		if (composer == null) {
			content = mail.getMessage().getStringValue();
			html = "";
			subject = mail.getSubject();
		} else {
			if (composer.includeDefaultMessage()) {
				content += mail.getMessage().getStringValue();
			}
			content += composer.getMessageText();
			subject += composer.getMessageSubject();
			html += composer.getMessageHTML();
		}

		new Thread(new MailThread(address, subject, content, html)).start();
	}

	public void sendSMS(Sendsms sms) {
		System.out.println("======================================");
		System.out.println("Send SMS to " + sms.getSmsto().getWhom());
		System.out.println(sms.getSubject());
		System.out.println(sms.getMessage());
		System.out.println("======================================");
	}

	public void sendMail(String[] to, String subject, String content, String html) {
		new Thread(new MailThread(to, subject, content, html)).start();
	}

	public void sendSms(String[] to, String subject, String content, String html) {
		/*
		 * Mail sendmail = new Mail(); sendmail.setHost(smtpHost);
		 * sendmail.setUserName(smtpAuthUser);
		 * sendmail.setPassWord(smtpAuthPass); sendmail.setTo(to);
		 * sendmail.setFrom(mailFrom); sendmail.setSubject(subject);
		 * sendmail.setContent(content); sendmail.setHTML(html);
		 * sendmail.sendMail();
		 */
		return;

	}

	public String[] getEmailAddresses(DbAdmin dbadmin, ProcessT prc, Sendmail sendmail) {
		String[] ret = {};
		/*
		 * ArrayList<String> list = new ArrayList<String>(); String
		 * mailTrapAddress = CflowHelper.cfg.getString("message.mailTrap");
		 * String mailtoType = sendmail.getMailto().getType(); String mailtoWhom
		 * = sendmail.getMailto().getWhom(); if(mailtoType.equals("role")){
		 * if(mailtoWhom.equals("starter")){ String usrid = prc.getStartby();
		 * User tmpUser; try { tmpUser = dbadmin.getDevById(usrid);
		 * list.add(tmpUser.email); } catch (SQLException e) {
		 * e.printStackTrace(); } }else{ String qe = "./team/role[@name='" +
		 * mailtoWhom + "']"; Role[] roles=
		 * CflowHelper.prcManager.queryTeamRoles(prc, qe); for(int i=0;
		 * i<roles.length; i++){ String usrid = roles[i].getValue(); User
		 * tmpUser; try { tmpUser = dbadmin.getDevById(usrid);
		 * list.add(tmpUser.email); } catch (SQLException e) {
		 * e.printStackTrace(); } } } }else if(mailtoType.equals("person")){
		 * String tmpUserId = mailtoWhom; try { User tmpUser =
		 * dbadmin.getDevById(tmpUserId); list.add(tmpUser.email); } catch
		 * (SQLException e) { e.printStackTrace(); } }else
		 * if(mailtoType.equals("team")){ String tmpTeamId = mailtoWhom; try {
		 * Team tmpTeam = dbadmin.getTeamById(tmpTeamId);
		 * list.add(tmpTeam.email); } catch (SQLException e) {
		 * e.printStackTrace(); } } if(list.size()==0){
		 * list.add(mailTrapAddress); } ret = list.toArray(ret);
		 */
		return ret;
	}
}