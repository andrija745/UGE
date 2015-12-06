package com.uge.tcpclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.uge.tcpclient.typeSystem.QueueItem;
import com.uge.tcpclient.typeSystem.SocketResources;

public class TCPPeriodicTask extends TimerTask {
	static Logger log = Logger.getLogger(TCPPeriodicTask.class.getName());

	private static boolean run = true;
	private static ExecutorService executor;

	private List<String> endPoints;

	List<SocketResources> sktList = null;

	public TCPPeriodicTask(List<String> ipAddresses) {
		sktList = new ArrayList<SocketResources>();
		executor = Executors.newCachedThreadPool();
		endPoints = ipAddresses;
		for (String endPoint : endPoints) {
			try {
				Socket skt = new Socket(endPoint, 1234);
				BufferedReader in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));
				sktList.add(new SocketResources(skt, in, out));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void run() {
		if (run) {
			// try {
			Calendar cal = Calendar.getInstance();
			String min = String.valueOf(cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE) + 1);
			while (min.length() < 4)
				min = "0" + min;

			for (SocketResources sktRsrc : sktList) {
				Future<String> future = executor
						.submit(new TCPExecutor(min, sktRsrc.getSocket(), 0, sktRsrc.getIn(), sktRsrc.getOut()));
				QueueItem item = new QueueItem(future, min, sktRsrc);

				TCPQueueHolder.executeAndRequeue(future, item);
			}
		}
	}

	public void interrupt() {
		sktList.forEach(s -> {
			if (s != null)
				try {
					s.getIn().close();
					s.getOut().close();
					s.getSocket().close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		});
		run = false;
		executor.shutdownNow();
	}
}
