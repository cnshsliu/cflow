package com.lkh.cflow.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

class ScriptRunner implements Runnable {

	private String script;

	public ScriptRunner(String script) {
		this.script = script;
	}

	public ScriptRunner() {
		this("importPackage = null; java.lang.System.exit(1);");
	}

	public void run() {
		try {
			// create a script engine manager
			ScriptEngineManager factory = new ScriptEngineManager();
			// create a JavaScript engine
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			// evaluate JavaScript code from String
			System.out.println("running script :'" + script + "'");
			engine.eval(script);
			System.out.println("stopped running script");
		} catch (ScriptException se) {
			System.out.println("caught exception");
			throw new RuntimeException(se);
		}
		System.out.println("exiting run");
	}
}

public class Inter {

	public void run() {
		ExecutorService pool = Executors.newCachedThreadPool();
		try {
			pool.submit(new ScriptRunner()).get(15, TimeUnit.SECONDS);
		} catch (Exception e) {
			pool.shutdownNow();
			throw new RuntimeException(e);
		}
	}

	public void run2() {
		try {
			Thread t = new Thread(new ScriptRunner());
			t.start();
			Thread.sleep(1000);
			System.out.println("interrupting");
			t.interrupt();
			Thread.sleep(5000);
			System.out.println("stopping");
			t.stop();
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}

	public static void main(String[] args) {
		new Inter().run2();
	}
}