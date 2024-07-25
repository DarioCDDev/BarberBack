package com.barber.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_table")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUser;

	@Column
	private String name;

	@Column
	private String phone;

	@Column
	private String email;

	@ManyToOne
	@JoinColumn(name = "idRol")
	private Rol rol;

	@JsonIgnore
	@OneToMany(mappedBy = "barber")
	private List<Appointment> appointmentsAsBarber = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "client")
	private List<Appointment> appointmentsAsClient = new ArrayList<>();

	public User() {
	}

	public User(String name, String phone, String email, Rol rol) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.rol = rol;
	}

	public User(Long idUser, String name, String phone, String email, Rol rol) {
		this.idUser = idUser;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.rol = rol;
	}

	public User(Long idUser, String name, String phone, String email, Rol rol, List<Appointment> appointmentsAsBarber,
			List<Appointment> appointmentsAsClient) {
		this.idUser = idUser;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.rol = rol;
		this.appointmentsAsBarber = appointmentsAsBarber;
		this.appointmentsAsClient = appointmentsAsClient;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public List<Appointment> getAppointmentsAsBarber() {
		return appointmentsAsBarber;
	}

	public void setAppointmentsAsBarber(List<Appointment> appointmentsAsBarber) {
		this.appointmentsAsBarber = appointmentsAsBarber;
	}

	public List<Appointment> getAppointmentsAsClient() {
		return appointmentsAsClient;
	}

	public void setAppointmentsAsClient(List<Appointment> appointmentsAsClient) {
		this.appointmentsAsClient = appointmentsAsClient;
	}

}
