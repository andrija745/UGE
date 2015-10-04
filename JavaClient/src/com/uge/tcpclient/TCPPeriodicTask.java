package com.uge.tcpclient;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPPeriodicTask extends TimerTask {
	static Logger log = Logger.getLogger(TCPPeriodicTask.class.getName());

	private static boolean run = true;
	private static ExecutorService executor;
	private static Queue<Future<String>> queue;

	private List<String> endPoints;

	public TCPPeriodicTask(List<String> ipAddresses) {
		queue = new LinkedList<Future<String>>();
		endPoints = ipAddresses;
	}

	@Override
	public void run() {

		if (run) {
			executor = Executors.newFixedThreadPool(endPoints.size());
			// try {
			Calendar cal = Calendar.getInstance();
			String min = (cal.get(Calendar.HOUR_OF_DAY) * 60) + ""
					+ cal.get(Calendar.MINUTE);
			Future<String> future;
			for (String endPoint : endPoints) {
				future = executor.submit(new TCPExecutor(min, endPoint));
				Queue<Future<String>> tmp_queue = new LinkedList<Future<String>>();
				tmp_queue.add(future);
				tmp_queue.addAll(queue);
				queue.clear();
				queue.addAll(tmp_queue);
			}

			log.info("Started..");
			int i = 0;
			int size = queue.size();
			while (i < size) {
				i++;
				future = queue.poll();
				try {
					log.info(future.get(20, TimeUnit.SECONDS));
				} catch (TimeoutException e) {
					future.cancel(true);
					queue.add(future);
					log.log(Level.SEVERE, "Terminated - Time Out!", e);
				} catch (InterruptedException e) {
					future.cancel(true);
					queue.add(future);
					log.log(Level.SEVERE, "Terminated - Interupt!", e);
				} catch (ExecutionException e) {
					future.cancel(true);
					queue.add(future);
					log.log(Level.SEVERE, "Terminated - Execution!", e);
				} catch (Exception e) {
					future.cancel(true);
					queue.add(future);
					log.log(Level.SEVERE, "Terminated!", e);
				}
			}
			log.info("Finished!");
		}

		executor.shutdownNow();
	}

	public void interrupt() {
		run = false;
	}

}
