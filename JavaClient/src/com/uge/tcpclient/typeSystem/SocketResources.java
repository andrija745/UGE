package com.uge.tcpclient.typeSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class SocketResources {

	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;

	public SocketResources(Socket skt, BufferedReader in, BufferedWriter out) {
		this.socket = skt;
		this.in = in;
		this.out = out;
	}

	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getIn() {
		return in;
	}

	public BufferedWriter getOut() {
		return out;
	}

}
