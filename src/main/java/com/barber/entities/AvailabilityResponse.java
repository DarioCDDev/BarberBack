package com.barber.entities;

import java.util.List;
import java.util.Map;

public class AvailabilityResponse {
	private List<String> dateTimestamps;
	private Map<String, List<Map<String, String>>> availability;

	public AvailabilityResponse(List<String> dateTimestamps, Map<String, List<Map<String, String>>> availability) {
		this.dateTimestamps = dateTimestamps;
		this.availability = availability;
	}

	public List<String> getDateTimestamps() {
		return dateTimestamps;
	}

	public void setDateTimestamps(List<String> dateTimestamps) {
		this.dateTimestamps = dateTimestamps;
	}

	public Map<String, List<Map<String, String>>> getAvailability() {
		return availability;
	}

	public void setAvailability(Map<String, List<Map<String, String>>> availability) {
		this.availability = availability;
	}

}
