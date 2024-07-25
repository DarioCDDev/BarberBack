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

	public Appointment() {
	}

	public Appointment(User barber, User client, LocalDateTime appointmentTime) {
		this.barber = barber;
		this.client = client;
		this.appointmentTime = appointmentTime;
	}

	public Appointment(Long idAppointment, User barber, User client, LocalDateTime appointmentTime, Status status) {
		this.idAppointment = idAppointment;
		this.barber = barber;
		this.client = client;
		this.appointmentTime = appointmentTime;
		this.status = status;
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

	@Override
	public String toString() {
		return "Appointment [idAppointment=" + idAppointment + ", barber=" + barber + ", client=" + client
				+ ", appointmentTime=" + appointmentTime + "]";
	}

}
