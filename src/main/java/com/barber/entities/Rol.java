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
import lombok.Data;

@Entity
@Table(name = "rol_table")
@Data
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

}
