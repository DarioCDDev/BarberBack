package com.barber.dto;

public class CreateAppointmentRequest {
	private AppointmentDTO appointment;
	private Long statusId;
	private Long serviceId;
	private String comment;

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

	
	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "CreateAppointmentRequest [appointment=" + appointment + ", statusId=" + statusId + ", serviceId="
				+ serviceId + ", comment=" + comment + "]";
	}
	
	
	
	

}
