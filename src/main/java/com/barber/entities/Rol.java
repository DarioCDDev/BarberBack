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
@Table(name = "rol_table")
public class Rol {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idRol;

	@Column(unique = true)
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "rol")
	private List<User> users;

	public Rol(Long idRol, String name, List<User> users) {
		this.idRol = idRol;
		this.name = name;
		this.users = users;
	}

	public Rol() {
	}

	public Rol(Long idRol, String name) {
		this.idRol = idRol;
		this.name = name;
	}

	public Long getIdRol() {
		return idRol;
	}

	public void setIdRol(Long idRol) {
		this.idRol = idRol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
