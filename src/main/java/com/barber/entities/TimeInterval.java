package com.barber.entities;

public class TimeInterval {
	private String startTime;
	private String endTime;

	public TimeInterval() {
	}

	public TimeInterval(String startTime, String endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "TimeInterval{" + "startTime='" + startTime + '\'' + ", endTime='" + endTime + '\'' + '}';
	}
}
