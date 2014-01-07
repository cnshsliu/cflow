package com.lkh.cflow.script;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public class SandboxContextFactory extends ContextFactory {

	private static class MyContext extends Context {
		long startTime;
	}

	static {
		ContextFactory.initGlobal(new SandboxContextFactory());
	}

	@Override
	protected Context makeContext() {
		MyContext cx = new MyContext();
		cx.setClassShutter(new ClassShutter() {
			public boolean visibleToScripts(String className) {
				if (className.startsWith("adapterliukehongabcdefg"))
					return true;

				return false;
			}
		});
		cx.setOptimizationLevel(-1);
		cx.setInstructionObserverThreshold(10000);
		cx.setWrapFactory(new SandboxWrapFactory());
		return cx;
	}

	// Override hasFeature(Context, int)
	public boolean hasFeature(Context cx, int featureIndex) {
		// Turn on maximum compatibility with MSIE scripts
		switch (featureIndex) {
		case Context.FEATURE_NON_ECMA_GET_YEAR:
			return true;

		case Context.FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME:
			return true;

		case Context.FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER:
			return true;

		case Context.FEATURE_PARENT_PROTO_PROPERTIES:
			return false;
		}
		return super.hasFeature(cx, featureIndex);
	}

	// Override observeInstructionCount(Context, int)
	protected void observeInstructionCount(Context cx, int instructionCount) {
		MyContext mcx = (MyContext) cx;
		long currentTime = System.currentTimeMillis();
		if (currentTime - mcx.startTime > 60 * 1000) {
			// More then 10 seconds from Context creation time:
			// it is time to stop the script.
			// Throw Error instance to ensure that script will never
			// get control back through catch or finally.
			throw new Error();
		}
	}

	protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		MyContext mcx = (MyContext) cx;
		mcx.startTime = System.currentTimeMillis();

		return super.doTopCall(callable, cx, scope, thisObj, args);
	}
}