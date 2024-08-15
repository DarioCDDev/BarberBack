package com.barber.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "status_table")
public class Status {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idStatus;

	@Column(unique = true)
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "status")
	private List<Appointment> appointments;

	public Status() {
	}

	public Status(Long idStatus, String name, List<Appointment> appointments) {
		this.idStatus = idStatus;
		this.name = name;
		this.appointments = appointments;
	}

	public Status(String name, List<Appointment> appointments) {
		this.name = name;
		this.appointments = appointments;
	}

	public Status(Long idStatus, String name) {
		this.idStatus = idStatus;
		this.name = name;
	}

	public Long getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(Long idStatus) {
		this.idStatus = idStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}

	@Override
	public String toString() {
		return "Status [idStatus=" + idStatus + ", name=" + name + "]";
	}

}
