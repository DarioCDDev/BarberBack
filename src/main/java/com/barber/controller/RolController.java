package com.barber.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barber.entities.Rol;
import com.barber.service.RolService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class RolController {

	@Autowired
	RolService rolService;

	@GetMapping("/rol")
	public List<Rol> getAllUsers() {
		return rolService.getAllRoles();
	}

	@PostMapping("/rol")
	public Rol createUser(@RequestBody Rol rol) {
		return rolService.createRol(rol);
	}

}
