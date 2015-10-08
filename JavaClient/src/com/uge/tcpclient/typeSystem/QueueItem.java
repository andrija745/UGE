package com.uge.tcpclient.typeSystem;

import java.util.concurrent.Future;

public class QueueItem {
	private Future<String> future;
	private String cycle;
	private String endPoint;

	public QueueItem(Future<String> future, String cycle, String endPoint) {
		super();
		this.future = future;
		this.cycle = cycle;
		this.endPoint = endPoint;
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

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public String toString() {
		return "QueueItem [future=" + future.toString() + ", cycle=" + cycle + ", endPoint=" + endPoint + "]";
	}

}
