package com.uge.tcpclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

import com.uge.tcpclient.typeSystem.QueueItem;

public class TCPPeriodicTask extends TimerTask {
	static Logger log = Logger.getLogger(TCPPeriodicTask.class.getName());

	private static boolean run = true;
	private static ExecutorService executor;
	private static Queue<QueueItem> queue;
	private static int count = 0;

	private List<String> endPoints;

	public TCPPeriodicTask(List<String> ipAddresses) {
		executor = Executors.newCachedThreadPool();
		queue = new LinkedList<QueueItem>();
		endPoints = ipAddresses;
	}

	@Override
	public void run() {

		if (run) {
			// try {
			Calendar cal = Calendar.getInstance();
			String min = String.valueOf(cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE) + 1);
			while (min.length() < 4)
				min = "0" + min;

			for (String endPoint : endPoints) {
				Future<String> future = executor.submit(new TCPExecutor(min, endPoint));
				Queue<QueueItem> tmp_queue = new LinkedList<QueueItem>();
				tmp_queue.add(new QueueItem(future, min, endPoint));
				tmp_queue.addAll(queue);
				queue.clear();
				queue.addAll(tmp_queue);
			}

			log.info("Started..");
			int i = 0;
			int size = queue.size();
			while (i < size) { // sta se zbiva ukoliko se u nastavku neki future
								// vrati u queue u okviru exceptions? size nece
								// da oslikava realno stanje u redu?
				i++;
				QueueItem item = queue.poll();
				Future<String> future = item.getFuture();
				try {
					String resp = future.get(40, TimeUnit.SECONDS);
					log.info(resp);
					if (resp == null || resp.length() < 10) {
						count++;
						requeueFutureTask(item);
					} else
						writeToFile(item.getEndPoint(), item.getCycle(), resp, count);
				} catch (TimeoutException e) {
					// future.cancel(true);
					count++;
					requeueFutureTask(item);
					log.log(Level.SEVERE, "Terminated - Time Out!", e);
				} catch (InterruptedException e) {
					// future.cancel(true);
					count++;
					requeueFutureTask(item);
					log.log(Level.SEVERE, "Terminated - Interupt!", e);
				} catch (ExecutionException e) {
					// future.cancel(true);
					count++;
					requeueFutureTask(item);
					log.log(Level.SEVERE, "Terminated - Execution!", e);
				} catch (Exception e) {
					// future.cancel(true);
					count++;
					requeueFutureTask(item);
					log.log(Level.SEVERE, "Terminated!", e);
				}
			}
			log.info("Finished!");
		}
	}

	private void requeueFutureTask(QueueItem item) {
		TCPExecutor exec = new TCPExecutor(item.getCycle(), item.getEndPoint());
		Future<String> future = executor.submit(exec);
		queue.add(new QueueItem(future, item.getCycle(), item.getEndPoint()));
		log.warning("Requed cycle " + item.getCycle());
	}

	public void interrupt() {
		run = false;
		executor.shutdownNow();
	}

	private void writeToFile(String endPoint, String cycle, String resp, int count) {
		if (resp == null || resp.length() < 10)
			return;

		try {
			BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File("data-" + endPoint + ".txt"), true)));

			out.write("\n" + cycle + ": " + resp + "\n");
			out.write("ciklusi su vraceni u red cekanja ukupno: " + count + " puta.\n");
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
