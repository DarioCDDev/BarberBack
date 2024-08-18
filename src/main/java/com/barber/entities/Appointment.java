package com.barber.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointment_table")
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idAppointment;

	@ManyToOne
	@JoinColumn(name = "barber_id", nullable = false)
	private User barber;

	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private User client;

	@Column(nullable = false)
	private LocalDateTime appointmentTime;

	@ManyToOne
	@JoinColumn(name = "idStatus")
	private Status status;

	@ManyToOne
	@JoinColumn(name = "service_id", nullable = false)
	private Service service;

	@Column(length = 500) // Puedes ajustar el tamaño según sea necesario
	private String comments;

	public Appointment() {
	}

	public Appointment(User barber, User client, LocalDateTime appointmentTime, Service service) {
		this.barber = barber;
		this.client = client;
		this.appointmentTime = appointmentTime;
		this.service = service;
	}

	public Appointment(Long idAppointment, User barber, User client, LocalDateTime appointmentTime, Status status,
			Service service, String comments) {
		this.idAppointment = idAppointment;
		this.barber = barber;
		this.client = client;
		this.appointmentTime = appointmentTime;
		this.status = status;
		this.service = service;
		this.comments = comments;
	}

	public Appointment(User barber, User client, LocalDateTime appointmentTime, Service service,
			String comments) {
		this.barber = barber;
		this.client = client;
		this.appointmentTime = appointmentTime;
		this.service = service;
		this.comments = comments;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getIdAppointment() {
		return idAppointment;
	}

	public void setIdAppointment(Long idAppointment) {
		this.idAppointment = idAppointment;
	}

	public User getBarber() {
		return barber;
	}

	public void setBarber(User barber) {
		this.barber = barber;
	}

	public User getClient() {
		return client;
	}

	public void setClient(User client) {
		this.client = client;
	}

	public LocalDateTime getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(LocalDateTime appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Appointment [idAppointment=" + idAppointment + ", barber=" + barber + ", client=" + client
				+ ", appointmentTime=" + appointmentTime + ", status=" + status + ", service=" + service + ", comments="
				+ comments + "]";
	}

}
