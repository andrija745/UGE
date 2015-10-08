package com.uge.tcpclient.typeSystem;

public class ExecutorResponse {
	private String resp;
	private String cycle;
	private String endPoint;

	public ExecutorResponse(String resp, String cycle, String endPoint) {
		super();
		this.resp = resp;
		this.cycle = cycle;
		this.endPoint = endPoint;
	}

	public String getResp() {
		return resp;
	}

	public void setResp(String resp) {
		this.resp = resp;
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
		return "ExecutorResponse [resp=" + resp + ", cycle=" + cycle + ", endPoint=" + endPoint + "]";
	}

}
