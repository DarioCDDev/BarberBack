package com.barber.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barber.dto.CreateUserRequest;
import com.barber.entities.User;
import com.barber.service.UserService;

import io.jsonwebtoken.Claims;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/user")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}
	
	@GetMapping("/user/token")
	public Claims getAllUsers(@RequestParam String token) {
		return userService.obtenerClaimsDesdeToken(token);
	}

	@GetMapping("/user/{id}")
	public User getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@DeleteMapping("/user/{id}")
	public User deleteUser(@PathVariable Long id) {
		return userService.deleteUser(id);
	}

	@PutMapping("/user/{id}")
	public User updateUser(@PathVariable Long id, @RequestBody User user) {
		return userService.updateUser(id, user);
	}

	@PostMapping("/user")
	public User createUser(@RequestBody CreateUserRequest user) {
		return userService.createUser(user);
	}

}
