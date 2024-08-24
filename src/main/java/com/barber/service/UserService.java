package com.barber.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import com.barber.security.TokenUtils;

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

	@Autowired
	private JavaMailSender mailSender;

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

			String verificationCode = RandomStringUtils.randomAlphanumeric(8);
			user.setVerificationCode(verificationCode);
			user.setVerified(false);
			User savedUser = userRepository.save(user);

			sendVerificationEmail(savedUser.getEmail(), verificationCode);

			return savedUser;
		} catch (Exception e) {
			throw e;
		}

	}

	private void sendVerificationEmail(String email, String verificationCode) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Verifica tu correo electrónico");
		message.setText("Tu código de verificación es: " + verificationCode);
		mailSender.send(message);
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
				UserDTO userDTO = new UserDTO(user.getIdUser(), user.getName(), user.getPhone(), user.getEmail(),
						user.getPhoto());
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
	public ResponseEntity<?> updateUser(Long id, User user, String userPassword) {
		Map<String, Object> response = new HashMap<>();

		Optional<User> _user = userRepository.findById(id);
		
		if (_user.isPresent()) {
			User currentUser = _user.get();

			if (user.getName() != null && user.getName() != "") {
				currentUser.setName(user.getName());
			}

			if (user.getPhone() != null && user.getPhone() != "") {
				currentUser.setPhone(user.getPhone());
			}
			if ((userPassword != null && user.getPassword() != null)
					&& (userPassword != "" && user.getPassword() != "")) {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				if (passwordEncoder.matches(userPassword, currentUser.getPassword())) {
					if (!passwordEncoder.matches(user.getPassword(), currentUser.getPassword())) {
						currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
					}
				} else {
					response.put("message", "La contraseña actual no coincide");
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}
			}
			userRepository.save(currentUser);
			String token = TokenUtils.createToken(currentUser.getName(), currentUser.getEmail(), currentUser.getRol(), currentUser.getIdUser(),
					currentUser.getPhone(), currentUser.isVerified());
			response.put("data", token);
			response.put("message", "Usuario editado con éxito");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		response.put("message", "No se ha podido actualizar los datos");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
