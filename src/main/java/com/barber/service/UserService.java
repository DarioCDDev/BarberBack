package com.barber.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barber.dto.CreateUserRequest;
import com.barber.entities.Rol;
import com.barber.entities.Schedule;
import com.barber.entities.User;
import com.barber.repository.RolRepository;
import com.barber.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RolRepository rolRepository;

	public User createUser(CreateUserRequest createUserRequest) {
		// Obtener el rol basado en el ID proporcionado
		Rol rol = rolRepository.findById(createUserRequest.getRol_id())
				.orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

		// Obtener el usuario de la solicitud
		User user = createUserRequest.getUser();
		user.setRol(rol);

		// Establecer el horario solo si el rol es barbero
		if (rol.getIdRol() == 1) { // Rol es barbero
			Schedule requestSchedule = createUserRequest.getSchedule();
			if (requestSchedule == null || requestSchedule.getWeeklySchedule().isEmpty()) {
				throw new IllegalArgumentException("El horario es requerido para los barberos.");
			}
			user.setSchedule(requestSchedule);
		} else {
			// Para roles que no son barberos, asegurarse de que el horario no esté
			// establecido
			user.setSchedule(null);
		}

		// Guardar el usuario en la base de datos
		return userRepository.save(user);
	}

	// Read a User by ID
	public User getUserById(Long id) {
		return userRepository.findById(id).get();
	}

	// Read all Users
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Update an existing User
	public User updateUser(Long id, User user) {

		Optional<User> _user = userRepository.findById(id);
		if (_user.isPresent()) {
			User currentUser = _user.get();
			if (currentUser.getName() != null) {
				currentUser.setName(user.getName());
			}
			if (currentUser.getEmail() != null) {
				currentUser.setName(user.getEmail());
			}
			if (currentUser.getPhone() != null) {
				currentUser.setName(user.getPhone());
			}

			return userRepository.save(currentUser);
		}
		return null; // or throw an exception if preferred
	}

	public User findUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	}

	// Delete a User by ID
	public User deleteUser(Long id) {
		User user = userRepository.findById(id).get();
		userRepository.deleteById(id);
		return user;
	}

	// Find User by email
	public Optional<User> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
}
