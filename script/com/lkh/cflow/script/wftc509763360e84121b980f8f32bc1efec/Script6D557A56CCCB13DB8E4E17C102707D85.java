package com.lkh.cflow.script.wftc509763360e84121b980f8f32bc1efec;
import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collection;
import java.io.PrintStream;
public class Script6D557A56CCCB13DB8E4E17C102707D85{
AccessControlContext _accessControlContext;
	public static void main(String[] args){
		try{
			SecurityManager sm = System.getSecurityManager();
			if (sm == null) {
					sm = new SecurityManager();
					System.setSecurityManager(sm);
			}
			JSONParser parser = new JSONParser();
			JSONObject ctx = (JSONObject)parser.parse(args[0]);
			Script6D557A56CCCB13DB8E4E17C102707D85 me=new Script6D557A56CCCB13DB8E4E17C102707D85();
			me.setPermissions(null);
			Object ret = me.runScript(ctx);
			PrintStream out = new PrintStream(System.out, true, "UTF-8");
			out.println(ret);
		}catch(Exception ex){
			System.out.println("ERROR: " + ex.getLocalizedMessage());
		}
	}
public void setPermissions(Collection<Permission> permissionCollection) {
	Permissions perms = new Permissions();
	perms.add(new RuntimePermission("accessDeclaredMembers"));
	if (permissionCollection != null) {
		for (Permission p : permissionCollection) {
			perms.add(p);
			}
		}
		ProtectionDomain domain = new ProtectionDomain(new CodeSource(null, (Certificate[]) null), perms);
	_accessControlContext = new AccessControlContext(new ProtectionDomain[] { domain });
	}
	public Object runScript(JSONObject kvs) throws ScriptException, NoSuchMethodException {
	String json2 = "if (typeof JSON !== 'object') { JSON = {}; } (function () { 'use strict'; function f(n) { return n < 10 ? '0' + n : n; } if (typeof Date.prototype.toJSON !== 'function') { Date.prototype.toJSON = function (key) { return isFinite(this.valueOf()) ?  this.getUTCFullYear()     + '-' + f(this.getUTCMonth() + 1) + '-' + f(this.getUTCDate())      + 'T' + f(this.getUTCHours())     + ':' + f(this.getUTCMinutes())   + ':' + f(this.getUTCSeconds())   + 'Z' : null; }; String.prototype.toJSON      = Number.prototype.toJSON  = Boolean.prototype.toJSON = function (key) { return this.valueOf(); }; } var cx = /[\\u0000\\u00ad\\u0600-\\u0604\\u070f\\u17b4\\u17b5\\u200c-\\u200f\\u2028-\\u202f\\u2060-\\u206f\\ufeff\\ufff0-\\uffff]/g, escapable = /[\\\\\\\"\\x00-\\x1f\\x7f-\\x9f\\u00ad\\u0600-\\u0604\\u070f\\u17b4\\u17b5\\u200c-\\u200f\\u2028-\\u202f\\u2060-\\u206f\\ufeff\\ufff0-\\uffff]/g, gap, indent, meta = {    '\\b': '\\\\b', '\\t': '\\\\t', '\\n': '\\\\n', '\\f': '\\\\f', '\\r': '\\\\r', '\"' : '\\\\\"', '\\\\': '\\\\\\\\' }, rep; function quote(string) { escapable.lastIndex = 0; return escapable.test(string) ? '\"' + string.replace(escapable, function (a) { var c = meta[a]; return typeof c === 'string' ? c : '\\\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4); }) + '\"' : '\"' + string + '\"'; } function str(key, holder) { var i,          k,          v,          length, mind = gap, partial, value = holder[key]; if (value && typeof value === 'object' && typeof value.toJSON === 'function') { value = value.toJSON(key); } if (typeof rep === 'function') { value = rep.call(holder, key, value); } switch (typeof value) { case 'string': return quote(value); case 'number': return isFinite(value) ? String(value) : 'null'; case 'boolean': case 'null': return String(value); case 'object': if (!value) { return 'null'; } gap += indent; partial = []; if (Object.prototype.toString.apply(value) === '[object Array]') { length = value.length; for (i = 0; i < length; i += 1) { partial[i] = str(i, value) || 'null'; } v = partial.length === 0 ? '[]' : gap ?  '[\\n' + gap + partial.join(',\\n' + gap) + '\\n' + mind + ']' : '[' + partial.join(',') + ']'; gap = mind; return v; } if (rep && typeof rep === 'object') { length = rep.length; for (i = 0; i < length; i += 1) { k = rep[i]; if (typeof k === 'string') { v = str(k, value); if (v) { partial.push(quote(k) + (gap ? ': ' : ':') + v); } } } } else { for (k in value) { if (Object.prototype.hasOwnProperty.call(value, k)) { v = str(k, value); if (v) { partial.push(quote(k) + (gap ? ': ' : ':') + v); } } } } v = partial.length === 0 ? '{}' : gap ?  '{\\n' + gap + partial.join(',\\n' + gap) + '\\n' + mind + '}' : '{' + partial.join(',') + '}'; gap = mind; return v; } } if (typeof JSON.stringify !== 'function') { JSON.stringify = function (value, replacer, space) { var i; gap = ''; indent = ''; if (typeof space === 'number') { for (i = 0; i < space; i += 1) { indent += ' '; } } else if (typeof space === 'string') { indent = space; } rep = replacer; if (replacer && typeof replacer !== 'function' && (typeof replacer !== 'object' || typeof replacer.length !== 'number')) { throw new Error('JSON.stringify'); } return str('', {'': value}); }; } if (typeof JSON.parse !== 'function') { JSON.parse = function (text, reviver) { var j; function walk(holder, key) { var k, v, value = holder[key]; if (value && typeof value === 'object') { for (k in value) { if (Object.prototype.hasOwnProperty.call(value, k)) { v = walk(value, k); if (v !== undefined) { value[k] = v; } else { delete value[k]; } } } } return reviver.call(holder, key, value); } text = String(text); cx.lastIndex = 0; if (cx.test(text)) { text = text.replace(cx, function (a) { return '\\\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4); }); } if (/^[\\],:{}\\s]*$/ .test(text.replace(/\\\\(?:[\"\\\\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@') .replace(/\"[^\"\\\\\\n\\r]*\"|true|false|null|-?\\d+(?:\\.\\d*)?(?:[eE][+\\-]?\\d+)?/g, ']') .replace(/(?:^|:|,)(?:\\s*\\[)+/g, ''))) { j = eval('(' + text + ')'); return typeof reviver === 'function' ?  walk({'': j}, '') : j; } throw new SyntaxError('JSON.parse'); }; } if (!Object.prototype.toJSONString) { Object.prototype.toJSONString = function (filter) { return JSON.stringify(this, filter); }; Object.prototype.parseJSON = function (filter) { return JSON.parse(this, filter); }; } }()); ";
	ScriptEngineManager manager = new ScriptEngineManager();
	ScriptEngine engine = manager.getEngineByName("JavaScript");
	Object[] keys = kvs.keySet().toArray();
	for (int i = 0; i < keys.length; i++) {
		engine.put((String) keys[i], kvs.get(keys[i]));
	}
	String fullScript = json2 + "function cfoooooooFunction() { var data= new Object(); if(month == 1){\ndata.usersG1=\"买家1,买家2,买家3\";\ndata.usersG2=\"买家8,买家9,买家10\";\n}else{\ndata.usersG1=\"买家A,买家B,买家C\";\ndata.usersG2=\"买家L,买家M,买家N\";\n}\ndata.RETURN=\"DEFAULT\"; return (data.toJSONString());}";
	engine.eval(fullScript);
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
