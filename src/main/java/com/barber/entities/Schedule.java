package com.barber.entities;

import java.util.Map;
import java.util.List;

public class Schedule {
	private Map<String, List<TimeInterval>> weeklySchedule; // Mapa de días a listas de intervalos de tiempo

	public Schedule() {
	}

	public Schedule(Map<String, List<TimeInterval>> weeklySchedule) {
		this.weeklySchedule = weeklySchedule;
	}

	public Map<String, List<TimeInterval>> getWeeklySchedule() {
		return weeklySchedule;
	}

	public void setWeeklySchedule(Map<String, List<TimeInterval>> weeklySchedule) {
		this.weeklySchedule = weeklySchedule;
	}

	@Override
	public String toString() {
		return "Schedule{" + "weeklySchedule=" + weeklySchedule + '}';
	}
}
