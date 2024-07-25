package com.barber.dto;

import com.barber.entities.Schedule;
import com.barber.entities.User;

public class CreateUserRequest {
	private Long rol_id;
	private User user;
	private Schedule schedule; // Campo para el horario detallado

	// Getters y Setters

	public Long getRol_id() {
		return rol_id;
	}

	public void setRol_id(Long rol_id) {
		this.rol_id = rol_id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
}
