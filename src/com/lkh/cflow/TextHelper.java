package com.lkh.cflow;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;


public class TextHelper {
	private ResourceBundle rb;
	private String lang;
	private Locale locale;
	
	public TextHelper(String bundleName, String lang){
		Locale locale = getLocale(lang);
		this.locale = locale;
		this.lang = lang;
		rb = getResourceBundle(bundleName, locale);
	}
	public TextHelper(String bundleName, Locale locale){
		 this.locale = locale;
		 this.lang = this.locale.getLanguage() + "-" + this.locale.getCountry();
		 rb = getResourceBundle(bundleName, locale);
	}
	public TextHelper(String bundleName, HttpServletRequest request){
		 String theLang = CflowHelper.userLang(request);
		 this.lang = theLang;
		 this.locale = getLocale(theLang);
		 rb = getResourceBundle(bundleName, locale);
	}
	public String getLangSuffix(){
		String langSuffix = locale.getLanguage() + "_" + locale.getCountry();
		return langSuffix;
	}
	
	/**
	 * 取得TextHelper
	 * 第一，将首先尝试从session中取textHelper, 如果不存在，则生成新的TextHelper. 语言根据cflowHelper.userLang(request)来判定
	 * 如果存在，还需要比较当前的语言是否与原textHelper的语言一致，如果不一致，则生成新的TextHelper.
	 * @param session
	 * @param request
	 * @return
	 */
	public static TextHelper getTextHelper(ServletContext app, HttpServletRequest request){
		TextHelper ret = null;
		if(app != null)
			ret = (TextHelper)app.getAttribute("TEXTHELPER");
		if(ret==null){
			ret = new TextHelper("MessageBundle", request);
			app.setAttribute("TEXTHELPER", ret);
		}else{
			if(!ret.lang.equals(CflowHelper.userLang(request))){
				ret = null;
				ret = new TextHelper("MessageBundle", request);
				app.setAttribute("TEXTHELPER", ret);
			}
		}
		return ret;
	}
	public static TextHelper getTextHelper(ServletContext app, Locale locale){
		TextHelper ret = null;
		if(app != null)
			ret = (TextHelper)app.getAttribute("TEXTHELPER");
		if(ret==null){
			ret = new TextHelper("MessageBundle", locale);
			app.setAttribute("TEXTHELPER", ret);
		}else{
			if(ret.locale != locale){
				ret = new TextHelper("MessageBundle", locale);
				app.setAttribute("TEXTHELPER", ret);
			}
		}
		return ret;
	}
	
	public String getText(String key ){
		return getText(key, null, 0);
	}

	public String getText(String key, int usemode)
	{
		return getText(key, null, usemode);
	}
	
	public String getText(String key, Object[] args){
		return getText(key, args, 0);
	}
	
	public String getText(String key, Object[] args, int usemode){
		try{
			String keyWithUseMode = key + ".um" + usemode;
			String textValue = "";
			if(rb.containsKey(keyWithUseMode))
				textValue = rb.getString(keyWithUseMode);
			else
				textValue = rb.getString(key);
			
			if(args==null){
				return textValue;
			}else{
				MessageFormat formatter = new MessageFormat("");
				formatter.setLocale(locale);
				formatter.applyPattern(textValue);
				String ret= formatter.format(args);
				return ret;
			}
		}catch(java.util.MissingResourceException ex){
			return key;
		}
	}
	

	public static final Locale getLocale(String langStr){
		String[] langa = StringUtils.split(langStr, '-');
		return new Locale(langa[0], langa[1]);
	}

	public static ResourceBundle getResourceBundle(String bundleName, Locale locale){
		ResourceBundle messages = null;

		messages = ResourceBundle.getBundle(bundleName, locale);
		return messages;

	}
	
}
