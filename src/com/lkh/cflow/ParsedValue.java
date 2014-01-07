package com.lkh.cflow;

public class ParsedValue {
	public Object value;
	public String error;
	public boolean noProblem;

	public ParsedValue(Object v, String err, boolean p) {
		this.value = v;
		this.error = err;
		this.noProblem = p;
	}
}
