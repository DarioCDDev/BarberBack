package com.barber.entities;

import java.util.List;

public class AvailabilityResponse {
	private List<String> dates; // Lista de fechas en formato timestamp
	private List<String> availableTimes; // Lista de horas disponibles en formato "HH:mm"

	public AvailabilityResponse(List<String> dates, List<String> availableTimes) {
		this.dates = dates;
		this.availableTimes = availableTimes;
	}

	public List<String> getDates() {
		return dates;
	}

	public void setDates(List<String> dates) {
		this.dates = dates;
	}

	public List<String> getAvailableTimes() {
		return availableTimes;
	}

	public void setAvailableTimes(List<String> availableTimes) {
		this.availableTimes = availableTimes;
	}
}
