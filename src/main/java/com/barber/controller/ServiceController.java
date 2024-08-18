package com.barber.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barber.entities.Service;
import com.barber.service.ServiceService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class ServiceController {

	@Autowired
	ServiceService serviceService;

	@GetMapping("/public/service")
	public List<Service> getAllUsers() {
		return serviceService.getAllServices();
	}

	@PostMapping("/service")
	public Service createStatus(@RequestBody Service service) {
		return serviceService.creatService(service);
	}

}
