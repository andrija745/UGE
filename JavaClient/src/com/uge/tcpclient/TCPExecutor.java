package com.uge.tcpclient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPExecutor implements Callable<String> {

	// private static final String ENDPOINT = "172.24.26.13";
	private static final String ENDPOINT = "localhost";

	static Logger log = Logger.getLogger(TCPExecutor.class.getName());

	private String cycle = "0000";

	public TCPExecutor(String cycle) {
		this.cycle = cycle;
	}

	@Override
	public String call() throws Exception {

		try {
			Socket skt = new Socket(ENDPOINT, 1234);

			log.info("Povezan na " + skt.getRemoteSocketAddress());

			BufferedReader in = new BufferedReader(new InputStreamReader(
					skt.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					skt.getOutputStream()));

			// Thread.sleep(2000);
			// log.info("Proslo 2 sekundi, prosledjujem IM poruku\n");
			out.write("IM" + cycle + "\n");
			// out.close();
			out.flush();

			long start = Calendar.getInstance().getTimeInMillis();
			log.info("Received string: \n");
			String resp = in.readLine();
			log.info(resp); // Read one line and output it on screen
			writeToFile(resp);

			log.info("\nPrimljeno\n");
			log.info("Vreme potrebno za prijem paketa: "
					+ (Calendar.getInstance().getTimeInMillis() - start) + "ms");
			in.close();
			skt.close();

		} catch (Exception e) {
			log.log(Level.SEVERE, "Connection Failed", e);
		}

		return "OK";
	}

	private void writeToFile(String resp) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File("data.txt"), true)));

			out.write(resp + "\n");
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
