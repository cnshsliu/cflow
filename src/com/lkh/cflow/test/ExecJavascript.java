package com.lkh.cflow.test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ExecJavascript {
	private static final int TIMEOUT_SEC = 5;

	public static void main(final String... args) throws Exception {

		// final String script = "var out = java.lang.System.out;\n" +
		// "out.println( 'JS: Before infinite loop...' );\n" +
		// "while( false ) {}\n" +
		// "out.println( 'JS: After infinite loop...' );\n";
		String script = "function cfoooooooFunction(){var out = java.lang.System.out;\n" + "out.println( 'JS: Before infinite loop...' );\n" + "while( true ) {}\n" + "out.println( 'JS: After infinite loop...' );\n return 1; }";

		ExecJavascript ej = new ExecJavascript();
		Object ret = ej.execWithFuture(script);
		System.out.println(ret);
	}

	private class myRunnable implements Runnable {
		private String script;

		public myRunnable(String script) {
			this.script = script;
		}

		public void run() {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			try {
				engine.eval(script);
				Invocable inv = (Invocable) engine;
				Object tmp = (inv.invokeFunction("cfoooooooFunction"));
				System.out.println(tmp);

			} catch (Exception e) {
				System.out.println("Java: Caught exception from eval(): " + e.getMessage());
			}
		}

	}

	private Object execWithThread(String script) {
		Object ret = null;
		myRunnable r = new myRunnable(script);
		System.out.println("Java: Starting thread...");
		final Thread t = new Thread(r);
		t.start();
		System.out.println("Java: ...thread started");
		try {
			Thread.currentThread().sleep(TIMEOUT_SEC * 1000);
			if (t.isAlive()) {
				System.out.println("Java: Thread alive after timeout, stopping...");
				t.stop();
				System.out.println("Java: ...thread stopped");
				return "Done";
			} else {
				System.out.println("Java: Thread not alive after timeout.");
				return "Done";
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted while waiting for timeout to elapse.");
			return null;
		}

	}

	private class myCallable implements Callable<Object> {
		private String script;

		public myCallable(String script) {
			this.script = script;
		}

		public Object call() throws Exception {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			engine.eval(script);

			Invocable inv = (Invocable) engine;
			try {
				Object ret = (inv.invokeFunction("cfoooooooFunction"));
				return ret;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}

		}

	}

	private Object execWithFuture(String script) throws Exception {

		myCallable c = new myCallable(script);
		System.out.println("Java: Submitting script eval to thread pool...");
		ExecutorService pool = Executors.newCachedThreadPool();
		final Future<Object> f = pool.submit(c);
		System.out.println("Java: ...submitted.");
		try {
			final Object result = f.get(TIMEOUT_SEC, TimeUnit.SECONDS);
			System.out.println(result);
			return result;
		} catch (InterruptedException e) {
			System.out.println("Java: Interrupted while waiting for script...");
			return null;
		} catch (ExecutionException e) {
			System.out.println("Java: Script threw exception: " + e.getMessage());
			return null;
		} catch (TimeoutException e) {
			System.out.println("Java: Timeout! trying to future.cancel()...");
			f.cancel(true);
			System.out.println("Java: ...future.cancel() executed");
			return null;
		} finally {
			pool.shutdownNow();
		}
	}

}