package com.lkh.cflow.script;

import org.json.simple.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ScriptableObject;

public class SandboxEvaluator {

	private class MyContextAction implements ContextAction {
		String script;
		JSONObject kvs;

		public MyContextAction(String script, JSONObject kvs) {
			this.script = script;
			this.kvs = kvs;
		}

		@Override
		public Object run(Context cx) {
			ScriptableObject scope = cx.initStandardObjects();
			if (kvs != null) {
				Object[] keys = kvs.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					ScriptableObject.putProperty(scope, (String) keys[i], kvs.get(keys[i]));
				}
			}
			Object obj = cx.evaluateString(scope, script, "source", 0, null);
			return obj;
		}
	}

	public Object eval(String script, JSONObject kvs) {
		SandboxContextFactory factory = new SandboxContextFactory();
		return factory.call(new MyContextAction(script, kvs));
	}
}
