package com.barber.dto;

import com.barber.entities.User;

public class CreateUserRequest {
	private User user;
	private Long rol_id;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getRol_id() {
		return rol_id;
	}

	public void setRol_id(Long rol_id) {
		this.rol_id = rol_id;
	}

}