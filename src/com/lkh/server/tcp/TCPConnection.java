package com.lkh.server.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnection {
	public Socket socket;

	public DataOutputStream toServer;
	public BufferedReader fromServer;
	private InputStream inps = null;

	public TCPConnection(String host, int port) throws UnknownHostException, IOException {
		this.socket = new Socket(host, port);
		this.toServer = new DataOutputStream(socket.getOutputStream());
		inps = socket.getInputStream();
		this.fromServer = new BufferedReader(new InputStreamReader(inps));
	}

	public TCPConnection(String host, int port, int timeout) throws UnknownHostException, IOException {
		this.socket = new Socket();
		this.socket.connect(new InetSocketAddress(host, port), timeout);
		this.toServer = new DataOutputStream(socket.getOutputStream());
		inps = socket.getInputStream();
		this.fromServer = new BufferedReader(new InputStreamReader(inps));
	}

	public void sendCommand(String commandLine) throws IOException {
		readAll();
		this.toServer.writeBytes(commandLine + "\r\n");
	}

	public String readLine() throws IOException {
		return fromServer.readLine();
	}

	public void readAll() {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inps);
		boolean notDone = true;
		byte[] buf = new byte[1024];
		while (notDone) {
			try {
				if (bufferedInputStream.available() > 0) {
					bufferedInputStream.read(buf);
				} else {
					notDone = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (this.socket != null) {
			try {
				sendCommand("EXIT");
			} catch (Exception logOrIgnore) {
			}
			try {
				this.socket.close();
			} catch (IOException logOrIgnore) {
			}
		}
	}
}