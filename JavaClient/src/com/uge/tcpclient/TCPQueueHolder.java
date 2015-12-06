package com.uge.tcpclient;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.uge.tcpclient.typeSystem.QueueItem;

public class TCPQueueHolder extends Thread {
	static Logger log = Logger.getLogger(TCPQueueHolder.class.getName());

	private static Queue<QueueItem> queue;
	private static ExecutorService executor;

	private static boolean run = true;

	public TCPQueueHolder() {
		executor = Executors.newCachedThreadPool();
		queue = new LinkedList<QueueItem>();
	}

	public static Queue<QueueItem> getQueue() {
		return queue;
	}

	public void run() {
		while (run) {
			log.info("Started queue flush..");
			int i = 0;
			int size = queue.size();
			while (i < size) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// sta se zbiva ukoliko se u nastavku neki future
				// vrati u queue u okviru exceptions? size nece
				// da oslikava realno stanje u redu?
				i++;
				QueueItem item = queue.poll();
				Future<String> future = item.getFuture();
				executeAndRequeue(future, item);
			}
			log.info("Finished queue flush!");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void requeueFutureTask(QueueItem item) {
		TCPExecutor exec = new TCPExecutor(item.getCycle(), item.getSocketResorces(), item.getCount());
		Future<String> future = executor.submit(exec);
		queue.add(new QueueItem(future, item.getCycle(), item.getSocketResorces()));
		log.warning("Requed cycle " + item.getCycle());
	}

	protected static void executeAndRequeue(Future<String> future, QueueItem item) {
		try {
			String resp = future.get(40, TimeUnit.SECONDS);
			log.info(resp);
			if (resp == null || resp.length() < 10) {
				item.setCount(item.getCount() + 1);
				TCPQueueHolder.requeueFutureTask(item);
			}
		} catch (TimeoutException e) {
			// future.cancel(true);
			item.setCount(item.getCount() + 1);
			TCPQueueHolder.requeueFutureTask(item);
			log.log(Level.SEVERE, "Terminated - Time Out!", e);
		} catch (InterruptedException e) {
			// future.cancel(true);
			item.setCount(item.getCount() + 1);
			TCPQueueHolder.requeueFutureTask(item);
			log.log(Level.SEVERE, "Terminated - Interupt!", e);
		} catch (ExecutionException e) {
			// future.cancel(true);
			item.setCount(item.getCount() + 1);
			TCPQueueHolder.requeueFutureTask(item);
			log.log(Level.SEVERE, "Terminated - Execution!", e);
		} catch (Exception e) {
			// future.cancel(true);
			item.setCount(item.getCount() + 1);
			TCPQueueHolder.requeueFutureTask(item);
			log.log(Level.SEVERE, "Terminated!", e);
		}
	}

	public void interrupt() {
		run = false;
		executor.shutdownNow();
	}
}
