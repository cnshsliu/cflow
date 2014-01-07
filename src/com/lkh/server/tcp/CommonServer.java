package com.lkh.server.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.lkh.cflow.ha.Elector;
import com.lkh.cflow.tool.MyJSON;

public class CommonServer implements Runnable {
	private final ServerSocket serverSocket;
	private final ExecutorService pool;
	private String processorClassName = null;
	private JSONArray masterInfo = null;
	private TCPConnection lastMasterConn = null;
	private static boolean lastMasterIsReachable = false;
	final static Logger logger = Logger.getLogger(CommonServer.class);

	/**
	 * Start a Server under the management of Master servers If master servers
	 * are presented, the Server will send heartbeat to those masters
	 * 
	 * @param hostOrIP
	 *            , the unique host name or IP of this server
	 * @param port
	 *            , the port of this server
	 * @param processorClassName
	 *            , the client command processor class name
	 * @param masters
	 *            , the masters
	 * @throws Exception
	 */
	public CommonServer(String hostOrIP, int port, String processorClassName, JSONArray masters) throws Exception {
		serverSocket = new ServerSocket(port);
		pool = Executors.newCachedThreadPool();
		this.processorClassName = processorClassName;
		// Try to register to maseter
		if (masters != null) {
			masterInfo = masters;
			new Thread(new HeartBeatFromServerToMaster(hostOrIP, port)).start();
		}
	}

	/**
	 * Start a Server without master management
	 * 
	 * @param hostOrIP
	 *            , the unique host name or IP of this server
	 * @param port
	 *            , the port of this server
	 * @param processorClassName
	 *            , the client command processor class name
	 * @throws Exception
	 */
	public CommonServer(String hostOrIP, int port, String processorClassName) throws Exception {
		serverSocket = new ServerSocket(port);
		pool = Executors.newCachedThreadPool();
		this.processorClassName = processorClassName;
		// Try to register to maseter
	}

	private class HeartBeatFromServerToMaster implements Runnable {
		String myHost = null;
		int myPort = -1;

		public HeartBeatFromServerToMaster(String host, int port) {
			this.myHost = host;
			this.myPort = port;
		}

		@Override
		public void run() {
			int tmpb = 0;
			for (;;) {
				try {
					if (tmpb >= 10) {
						logger.debug("10 hearbeats sent.");
						tmpb = 0;
					}
					TCPConnection masterConn = getMasterConnection();
					masterConn.sendCommand("HBEAT " + myHost + " " + myPort);
					masterConn.readLine();
					lastMasterIsReachable = true;
					tmpb++;
				} catch (Exception e) {
					lastMasterIsReachable = false;
					// e.printStackTrace();
				}
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private TCPConnection getMasterConnection() throws Exception {
		String masterServer = null;
		int masterPort = -1;

		boolean needToChooseMaster = false;
		if (lastMasterConn != null && lastMasterIsReachable == true) {
			return lastMasterConn;
		}

		int elected = new Elector().elect(masterInfo.size());
		MyJSON master = new MyJSON((JSONObject) masterInfo.get(elected));
		try {
			masterServer = master.getString("host");
			masterPort = master.getInt("port");
			lastMasterConn = new TCPConnection(masterServer, masterPort);
		} catch (Exception e) {
			lastMasterConn = null;
		}

		if (lastMasterConn == null) {
			for (int i = 0; i < masterInfo.size(); i++) {
				if (i == elected)
					continue;
				master = new MyJSON((JSONObject) masterInfo.get(i));
				try {
					masterServer = master.getString("host");
					masterPort = master.getInt("port");
					lastMasterConn = new TCPConnection(masterServer, masterPort);
				} catch (Exception e) {
					// e.printStackTrace();
				}
				if (lastMasterConn != null) {
					break;
				}
			}
		}

		if (masterServer == null || masterPort < 0 || lastMasterConn == null) {
			throw new Exception("Connect to master server failed.");
		} else {
			logger.info("Found master " + masterServer + ":" + masterPort);
		}

		return lastMasterConn;
	}

	public void run() { // run the service
		try {
			for (;;) {
				pool.execute(new CommonServerHandler(serverSocket.accept(), processorClassName));
			}
		} catch (IOException ex) {
			pool.shutdown();
		}
	}
}

class CommonServerHandler implements Runnable {
	private final Socket socket;
	String clientSentence;
	ReturnValue returnValue;
	private Processor processor;
	public final static int soTimeoutDuration = 0;

	CommonServerHandler(Socket socket, String processorClassName) {
		this.socket = socket;
		try {
			this.socket.setSoTimeout(soTimeoutDuration); // set to 5 seconds
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		try {
			this.processor = (Processor) Class.forName(processorClassName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void writeToStream(DataOutputStream dostream, String str) {
		try {
			dostream.writeBytes(str);
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public void run() {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			while (true) {
				try {
					clientSentence = inFromClient.readLine();
				} catch (java.net.SocketException soEx) {
					writeToStream(outToClient, "ERROR socket read exception\r\n");
					break;
				}
				if (clientSentence == null) {
					writeToStream(outToClient, "ERROR read a null from socket\r\n");
					break;
				}
				if (clientSentence.equalsIgnoreCase("AREYOUOK?")) {
					writeToStream(outToClient, "FTAY?");
					break;
				}
				if (clientSentence.indexOf('\r') >= 0 || clientSentence.indexOf('\n') >= 0) {
					writeToStream(outToClient, "ERROR Command should not have new line character\r\n");
					break;
				}
				if (clientSentence.equalsIgnoreCase("EXIT")) {
					break;
				}
				returnValue = processor.process(clientSentence);
				if (returnValue.serverToClient == null) {
					break;
				} else if (returnValue.serverToClient instanceof java.lang.String) {
					writeToStream(outToClient, returnValue.serverToClient + "\r\n");
				} else if (returnValue.serverToClient instanceof java.lang.String[]) {
					String[] retStrings = (String[]) returnValue.serverToClient;
					String lines = "" + retStrings.length;
					writeToStream(outToClient, lines + "\r\n");
					for (int i = 0; i < retStrings.length; i++) {
						writeToStream(outToClient, retStrings[i] + "\r\n");
					}
				}
				if (returnValue.serverControl.equalsIgnoreCase("EXIT")) {
					break;
				}
				outToClient.flush();
			}
			outToClient.close();
			inFromClient.close();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
