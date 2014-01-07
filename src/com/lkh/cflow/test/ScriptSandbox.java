package com.lkh.cflow.test;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collection;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptSandbox {
	ScriptEngine _scriptEngine;
	AccessControlContext _accessControlContext;

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

	public ScriptSandbox() throws InstantiationException {

		ScriptEngineManager sem = new ScriptEngineManager();
		_scriptEngine = sem.getEngineByName("JavaScript");
		_scriptEngine.put("PBO", "doc_id_000");
		_scriptEngine.put("NUM", 123);
		if (_scriptEngine == null) {
			throw new InstantiationException("Could not load script engine: ");
		}
		setPermissions(null);
	}

	public void setPermissions(Collection<Permission> permissionCollection) {
		Permissions perms = new Permissions();
		perms.add(new RuntimePermission("accessDeclaredMembers"));
		if (permissionCollection != null) {
			for (Permission p : permissionCollection) {
				perms.add(p);
			}
		}
		// Cast to Certificate[] required because of ambiguity:
		ProtectionDomain domain = new ProtectionDomain(new CodeSource(null, (Certificate[]) null), perms);
		_accessControlContext = new AccessControlContext(new ProtectionDomain[] { domain });
	}

	public Object eval(final String code) {
		return AccessController.doPrivileged(new PrivilegedAction() {
			@Override
			public Object run() {
				try {
					_scriptEngine.eval(code);
					Invocable inv = (Invocable) _scriptEngine;
					Object tmp = (inv.invokeFunction("cfoooooooFunction"));
					return tmp;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}, _accessControlContext);
	}

	public static void main(String[] args) {
		try {
			SecurityManager sm = System.getSecurityManager();
			if (sm == null) {
				sm = new SecurityManager();
				System.setSecurityManager(sm);
			}
			System.out.println("Before evaluation.");
			ScriptSandbox sb = new ScriptSandbox();
			Object ret = sb.eval("function cfoooooooFunction(){var out = java.lang.System.out;\n" + "out.println( 'JS: Before infinite loop...' );\n" + "while( false ) {}\n"
					+ "out.println( 'JS: After infinite loop...' );\n  var r = new java.io.FileReader(\"C:\\\\cflow\\\\test.html\"); var ret = NUM+1;\n return ret; }");
			System.out.println(ret);
			System.out.println("after evaluation.");
			java.io.Reader r = new java.io.FileReader("C:\\cflow\\test.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}