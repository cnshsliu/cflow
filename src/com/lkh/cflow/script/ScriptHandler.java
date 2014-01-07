package com.lkh.cflow.script;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class ScriptHandler {
	final static Logger logger = Logger.getLogger(ScriptHandler.class);
	static ExecutorService executor = null;

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		(new ScriptHandler(executor)).work();
	}

	public class TestThread extends Thread {
		public int ser = 0;

		public TestThread(int ser) {
			this.ser = ser;
		}

		public void run() {
			String val = "tt_" + ser;
			JSONObject kvs = new JSONObject();
			kvs.put("finished", val);
			// String src =
			// "if(days>10) data.RETURN=\"long\"; else data.RETURN=\"short\"; var added = days+10; data.days=days; data.added10 = added; ";
			// Object ret = runScriptNode(src, kvs);
			String src = " finished=finished; return finished;";
			Object ret = runScript(src, kvs);
			if (!ret.toString().equals(val)) {
				System.out.println("Found error");
			}
		}
	}

	private void work() {
		for (int i = 0; i < 100000000; i++) {
			new TestThread(i).run();
		}

		this.executor.shutdownNow();

	}

	public ScriptHandler(ExecutorService executor) {
		this.executor = executor;
	}

	public String runScriptNode(String script, JSONObject kvs) {

		String json2 = "//Rhino\nif (typeof JSON !== 'object') { JSON = {}; } (function () { 'use strict'; function f(n) { return n < 10 ? '0' + n : n; } if (typeof Date.prototype.toJSON !== 'function') { Date.prototype.toJSON = function (key) { return isFinite(this.valueOf()) ?  this.getUTCFullYear()     + '-' + f(this.getUTCMonth() + 1) + '-' + f(this.getUTCDate())      + 'T' + f(this.getUTCHours())     + ':' + f(this.getUTCMinutes())   + ':' + f(this.getUTCSeconds())   + 'Z' : null; }; String.prototype.toJSON      = Number.prototype.toJSON  = Boolean.prototype.toJSON = function (key) { return this.valueOf(); }; } var cx = /[\\u0000\\u00ad\\u0600-\\u0604\\u070f\\u17b4\\u17b5\\u200c-\\u200f\\u2028-\\u202f\\u2060-\\u206f\\ufeff\\ufff0-\\uffff]/g, escapable = /[\\\\\\\"\\x00-\\x1f\\x7f-\\x9f\\u00ad\\u0600-\\u0604\\u070f\\u17b4\\u17b5\\u200c-\\u200f\\u2028-\\u202f\\u2060-\\u206f\\ufeff\\ufff0-\\uffff]/g, gap, indent, meta = {    '\\b': '\\\\b', '\\t': '\\\\t', '\\n': '\\\\n', '\\f': '\\\\f', '\\r': '\\\\r', '\"' : '\\\\\"', '\\\\': '\\\\\\\\' }, rep; function quote(string) { escapable.lastIndex = 0; return escapable.test(string) ? '\"' + string.replace(escapable, function (a) { var c = meta[a]; return typeof c === 'string' ? c : '\\\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4); }) + '\"' : '\"' + string + '\"'; } function str(key, holder) { var i,          k,          v,          length, mind = gap, partial, value = holder[key]; if (value && typeof value === 'object' && typeof value.toJSON === 'function') { value = value.toJSON(key); } if (typeof rep === 'function') { value = rep.call(holder, key, value); } switch (typeof value) { case 'string': return quote(value); case 'number': return isFinite(value) ? String(value) : 'null'; case 'boolean': case 'null': return String(value); case 'object': if (!value) { return 'null'; } gap += indent; partial = []; if (Object.prototype.toString.apply(value) === '[object Array]') { length = value.length; for (i = 0; i < length; i += 1) { partial[i] = str(i, value) || 'null'; } v = partial.length === 0 ? '[]' : gap ?  '[\\n' + gap + partial.join(',\\n' + gap) + '\\n' + mind + ']' : '[' + partial.join(',') + ']'; gap = mind; return v; } if (rep && typeof rep === 'object') { length = rep.length; for (i = 0; i < length; i += 1) { k = rep[i]; if (typeof k === 'string') { v = str(k, value); if (v) { partial.push(quote(k) + (gap ? ': ' : ':') + v); } } } } else { for (k in value) { if (Object.prototype.hasOwnProperty.call(value, k)) { v = str(k, value); if (v) { partial.push(quote(k) + (gap ? ': ' : ':') + v); } } } } v = partial.length === 0 ? '{}' : gap ?  '{\\n' + gap + partial.join(',\\n' + gap) + '\\n' + mind + '}' : '{' + partial.join(',') + '}'; gap = mind; return v; } } if (typeof JSON.stringify !== 'function') { JSON.stringify = function (value, replacer, space) { var i; gap = ''; indent = ''; if (typeof space === 'number') { for (i = 0; i < space; i += 1) { indent += ' '; } } else if (typeof space === 'string') { indent = space; } rep = replacer; if (replacer && typeof replacer !== 'function' && (typeof replacer !== 'object' || typeof replacer.length !== 'number')) { throw new Error('JSON.stringify'); } return str('', {'': value}); }; } if (typeof JSON.parse !== 'function') { JSON.parse = function (text, reviver) { var j; function walk(holder, key) { var k, v, value = holder[key]; if (value && typeof value === 'object') { for (k in value) { if (Object.prototype.hasOwnProperty.call(value, k)) { v = walk(value, k); if (v !== undefined) { value[k] = v; } else { delete value[k]; } } } } return reviver.call(holder, key, value); } text = String(text); cx.lastIndex = 0; if (cx.test(text)) { text = text.replace(cx, function (a) { return '\\\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4); }); } if (/^[\\],:{}\\s]*$/ .test(text.replace(/\\\\(?:[\"\\\\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@') .replace(/\"[^\"\\\\\\n\\r]*\"|true|false|null|-?\\d+(?:\\.\\d*)?(?:[eE][+\\-]?\\d+)?/g, ']') .replace(/(?:^|:|,)(?:\\s*\\[)+/g, ''))) { j = eval('(' + text + ')'); return typeof reviver === 'function' ?  walk({'': j}, '') : j; } throw new SyntaxError('JSON.parse'); }; } if (!Object.prototype.toJSONString) { Object.prototype.toJSONString = function (filter) { return JSON.stringify(this, filter); }; Object.prototype.parseJSON = function (filter) { return JSON.parse(this, filter); }; } }()); ";
		String fullScript = json2 + " com=null; function cfoooooooFunction() { var data= new Object(); " + script + " return (data.toJSONString());} cfoooooooFunction();";
		return _runScript(fullScript, kvs);
	}

	public String runScript(String script, JSONObject kvs) {
		String fullScript = "function cfooooooooFunction(){" + script + "}cfooooooooFunction(); ";
		return _runScript(fullScript, kvs);
	}

	public String _runScript(String script, JSONObject kvs) {
		String ret = null;
		Future<String> future = this.executor.submit(new Task(script, kvs));
		try {
			logger.debug("runScript Started ");
			Object obj = future.get(60, TimeUnit.SECONDS);
			ret = obj.toString();
			logger.debug("runScript Finished ");
		} catch (Exception ex) {
			logger.debug("runScript Terminated ");
			ret = "{\"RETURN\":\"onerror\",\"error\":\"Script was terminated\"}";
			ex.printStackTrace();
		} finally {
			// executor.shutdownNow();
		}

		return ret;
	}

	class Task implements Callable<String> {
		private JSONObject kvs;
		private String script;

		public Task(String script, JSONObject kvs) {
			this.script = script;
			this.kvs = kvs;
		}

		@Override
		public String call() throws Exception {
			Object ret = new SandboxEvaluator().eval(script, kvs);
			return ret.toString();
		}
	}

}
