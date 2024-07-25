package com.barber.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@JoinColumn(name = "idRol", nullable = false)
	private Rol rol;

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

}
