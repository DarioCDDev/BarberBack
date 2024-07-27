package com.barber.dto;

public class UserDTO {

	private String name;
	private String phone;
	private String email;
	private byte[] photo;

	public UserDTO() {
	}

	public UserDTO(String name, String phone, String email, byte[] photo) {

		this.name = name;
		this.phone = phone;
		this.email = email;
		this.photo = photo;

	}

	// Getters y setters

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

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	

}