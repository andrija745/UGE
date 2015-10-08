package com.uge.tcpclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class TCPExecutor implements Callable<String> {

	// private static final String ENDPOINT = "172.24.26.13";
	private String endPoint = "172.24.26.13";

	static Logger log = Logger.getLogger(TCPExecutor.class.getName());

	private String cycle = "0000";

	public TCPExecutor(String cycle, String endPoint) {
		this.cycle = cycle;
		this.endPoint = endPoint;
	}

	@Override
	public String call() throws InterruptedException, ExecutionException, IOException {
		String resp = null;
		Socket skt = null;
		BufferedReader in = null;
		;
		BufferedWriter out = null;

		try {
			skt = new Socket(endPoint, 1234);
			log.info("Povezan na " + skt.getRemoteSocketAddress());
			long sktStart = Calendar.getInstance().getTimeInMillis();

			in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));

			Thread.sleep(4000);
			log.info("4 sec after connection sending IM message\n");
			out.write("IM" + cycle + "\n");
			out.flush();

			long start = Calendar.getInstance().getTimeInMillis();

			skt.setSoTimeout(10000);
			try {
				// log.info("Received string: \n");
				resp = in.readLine();
				if (resp != null)
					log.info("Received!\n");
				// TODO do stuff with readme
			} catch (SocketTimeoutException e) {
				// did not receive the line. readme is undefined, but the socket
				// can still be used
				if (resp == null) {
					log.info("Timeout reached. Resending IM message...\n");
					out.write("IM" + cycle + "\n");
					out.flush();
					skt.setSoTimeout(10000);
					try {
						resp = in.readLine();
						if (resp != null)
							log.info("Received!\n");
					} catch (SocketTimeoutException e1) {
						log.info("2nd try for sending IM not successful!\n");
						closeSocket(skt, in, out);
						throw e1;
					}
				}
			}

			// log.info(resp); // Read one line and output it on screen

			// log.info("Received!\n");
			log.info("Vreme potrebno za prijem paketa: " + (Calendar.getInstance().getTimeInMillis() - start) + "ms");
			out.close();
			in.close();
			skt.close();
			log.info("Vreme trajanja konekcije: " + (Calendar.getInstance().getTimeInMillis() - sktStart) + "ms");
		} catch (UnknownHostException e) {
			closeSocket(skt, in, out);
			throw e;
		} catch (InterruptedException e) {
			closeSocket(skt, in, out);
			throw e;
		} catch (SocketException e) {
			closeSocket(skt, in, out);
			throw e;
		} catch (IOException e) {
			closeSocket(skt, in, out);
			throw e;
		} catch (Exception e) {
			closeSocket(skt, in, out);
			throw new ExecutionException(e);
		}
		return resp;
	}

	private void closeSocket(Socket skt, BufferedReader in, BufferedWriter out) throws IOException {
		if (in != null)
			in.close();
		if (out != null)
			out.close();
		if (skt != null)
			skt.close();
	}

}
