package com.lkh.cflow.tool;

public class StringHeader {
	private final static char SEPCHAR = ' ';
	public String first;
	public String last;

	public StringHeader(String str) {
		if (str.indexOf(SEPCHAR) > 0) {
			this.first = str.substring(0, str.indexOf(SEPCHAR));
			if (str.indexOf(SEPCHAR) + 1 >= str.length())
				this.last = null;
			else
				this.last = str.substring(str.indexOf(SEPCHAR) + 1);
		} else {
			this.first = str;
			this.last = null;
		}
	}

	public StringHeader parse(String str) {
		return new StringHeader(str);

	}
}