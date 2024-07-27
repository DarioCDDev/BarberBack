package com.barber.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barber.dto.CreateUserRequest;
import com.barber.dto.UserDTO;
import com.barber.entities.Rol;
import com.barber.entities.Schedule;
import com.barber.entities.User;
import com.barber.repository.RolRepository;
import com.barber.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
@Transactional
public class UserService {

	private static final String SECRET_KEY = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RolRepository rolRepository;

	public User createUser(CreateUserRequest createUserRequest) {
		try {
			String bcryptHasgRegex = "\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}";
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

			boolean isBcryptHash = createUserRequest.getUser().getPassword().matches(bcryptHasgRegex);
			if (createUserRequest.getUser().getPassword() != null && createUserRequest.getUser().getPassword() != ""
					&& !isBcryptHash) {
				createUserRequest.getUser()
						.setPassword(new BCryptPasswordEncoder().encode(createUserRequest.getUser().getPassword()));
			}

			// Guardar el usuario en la base de datos
			return userRepository.save(user);
		} catch (Exception e) {
			throw e;
		}

	}

	public Claims obtenerClaimsDesdeToken(String token) {
		try {
			if (token.startsWith("Bearer ")) {
				token = token.replaceFirst("Bearer ", "");
			}

			Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build()
					.parseClaimsJws(token);

			return jws.getBody();
		} catch (Exception e) {
			// Manejar la excepción según tus necesidades (puede ser token inválido,
			// expirado, etc.)
			throw new RuntimeException("No se pudo obtener los claims del token", e);
		}
	}

	// Read a User by ID
	public User getUserById(Long id) {
		return userRepository.findById(id).get();
	}

	public List<UserDTO> getUsersByRol(Long rolId) {
		List<User> userList = userRepository.findAll();
		Rol rol = rolRepository.findById(rolId).orElse(null);

		if (rol == null) {
			return new ArrayList<>();
		}

		List<UserDTO> userDTOList = new ArrayList<>();
		for (User user : userList) {
			if (user.getRol().equals(rol)) {
				System.out.println(user.getIdUser());
				UserDTO userDTO = new UserDTO(
						user.getName(), user.getPhone(), user.getEmail(), user.getPhoto()
				);
				userDTOList.add(userDTO);
			}
		}

		return userDTOList;
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
