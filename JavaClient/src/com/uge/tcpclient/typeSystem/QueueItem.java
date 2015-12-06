package com.uge.tcpclient.typeSystem;

import java.util.concurrent.Future;

public class QueueItem {
	private Future<String> future;
	private String cycle;
	private SocketResources sktRsrc;
	private int count;

	public QueueItem(Future<String> future, String cycle, SocketResources skt) {
		super();
		this.future = future;
		this.cycle = cycle;
		this.sktRsrc = skt;
	}

	public Future<String> getFuture() {
		return future;
	}

	public void setFuture(Future<String> resp) {
		this.future = resp;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public SocketResources getSocketResorces() {
		return sktRsrc;
	}

	@Override
	public String toString() {
		return "QueueItem [future=" + future.toString() + ", cycle=" + cycle + ", endPoint="
				+ sktRsrc.getSocket().getRemoteSocketAddress().toString() + "]";
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
