package com.uge.tcpclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.uge.tcpclient.typeSystem.SocketResources;

public class TCPExecutor implements Callable<String> {

	// private static final String ENDPOINT = "172.24.26.13";

	static Logger log = Logger.getLogger(TCPExecutor.class.getName());

	private String cycle = "0000";
	private Socket skt;
	BufferedReader in;
	BufferedWriter out;
	private int count;

	public TCPExecutor(String cycle, Socket skt, int count, BufferedReader inReader, BufferedWriter outWriter) {
		this.cycle = cycle;
		this.skt = skt;
		this.count = count;
		this.in = inReader;
		this.out = outWriter;
	}

	public TCPExecutor(String cycle, SocketResources socketResorces, int count) {
		this.cycle = cycle;
		this.skt = socketResorces.getSocket();
		this.count = count;
		this.in = socketResorces.getIn();
		this.out = socketResorces.getOut();
	}

	@Override
	public String call() throws InterruptedException, ExecutionException, IOException {
		String resp = null;

		try {
			log.info("Povezan na " + skt.getRemoteSocketAddress());
			long sktStart = Calendar.getInstance().getTimeInMillis();

			Thread.sleep(100);
			log.info("0.1 sec after connection sending IM message\n");
			out.write("IM" + cycle + "\n");
			out.flush();

			long start = Calendar.getInstance().getTimeInMillis();

			skt.setSoTimeout(10000);
			// try {
			log.info("Receiving string... \n");
			resp = in.readLine();
			if (resp != null)
				log.info("Received!\n");
			// TODO do stuff with readme
			// } catch (SocketTimeoutException e) {
			// // did not receive the line. readme is undefined, but the socket
			// // can still be used
			// if (resp == null) {
			// log.info("Timeout reached. Resending IM message...\n");
			// out.write("IM" + cycle + "\n");
			// out.flush();
			// skt.setSoTimeout(10000);
			// try {
			// resp = in.readLine();
			// if (resp != null)
			// log.info("Received!\n");
			// } catch (SocketTimeoutException e1) {
			// log.info("2nd try for sending IM not successful!\n");
			// closeSocket(skt, in, out);
			// throw e1;
			// }
			// }
			// }

			// log.info(resp); // Read one line and output it on screen

			// log.info("Received!\n");
			log.info("Vreme potrebno za prijem paketa: " + (Calendar.getInstance().getTimeInMillis() - start) + "ms");

			// closeStream(in, out);
			log.info("Vreme trajanja konekcije: " + (Calendar.getInstance().getTimeInMillis() - sktStart) + "ms");
		} catch (UnknownHostException e) {
			// closeStream(in, out);
			throw e;
		} catch (InterruptedException e) {
			// closeStream(in, out);
			throw e;
		} catch (SocketException e) {
			// closeStream(in, out);
			throw e;
		} catch (IOException e) {
			// closeStream(in, out);
			throw e;
		} catch (Exception e) {
			// closeStream(in, out);
			throw new ExecutionException(e);
		}

		writeToFile(this.skt.getRemoteSocketAddress().toString(), cycle, resp);

		return resp;

	}

	private void writeToFile(String endPoint, String cycle, String resp) {
		if (resp == null || resp.length() < 10)
			return;

		try {
			BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File("data-" + endPoint + ".txt"), true)));

			out.write("\n" + cycle + ": " + resp + "\n");
			out.write("ciklusi su vraceni u red cekanja ukupno: " + this.count + " puta.\n");
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
