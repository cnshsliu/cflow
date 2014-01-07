package com.lkh.cflow.messenger;

import java.util.Vector;

import javax.mail.internet.MimeUtility;

/**
 * <p>
 * Title: 使用javamail发送邮件
 * </p>
 * <p>
 * Description: 演示如何使用javamail包发送电子邮件。这个实例可发送多附件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Filename: Mail.java
 * </p>
 * 
 * @version 1.0
 */
public class Mail {

	String[] to = {};// 收件人
	String from = "";// 发件人
	String host = "";// smtp主机
	String username = "";
	String password = "";
	String filename = "";// 附件文件名
	String subject = "";// 邮件主题
	String content = "";// 邮件正文
	String html = "";
	Vector<String> file = new Vector<String>();// 附件文件集合

	/**
	 * <br>
	 * 方法说明：默认构造器 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public Mail() {
	}

	/**
	 * <br>
	 * 方法说明：构造器，提供直接的参数传入 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public Mail(String[] to, String from, String smtpServer, String username, String password, String subject, String content, String html) {
		this.to = to;
		this.from = from;
		this.host = smtpServer;
		this.username = username;
		this.password = password;
		this.subject = subject;
		this.content = content;
		this.html = html;
	}

	/**
	 * <br>
	 * 方法说明：设置邮件服务器地址 <br>
	 * 输入参数：String host 邮件服务器地址名称 <br>
	 * 返回类型：
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * <br>
	 * 方法说明：设置登录服务器校验密码 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void setPassWord(String pwd) {
		this.password = pwd;
	}

	/**
	 * <br>
	 * 方法说明：设置登录服务器校验用户 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void setUserName(String usn) {
		this.username = usn;
	}

	/**
	 * <br>
	 * 方法说明：设置邮件发送目的邮箱 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void setTo(String[] to) {
		this.to = to;
	}

	/**
	 * <br>
	 * 方法说明：设置邮件发送源邮箱 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * <br>
	 * 方法说明：设置邮件主题 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * <br>
	 * 方法说明：设置邮件内容 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void setContent(String content) {
		this.content = content;
	}

	public void setHTML(String html) {
		this.html = html;
	}

	/**
	 * <br>
	 * 方法说明：把主题转换为中文 <br>
	 * 输入参数：String strText <br>
	 * 返回类型：
	 */
	public String transferChinese(String strText) {
		try {
			strText = MimeUtility.encodeText(new String(strText.getBytes(), "GB2312"), "GB2312", "B");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strText;
	}

	/**
	 * <br>
	 * 方法说明：往附件组合中添加附件 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public void attachfile(String fname) {
		file.addElement(fname);
	}

	/**
	 * <br>
	 * 方法说明：发送邮件 <br>
	 * 输入参数： <br>
	 * 返回类型：boolean 成功为true，反之为false
	 */
	public boolean sendMail() {
		// 构造mail session

		/*
		 * Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		 * final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		 * Properties props = System.getProperties();
		 * props.setProperty("mail.smtp.host", host);
		 * if(host.equals("smtp.gmail.com")){
		 * props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		 * props.setProperty("mail.smtp.socketFactory.fallback", "false");
		 * props.setProperty("mail.smtp.port", "465");
		 * props.setProperty("mail.smtp.socketFactory.port", "465"); }
		 * props.put("mail.smtp.auth", "true");
		 * 
		 * Session session = Session.getDefaultInstance(props, new
		 * Authenticator() {
		 * 
		 * public PasswordAuthentication getPasswordAuthentication() { return
		 * new PasswordAuthentication(username, password); } }); try {
		 * //构造MimeMessage 并设定基本的值 MimeMessage msg = new MimeMessage(session);
		 * msg.setFrom(new InternetAddress(from)); InternetAddress[] address =
		 * new InternetAddress[to.length]; for(int i=0; i<to.length; i++)
		 * address[i] = new InternetAddress(to[i]);
		 * msg.setRecipients(Message.RecipientType.TO, address); subject =
		 * transferChinese(subject); msg.setSubject(subject);
		 * 
		 * //构造Multipart Multipart mp = new MimeMultipart("alternative");
		 * 
		 * //向Multipart添加正文 MimeBodyPart mbpContent = new MimeBodyPart();
		 * mbpContent.setText(content); //向MimeMessage添加（Multipart代表正文）
		 * mp.addBodyPart(mbpContent); // //向Multipart添加HTML if(html!=null){
		 * MimeBodyPart mbpHTML = new MimeBodyPart(); mbpHTML.setText(html,
		 * "UTF8", "html"); //向MimeMessage添加（Multipart代表正文）
		 * mp.addBodyPart(mbpHTML); }
		 * 
		 * //向Multipart添加附件 Enumeration<String> efile = file.elements(); while
		 * (efile.hasMoreElements()) {
		 * 
		 * MimeBodyPart mbpFile = new MimeBodyPart(); filename =
		 * efile.nextElement().toString(); FileDataSource fds = new
		 * FileDataSource(filename); mbpFile.setDataHandler(new
		 * DataHandler(fds)); mbpFile.setFileName(fds.getName());
		 * //向MimeMessage添加（Multipart代表附件） mp.addBodyPart(mbpFile);
		 * 
		 * } file.removeAllElements(); //向Multipart添加MimeMessage
		 * msg.setContent(mp); msg.setSentDate(new Date()); //发送邮件
		 * Transport.send(msg);
		 * 
		 * } catch (MessagingException mex) { mex.printStackTrace(); Exception
		 * ex = null; if ((ex = mex.getNextException()) != null) {
		 * ex.printStackTrace(); } return false; }
		 */
		return true;
	}

	/**
	 * <br>
	 * 方法说明：主方法，用于测试 <br>
	 * 输入参数： <br>
	 * 返回类型：
	 */
	public static void main(String[] args) {
		String[] to = { args[4] };
		Mail sendmail = new Mail();
		sendmail.setHost(args[1]); // "s155.eatj.com");
		sendmail.setUserName(args[2]); // "liukehong");
		sendmail.setPassWord(args[3]); // "psammead");
		sendmail.setTo(to); // "luke.yuz.tsai@gmail.com");
		sendmail.setFrom(args[5]); // "cflowadmin@Pass8t.com");
		sendmail.setSubject("你好，这是测试！");
		sendmail.setContent("你好这是一个测试！");
		sendmail.setHTML("<html><body><font size=+2 color=red>你好这是一个测试！</font></body></html>");
		// Mail sendmail = new
		// Mail("dujiang@sricnet.com","du_jiang@sohu.com","smtp.sohu.com","du_jiang","31415926"," 你好","胃，你好吗？");

		// sendmail.attachfile("DND.jar");
		/*
		 * for (int i = 0; i < 5; i++) { sendmail.attachfile("c:\\test.txt");
		 * sendmail.sendMail(); }
		 */

		sendmail.sendMail();
	}
}// end

/*
 * import java.security.*; import java.util.Date; import java.util.Properties;
 * 
 * import javax.mail.Authenticator; import javax.mail.Message; import
 * javax.mail.MessagingException; import javax.mail.PasswordAuthentication;
 * import javax.mail.Session; import javax.mail.Transport; import
 * javax.mail.internet.AddressException; import
 * javax.mail.internet.InternetAddress; import javax.mail.internet.MimeMessage;
 * 
 * Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); final
 * String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory"; Properties props =
 * System.getProperties(); props.setProperty("mail.smtp.host",
 * "smtp.gmail.com"); props.setProperty("mail.smtp.socketFactory.class",
 * SSL_FACTORY); props.setProperty("mail.smtp.socketFactory.fallback", "false");
 * props.setProperty("mail.smtp.port", "465");
 * props.setProperty("mail.smtp.socketFactory.port", "465");
 * props.put("mail.smtp.auth", "true");
 * 
 * Session session = Session.getDefaultInstance(props, new Authenticator(){
 * protected PasswordAuthentication getPasswordAuthentication() { return new
 * PasswordAuthentication(username, password); }}); Message msg = new
 * MimeMessage(session);
 * 
 * // -- Set the FROM and TO fields -- msg.setFrom(new InternetAddress(username
 * + "@gmail.com")); msg.setRecipients(Message.RecipientType.TO,
 * InternetAddress.parse("chrislin0426@gmail.com",false));
 * msg.setSubject("Hello"); msg.setText("How are you"); msg.setSentDate(new
 * Date()); Transport.send(msg);
 * 
 * 
 * // Create an "Alternative" Multipart message Multipart mp = new
 * MimeMultipart("alternative");
 * 
 * String textfile = (String)props.get ("ttapr2004.txtfile"); BodyPart bp1 =
 * getFileBodyPart (textfile, "text/plain"); mp.addBodyPart(bp1); String
 * htmlfile = (String)props.get ("ttapr2004.htmlfile"); BodyPart bp2 =
 * getFileBodyPart (htmlfile, "text/html"); mp.addBodyPart(bp2);
 * msg.setContent(mp); Transport.send(msg);
 */
