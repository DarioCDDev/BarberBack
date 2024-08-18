package com.barber.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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

	@Column(unique = true)
	private String phone;

	@Column(unique = true)
	private String email;

	@Column
	private String password;

	@Column
	private boolean verified = false;
	@Column
	private String verificationCode;

	@Lob
	@Column(name = "photo", nullable = true)
	private byte[] photo;

	@ManyToOne
	@JoinColumn(name = "idRol")
	private Rol rol;

	@Column(name = "schedule", nullable = true, columnDefinition = "TEXT")
	@Convert(converter = ScheduleConverter.class)
	private Schedule schedule;

	@JsonIgnore
	@OneToMany(mappedBy = "barber")
	private List<Appointment> appointmentsAsBarber = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "client")
	private List<Appointment> appointmentsAsClient = new ArrayList<>();

	public User() {
	}

	public User(String name, String phone, String email, String password) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.password = password;
	}

	public User(String name, String phone, String email, String password, Rol rol) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.rol = rol;
		this.password = password;
	}

	public User(Long idUser, String name, String phone, String email, String password, Rol rol, Schedule schedule,
			byte[] photo) {
		this.idUser = idUser;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.photo = photo;
		this.rol = rol;
		this.schedule = schedule;
		this.password = password;
	}

	public User(Long idUser, String name, String phone, String email, String password, Rol rol) {
		this.idUser = idUser;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.rol = rol;
		this.password = password;
	}

	public User(Long idUser, String name, String phone, String email, String password, Rol rol,
			List<Appointment> appointmentsAsBarber, List<Appointment> appointmentsAsClient, Schedule schedule) {
		this.idUser = idUser;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.rol = rol;
		this.appointmentsAsBarber = appointmentsAsBarber;
		this.appointmentsAsClient = appointmentsAsClient;
		this.schedule = schedule;
		this.password = password;
	}

	public User(Long idUser, String name, String phone, String email, String password, Rol rol, Schedule schedule) {
		this.idUser = idUser;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.rol = rol;
		this.schedule = schedule;
		this.password = password;
	}

	// Getters and setters...

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

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", name=" + name + ", phone=" + phone + ", email=" + email + ", password="
				+ password + ", photo=" + Arrays.toString(photo) + ", rol=" + rol + ", schedule=" + schedule
				+ ", appointmentsAsBarber=" + appointmentsAsBarber + ", appointmentsAsClient=" + appointmentsAsClient
				+ "]";
	}

}
