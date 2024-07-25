package com.barber.dto;

public class CreateAppointmentRequest {
	private AppointmentDTO appointment;
	private Long statusId;

	public AppointmentDTO getAppointment() {
		return appointment;
	}

	public void setAppointment(AppointmentDTO appointment) {
		this.appointment = appointment;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

}
