package com.uge.tcpclient;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class TCPClient {

	// String endPoint = "172.24.26.13";

	static Logger log = Logger.getLogger(TCPClient.class.getName());
	private static TCPPeriodicTask periodicTask;
	private static ScheduledExecutorService scheduledExecutor;

	public static void main(String[] args) {
		periodicTask = new TCPPeriodicTask(Arrays.asList(new String[] { "localhost" }));
		Timer timer = new Timer();
		timer.schedule(periodicTask, 0, 60 * 1000);

		TCPQueueHolder queueHolder = new TCPQueueHolder();
		queueHolder.start();

		// listen to console input for stop
		Scanner sc = new Scanner(System.in);
		String line = sc.nextLine();
		while (!line.equalsIgnoreCase("stop")) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			line = sc.nextLine();
		}

		// stop all
		periodicTask.interrupt();
		queueHolder.interrupt();
		scheduledExecutor.shutdown();
		sc.close();
	}
}
