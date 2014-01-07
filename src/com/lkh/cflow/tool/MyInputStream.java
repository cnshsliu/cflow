package com.lkh.cflow.tool;

import java.io.InputStream;

public class MyInputStream {
	public InputStream inputStream;
	public Object attachment;

	public MyInputStream(InputStream is, Object att) {
		this.inputStream = is;
		this.attachment = att;
	}
}
