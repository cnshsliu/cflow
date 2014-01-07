package com.lkh.cflow.exception;

import java.lang.Exception;

import com.lkh.cflow.Context;

public class CflowException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -541360491129966166L;
	private Context context = null;

	public CflowException(String message){
		super(message);
	}
	public void setContext(Context obj){
		context= obj;
	}

	public Context getContext(){
		return context;
	}
}
