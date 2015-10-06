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
	public String call() throws ExecutionException, UnknownHostException, IOException, InterruptedException {
		String resp = null;
		Socket skt = new Socket(endPoint, 1234);

		log.info("Povezan na " + skt.getRemoteSocketAddress());

		BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));

		Thread.sleep(4000);
		log.info("Proslo 4 sekunde, prosledjujem IM poruku\n");
		out.write("IM" + cycle + "\n");
		out.flush();
		

		long start = Calendar.getInstance().getTimeInMillis();
		log.info("Received string: \n");
		// if (in.ready())
		// resp = in.readLine();
		// else {
		Thread.sleep(2000);
		log.info("Proslo jos 2 sekunde, pre citanja odgovora\n");
		// if (in.ready())
		resp = in.readLine();
		// else
		// throw new ExecutionException("Stream not ready for reading",
		// new Exception(endPoint + " " + cycle));
		// }
		log.info(resp); // Read one line and output it on screen
		writeToFile(endPoint, resp);

		log.info("\nPrimljeno\n");
		log.info("Vreme potrebno za prijem paketa: " + (Calendar.getInstance().getTimeInMillis() - start) + "ms");
		in.close();
		out.close();
		skt.close();
		// } catch (IOException e) {
		// log.log(Level.SEVERE, "Connection Failed", e);
		// }

		return resp;
	}

	private void writeToFile(String endPoint, String resp) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("data-"
					+ endPoint + ".txt"), true)));

			out.write("\n" + cycle + ": " + resp + "\n");
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
