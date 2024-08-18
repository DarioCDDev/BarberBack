package com.barber.dto;

import java.time.LocalDateTime;

public class BarberAppointmentDTO {
	private Long idAppointment;
    private UserDTO barber;
    private UserDTO client;
    private LocalDateTime appointmentTime;
    private StatusDTO status;
    private ServiceDTO service;
    private String comments;
	public Long getIdAppointment() {
		return idAppointment;
	}
	public void setIdAppointment(Long idAppointment) {
		this.idAppointment = idAppointment;
	}
	public UserDTO getBarber() {
		return barber;
	}
	public void setBarber(UserDTO barber) {
		this.barber = barber;
	}
	public UserDTO getClient() {
		return client;
	}
	public void setClient(UserDTO client) {
		this.client = client;
	}
	public LocalDateTime getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(LocalDateTime appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public StatusDTO getStatus() {
		return status;
	}
	public void setStatus(StatusDTO status) {
		this.status = status;
	}
	public ServiceDTO getService() {
		return service;
	}
	public void setService(ServiceDTO service) {
		this.service = service;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
    

}
