package com.lkh.cflow.web;

import org.apache.commons.lang.StringUtils;

public class Input {
	public String radio(String name, String values) {
		return radio(name, values, null);
	}

	public String radio(String name, String values, String value) {
		String[] tmp = StringUtils.split(values, ',');
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tmp.length; i++) {
			sb.append("<input type='radio' name='").append(name).append("' value='").append(tmp[i]).append("'");
			if (value != null && value.equals(tmp[i])) {
				sb.append(" checked");
			}
			sb.append(">").append(tmp[i]);
		}

		return sb.toString();
	}

	public String checkbox(String name, String values) {
		return checkbox(name, values, null);
	}

	public String checkbox(String name, String values, String value) {
		String[] tmp = StringUtils.split(values, ',');
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tmp.length; i++) {
			sb.append("<input type='checkbox' name='").append(name).append("' value='").append(tmp[i]).append("'");
			if (value != null && value.equals(tmp[i])) {
				sb.append(" checked");
			}
			sb.append(">").append(tmp[i]);
		}

		return sb.toString();
	}

	public String select(String name, String values) {
		return select(name, values, null);
	}

	public String select(String name, String values, String value) {
		String[] tmp = StringUtils.split(values, ',');
		StringBuffer sb = new StringBuffer();
		sb.append("<select name='").append(name).append("'>");
		for (int i = 0; i < tmp.length; i++) {
			sb.append("<option value='").append(tmp[i]).append("'");
			if (value != null && value.equals(tmp[i])) {
				sb.append(" selected");
			}
			sb.append(">").append(tmp[i]).append("</option>");
		}
		sb.append("</select>");

		return sb.toString();
	}

	public String input(String name) {
		return input(name, null);
	}

	public String input(String name, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("<input type='text' name='").append(name).append("'");
		if (value != null)
			sb.append(" value='").append(value).append("'>");
		else
			sb.append(">");

		return sb.toString();
	}

	public String text(String name) {
		return text(name, null);
	}

	public String text(String name, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("<textarea name='").append(name).append("'>");
		if (value != null)
			sb.append(value);
		sb.append("</textarea>");

		return sb.toString();
	}

	public String url(String name) {
		return url(name, null);
	}

	public String url(String name, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("<input type='text' name='").append(name).append("'");
		if (value != null)
			sb.append(" value='").append(value).append("'>");
		else
			sb.append(">");
		if (StringUtils.isNotEmpty(value))
			sb.append("<a href='").append(value).append("'><img src='/cflow/images/openurl.png' border='0'></a>");

		return sb.toString();
	}

	public String hidden(String name) {
		return hidden(name, null);
	}

	public String hidden(String name, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("<input type='hidden' name='").append(name).append("'");
		if (value != null)
			sb.append(" value='").append(value).append("'>");
		else
			sb.append(">");

		return sb.toString();
	}

	public String password(String name) {
		return password(name, null);
	}

	public String password(String name, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("<input type='password' name='").append(name).append("'");
		if (value != null)
			sb.append(" value='").append(value).append("'>");
		else
			sb.append(">");

		return sb.toString();
	}

}
