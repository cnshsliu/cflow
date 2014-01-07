package com.lkh.cflow.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang.StringUtils;

@Deprecated
public class JavaGenerator {
	public void generateJava(String wftId, String nodeId, String script) throws IOException {
		String script_folder = "C:\\cflow\\script";
		String ret = null;
		String javaPackageName = "com.lkh.cflow.script.wft" + wftId;
		String javaClassName = getNodeClassname(nodeId);
		String javaFullName = javaPackageName + "." + javaClassName;
		String packageFolder = script_folder + File.separatorChar + StringUtils.replaceChars(javaPackageName, '.', File.separatorChar);
		String javaFilename = packageFolder + File.separatorChar + javaClassName + ".java";
		String jsonJar = script_folder + File.separatorChar + "json_simple-1.1.jar";

		StringBuffer sb = new StringBuffer();
		sb.append("package ").append(javaPackageName).append(";\n");
		sb.append("import java.util.HashMap;\n");
		sb.append("import java.util.Map;\n");

		sb.append("import javax.script.Invocable;\n");
		sb.append("import javax.script.ScriptEngine;\n");
		sb.append("import javax.script.ScriptEngineManager;\n");
		sb.append("import javax.script.ScriptException;\n");
		sb.append("import org.json.simple.JSONArray;\n");
		sb.append("import org.json.simple.JSONObject;\n");
		sb.append("import org.json.simple.parser.JSONParser;\n");
		sb.append("import java.security.AccessControlContext;\n");
		sb.append("import java.security.AccessController;\n");
		sb.append("import java.security.CodeSource;\n");
		sb.append("import java.security.Permission;\n");
		sb.append("import java.security.Permissions;\n");
		sb.append("import java.security.PrivilegedAction;\n");
		sb.append("import java.security.ProtectionDomain;\n");
		sb.append("import java.security.cert.Certificate;\n");
		sb.append("import java.util.Collection;\n");
		sb.append("import java.io.PrintStream;\n");

		sb.append("public class ").append(javaClassName).append("{\n");
		sb.append("AccessControlContext _accessControlContext;\n");
		sb.append("	public static void main(String[] args){\n");
		sb.append("		try{\n");
		sb.append("			JSONParser parser = new JSONParser();\n");
		sb.append("			JSONObject ctx = (JSONObject)parser.parse(args[0]);\n");
		sb.append("			").append(javaClassName).append(" me=new ").append(javaClassName).append("();\n");
		sb.append("			Object ret = me.runScript(ctx);\n");
		sb.append("			PrintStream out = new PrintStream(System.out, true, \"UTF-8\");\n");
		sb.append("			out.println(ret);\n");
		sb.append("		}catch(Exception ex){\n");
		sb.append("			System.out.println(\"ERROR: \" + ex.getLocalizedMessage());\n");
		sb.append("		}\n");
		sb.append("\t}\n");
		sb.append("public void setPermissions(Collection<Permission> permissionCollection) {\n");
		sb.append("	Permissions perms = new Permissions();\n");
		sb.append("	perms.add(new RuntimePermission(\"accessDeclaredMembers\"));\n");
		sb.append("	if (permissionCollection != null) {\n");
		sb.append("		for (Permission p : permissionCollection) {\n");
		sb.append("			perms.add(p);\n");
		sb.append("			}\n");
		sb.append("		}\n");
		sb.append("		ProtectionDomain domain = new ProtectionDomain(new CodeSource(null, (Certificate[]) null), perms);\n");
		sb.append("	_accessControlContext = new AccessControlContext(new ProtectionDomain[] { domain });\n");
		sb.append("	}\n");

		sb.append("\tpublic Object runScript(JSONObject kvs) throws ScriptException, NoSuchMethodException {\n");
		sb.append("			setPermissions(null);\n");
		sb.append("\tString json2 = \"if (typeof JSON !== 'object') { JSON = {}; } (function () { 'use strict'; function f(n) { return n < 10 ? '0' + n : n; } if (typeof Date.prototype.toJSON !== 'function') { Date.prototype.toJSON = function (key) { return isFinite(this.valueOf()) ?  this.getUTCFullYear()     + '-' + f(this.getUTCMonth() + 1) + '-' + f(this.getUTCDate())      + 'T' + f(this.getUTCHours())     + ':' + f(this.getUTCMinutes())   + ':' + f(this.getUTCSeconds())   + 'Z' : null; }; String.prototype.toJSON      = Number.prototype.toJSON  = Boolean.prototype.toJSON = function (key) { return this.valueOf(); }; } var cx = /[\\\\u0000\\\\u00ad\\\\u0600-\\\\u0604\\\\u070f\\\\u17b4\\\\u17b5\\\\u200c-\\\\u200f\\\\u2028-\\\\u202f\\\\u2060-\\\\u206f\\\\ufeff\\\\ufff0-\\\\uffff]/g, escapable = /[\\\\\\\\\\\\\\\"\\\\x00-\\\\x1f\\\\x7f-\\\\x9f\\\\u00ad\\\\u0600-\\\\u0604\\\\u070f\\\\u17b4\\\\u17b5\\\\u200c-\\\\u200f\\\\u2028-\\\\u202f\\\\u2060-\\\\u206f\\\\ufeff\\\\ufff0-\\\\uffff]/g, gap, indent, meta = {    '\\\\b': '\\\\\\\\b', '\\\\t': '\\\\\\\\t', '\\\\n': '\\\\\\\\n', '\\\\f': '\\\\\\\\f', '\\\\r': '\\\\\\\\r', '\\\"' : '\\\\\\\\\\\"', '\\\\\\\\': '\\\\\\\\\\\\\\\\' }, rep; function quote(string) { escapable.lastIndex = 0; return escapable.test(string) ? '\\\"' + string.replace(escapable, function (a) { var c = meta[a]; return typeof c === 'string' ? c : '\\\\\\\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4); }) + '\\\"' : '\\\"' + string + '\\\"'; } function str(key, holder) { var i,          k,          v,          length, mind = gap, partial, value = holder[key]; if (value && typeof value === 'object' && typeof value.toJSON === 'function') { value = value.toJSON(key); } if (typeof rep === 'function') { value = rep.call(holder, key, value); } switch (typeof value) { case 'string': return quote(value); case 'number': return isFinite(value) ? String(value) : 'null'; case 'boolean': case 'null': return String(value); case 'object': if (!value) { return 'null'; } gap += indent; partial = []; if (Object.prototype.toString.apply(value) === '[object Array]') { length = value.length; for (i = 0; i < length; i += 1) { partial[i] = str(i, value) || 'null'; } v = partial.length === 0 ? '[]' : gap ?  '[\\\\n' + gap + partial.join(',\\\\n' + gap) + '\\\\n' + mind + ']' : '[' + partial.join(',') + ']'; gap = mind; return v; } if (rep && typeof rep === 'object') { length = rep.length; for (i = 0; i < length; i += 1) { k = rep[i]; if (typeof k === 'string') { v = str(k, value); if (v) { partial.push(quote(k) + (gap ? ': ' : ':') + v); } } } } else { for (k in value) { if (Object.prototype.hasOwnProperty.call(value, k)) { v = str(k, value); if (v) { partial.push(quote(k) + (gap ? ': ' : ':') + v); } } } } v = partial.length === 0 ? '{}' : gap ?  '{\\\\n' + gap + partial.join(',\\\\n' + gap) + '\\\\n' + mind + '}' : '{' + partial.join(',') + '}'; gap = mind; return v; } } if (typeof JSON.stringify !== 'function') { JSON.stringify = function (value, replacer, space) { var i; gap = ''; indent = ''; if (typeof space === 'number') { for (i = 0; i < space; i += 1) { indent += ' '; } } else if (typeof space === 'string') { indent = space; } rep = replacer; if (replacer && typeof replacer !== 'function' && (typeof replacer !== 'object' || typeof replacer.length !== 'number')) { throw new Error('JSON.stringify'); } return str('', {'': value}); }; } if (typeof JSON.parse !== 'function') { JSON.parse = function (text, reviver) { var j; function walk(holder, key) { var k, v, value = holder[key]; if (value && typeof value === 'object') { for (k in value) { if (Object.prototype.hasOwnProperty.call(value, k)) { v = walk(value, k); if (v !== undefined) { value[k] = v; } else { delete value[k]; } } } } return reviver.call(holder, key, value); } text = String(text); cx.lastIndex = 0; if (cx.test(text)) { text = text.replace(cx, function (a) { return '\\\\\\\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4); }); } if (/^[\\\\],:{}\\\\s]*$/ .test(text.replace(/\\\\\\\\(?:[\\\"\\\\\\\\\\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@') .replace(/\\\"[^\\\"\\\\\\\\\\\\n\\\\r]*\\\"|true|false|null|-?\\\\d+(?:\\\\.\\\\d*)?(?:[eE][+\\\\-]?\\\\d+)?/g, ']') .replace(/(?:^|:|,)(?:\\\\s*\\\\[)+/g, ''))) { j = eval('(' + text + ')'); return typeof reviver === 'function' ?  walk({'': j}, '') : j; } throw new SyntaxError('JSON.parse'); }; } if (!Object.prototype.toJSONString) { Object.prototype.toJSONString = function (filter) { return JSON.stringify(this, filter); }; Object.prototype.parseJSON = function (filter) { return JSON.parse(this, filter); }; } }()); \";\n");
		sb.append("\tScriptEngineManager manager = new ScriptEngineManager();\n");

		sb.append("\tScriptEngine engine = manager.getEngineByName(\"JavaScript\");\n");
		sb.append("\tObject[] keys = kvs.keySet().toArray();\n");
		sb.append("\tfor (int i = 0; i < keys.length; i++) {\n");
		sb.append("\t\tengine.put((String) keys[i], kvs.get(keys[i]));\n");
		sb.append("\t}\n");
		sb.append("\tString fullScript = json2 + \"function cfoooooooFunction() { var data= new Object(); ");
		sb.append(myEscape(script));
		sb.append(" return (data.toJSONString());}\";\n");
		sb.append("\tengine.eval(fullScript);\n");
		sb.append("\tInvocable inv = (Invocable) engine;\n");
		sb.append("\ttry {\n");
		sb.append("\tObject ret = (inv.invokeFunction(\"cfoooooooFunction\"));\n");
		sb.append("\t\treturn ret;\n");
		sb.append("\t} catch (Exception ex) {\n");
		sb.append("\t\tex.printStackTrace();\n");
		sb.append("\t\treturn null;\n");
		sb.append("\t}\n");
		sb.append("}\n");

		sb.append("}\n");

		File folder = new File(packageFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		FileWriter fw;
		fw = new FileWriter(javaFilename);
		fw.write(sb.toString());
		fw.close();

		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		// 4:获取一个文件管理器StandardJavaFileManage
		StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(null, null, null);
		// 5.文件管理器根与文件连接起来
		Iterable it = javaFileManager.getJavaFileObjects(new File(javaFilename));
		// 6.创建编译的任务
		List<String> optionList = new ArrayList<String>();
		optionList.addAll(Arrays.asList("-classpath", jsonJar, "-d", script_folder));
		CompilationTask task = javaCompiler.getTask(null, javaFileManager, null, optionList, null, it);
		// 执行编译
		task.call();
		javaFileManager.close();
	}

	private String getNodeClassname(String nodeId) {
		return "Script" + StringUtils.remove(nodeId, '-');
	}

	private static String myEscape(String str) {
		str = StringUtils.replaceEach(str, new String[] { "\\", "\"", "\n", "\r", "\'" }, new String[] { "\\\\", "\\\"", "\\n", "\\r", "\\\'" });

		return str;
	}
}
