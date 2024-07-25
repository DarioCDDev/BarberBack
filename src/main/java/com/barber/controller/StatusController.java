package com.barber.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barber.entities.Status;
import com.barber.service.StatusService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class StatusController {

	@Autowired
	StatusService statsusService;

	@GetMapping("/status")
	public List<Status> getAllUsers() {
		return statsusService.getAllStatus();
	}

	@PostMapping("/status")
	public Status createStatus(@RequestBody Status status) {
		return statsusService.createStatus(status);
	}

}
