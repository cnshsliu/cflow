package com.lkh.cflow.script;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Deprecated
public class ScriptMonitor {
	public static JdbcConnectionPool memCpool = null;
	public static String script_folder = null;
	ExecutorService executor = null;
	String ret = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScriptMonitor sm = new ScriptMonitor();
		try {
			if (args.length < 2)
				throw new Exception("Where is the script classpath?");
			for (int i = 0; i < args.length - 1; i++) {
				if (args[i].equalsIgnoreCase("-cp"))
					script_folder = args[i + 1];
			}
			sm.monitor(script_folder);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void monitor(String script_folder) throws Exception {
		this.script_folder = script_folder;
		String jsonJar = script_folder + File.separatorChar + "json_simple-1.1.jar";
		addFile(jsonJar);

		executor = Executors.newCachedThreadPool();

		memCpool = JdbcConnectionPool.create("jdbc:h2:tcp://localhost:8092/mem:cflow;MODE=MySQL", "cflow", "cflow");
		Connection conn = memCpool.getConnection();
		String updateSql = "UPDATE cf_script SET status = ?, result=?, completedat = CURRENT_TIMESTAMP where TICKET=?";
		PreparedStatement ps = null;
		String sql = "SELECT * FROM cf_script where status = 0";
		Statement stat = conn.createStatement();
		for (;;) {
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				String ticket = rs.getString("TICKET");
				String wftId = rs.getString("WFTID");
				String nodeId = rs.getString("NODEID");
				int status = rs.getInt("STATUS");
				InputStream kvsIS = rs.getBinaryStream("KVS");
				String kvs = IOUtils.toString(kvsIS);

				String ret = null;
				try {
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(kvs);
					ret = runScript(wftId, nodeId, obj);
				} catch (Exception ex) {
					ex.printStackTrace();
					ret = "{\"RETURN\":\"onerror\",\"error\":\"错误：" + ex.getLocalizedMessage() + "\"}";
				}

				ps = conn.prepareStatement(updateSql);
				ps.setInt(1, 1);
				byte[] b = ret.getBytes("UTF-8");
				ByteArrayInputStream input1 = new ByteArrayInputStream(b);
				ps.setBlob(2, input1, b.length);
				ps.setString(3, ticket);
				ps.executeUpdate();
				input1.close();
				ps.close();
			}
			rs.close();
			Thread.currentThread().sleep(10L);
		}
		// stat.close();
	}

	class Task implements Callable<String> {
		private String wftId;
		private String nodeId;
		private JSONObject kvs;

		public Task(String wftId, String nodeId, JSONObject kvs) {
			this.wftId = wftId;
			this.nodeId = nodeId;
			this.kvs = kvs;
		}

		@Override
		public String call() throws Exception {
			String javaPackageName = "com.lkh.cflow.script.wft" + wftId;
			String javaClassName = getNodeClassname(nodeId);
			final String javaFullName = javaPackageName + "." + javaClassName;
			String packageFolder = script_folder + File.separatorChar + StringUtils.replaceChars(javaPackageName, '.', File.separatorChar);
			String javaFilename = packageFolder + File.separatorChar + javaClassName + ".java";

			HotswapCL cl = new HotswapCL(script_folder, new String[] { javaFullName });
			Class theClass = cl.loadClass(javaFullName);
			Class[] args1 = new Class[1];
			args1[0] = JSONObject.class;
			Method theMethod = theClass.getMethod("runScript", args1);
			Object obj = theClass.newInstance();
			Object[] args2 = new Object[1];
			args2[0] = kvs;
			Object retObj = theMethod.invoke(obj, args2);
			return retObj.toString();
		}
	}

	public String runScript(String wftId, String nodeId, JSONObject kvs) {
		String javaPackageName = "com.lkh.cflow.script.wft" + wftId;
		String javaClassName = getNodeClassname(nodeId);
		final String javaFullName = javaPackageName + "." + javaClassName;
		String packageFolder = script_folder + File.separatorChar + StringUtils.replaceChars(javaPackageName, '.', File.separatorChar);
		String javaFilename = packageFolder + File.separatorChar + javaClassName + ".java";

		Future<String> future = executor.submit(new Task(wftId, nodeId, kvs));
		try {
			System.out.println("Started..");
			String ret = future.get(5, TimeUnit.SECONDS);
			System.out.println("Finished!");
			return ret;
		} catch (Exception ex) {
			System.out.println("Terminated!");
			System.out.println(ex.getLocalizedMessage());
			return "{\"RETURN\":\"onerror\",\"error\":\"Terminated script in " + nodeId + "\"}";
		} finally {
			// executor.shutdownNow();
		}
	}

	private String getNodeClassname(String nodeId) {
		return "Script" + StringUtils.remove(nodeId, '-');
	}

	private static String myEscape(String str) {
		str = StringUtils.replaceEach(str, new String[] { "\\", "\"", "\n", "\r", "\'" }, new String[] { "\\\\", "\\\"", "\\n", "\\r", "\\\'" });

		return str;
	}

	private static final Class[] parameters = new Class[] { URL.class };

	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}// end method

	public static void addFile(File f) throws IOException {
		addURL(f.toURL());
	}// end method

	public static void addURL(URL u) throws IOException {

		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}// end try catch

	}// end method

	public class HotswapCL extends ClassLoader {
		private String basedir; // 需要该类加载器直接加载的类文件的基目录
		private HashSet dynaclazns; // 需要由该类加载器直接加载的类名

		public HotswapCL(String basedir, String[] clazns) throws IOException {
			super(null); // 指定父类加载器为 null
			this.basedir = basedir;
			dynaclazns = new HashSet();
			loadClassByMe(clazns);
		}

		private void loadClassByMe(String[] clazns) throws IOException {
			for (int i = 0; i < clazns.length; i++) {
				loadDirectly(clazns[i]);
				dynaclazns.add(clazns[i]);
			}
		}

		private Class loadDirectly(String name) throws IOException {
			Class cls = null;
			StringBuffer sb = new StringBuffer(basedir);
			String classname = name.replace('.', File.separatorChar) + ".class";
			sb.append(File.separator + classname);
			File classF = new File(sb.toString());
			cls = instantiateClass(name, new FileInputStream(classF), classF.length());
			return cls;
		}

		private Class instantiateClass(String name, InputStream fin, long len) throws IOException {
			byte[] raw = new byte[(int) len];
			fin.read(raw);
			fin.close();
			return defineClass(name, raw, 0, raw.length);
		}

		protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
			Class cls = null;
			cls = findLoadedClass(name);
			if (!this.dynaclazns.contains(name) && cls == null)
				cls = getSystemClassLoader().loadClass(name);
			if (cls == null)
				throw new ClassNotFoundException(name);
			if (resolve)
				resolveClass(cls);
			return cls;
		}

	}
}
