package com.barber.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import com.barber.dto.CreateUserRequest;
import com.barber.dto.UserDTO;
import com.barber.entities.User;
import com.barber.repository.UserRepository;
import com.barber.service.UserService;

import io.jsonwebtoken.Claims;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

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

	@GetMapping("/user/rol")
	public List<UserDTO> getUserByRol(@RequestParam Long rolId) {
		return userService.getUsersByRol(rolId);
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

	@PostMapping("/{id}/photo")
	public ResponseEntity<String> uploadPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photo) {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			try {
				user.setPhoto(photo.getBytes());
				userRepository.save(user);
				return ResponseEntity.status(HttpStatus.OK).body("Photo uploaded successfully");
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading photo");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
	}

	@GetMapping("/{id}/photo")
	public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			byte[] photo = user.getPhoto();
			if (photo != null) {
				return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

}
