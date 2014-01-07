package com.lkh.cflow.messenger;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.MessageManager;


/**
 * 
 * @author LKH <BR>
 * 发送邮件的进程
 * 使用如下语句发送一封邮件：<BR>
 *  new MailThread(to, subject, content, html).run();
 */
public class MailThread implements Runnable {
	private String[] to;
	private String subject, content, html;
	
	/**
	 * 构造函数
	 * @param to 收件人邮箱地址
	 * @param subject 邮件主题
	 * @param content 邮件文本内容
	 * @param html 邮件HTML内容
	 */
	public MailThread(String[] to, String subject, String content, String html){
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.html = html;
	}
	
	/**
	 * 启动进程，发送邮件
	 * 
	 */
	public void run() {
		Mail sendmail = new Mail();
        sendmail.setHost(CflowHelper.cfg.getString("mail.smtpHost"));
        sendmail.setUserName(CflowHelper.cfg.getString("mail.smtpAuthUser"));
        sendmail.setPassWord(CflowHelper.cfg.getString("mail.smtpAuthPassword"));
        sendmail.setTo(to);
        sendmail.setFrom(CflowHelper.cfg.getString("mail.mailFrom"));
        sendmail.setSubject(subject);
        sendmail.setContent(content);
        sendmail.setHTML(html);
        sendmail.sendMail();
	}
}