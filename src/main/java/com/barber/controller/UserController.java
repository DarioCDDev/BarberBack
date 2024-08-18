package com.barber.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.barber.dto.UpdateUserDto;
import com.barber.dto.UserDTO;
import com.barber.entities.Rol;
import com.barber.entities.User;
import com.barber.repository.RolRepository;
import com.barber.repository.UserRepository;
import com.barber.security.AuthCredentials;
import com.barber.security.TokenUtils;
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
	
	@Autowired
	RolRepository rolRepository;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JavaMailSender mailSender;

	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthCredentials authCredentials) {
        try {
            // Autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authCredentials.getEmail(), 
                            authCredentials.getPassword()
                    )
            );

            // Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener el rol del usuario. Ajusta esto según cómo obtengas el rol del usuario.
            // Este es un ejemplo simple. Necesitarás ajustar según tu implementación.
            User user = userRepository.findByEmail(authCredentials.getEmail()).get();

            // Generar el token JWT
            String token = TokenUtils.createToken(user.getName(), user.getEmail(), user.getRol(), user.getIdUser(), user.getPhone());

            // Devolver el token en la respuesta
            return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

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
	
	@GetMapping("/public/user/rol")
	public List<UserDTO> getUserByRolPublic(@RequestParam Long rolId) {
		return userService.getUsersByRol(rolId);
	}

	@DeleteMapping("/user/{id}")
	public User deleteUser(@PathVariable Long id) {
		return userService.deleteUser(id);
	}

	@PutMapping("/user/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserDto userDto) {
		return userService.updateUser(id, userDto.getUser(), userDto.getNewPassword());
	}

	@PostMapping("/register")
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
	            return ResponseEntity.ok()
	                .contentType(MediaType.IMAGE_JPEG)
	                .body(photo);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}
	
	 @PostMapping("/verify")
	    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String code) {
	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

	        if (user.getVerificationCode().equals(code)) {
	            user.setVerified(true);
	            userRepository.save(user);
	            SimpleMailMessage message = new SimpleMailMessage();
	            message.setTo(email);
	    		message.setSubject("Verifica tu correo electrónico");
	    		message.setText("Cuenta verificada exitosamente.");
	    		mailSender.send(message);
	            return ResponseEntity.ok("Cuenta verificada exitosamente.");
	        } else {
	            return ResponseEntity.badRequest().body("Código de verificación incorrecto.");
	        }
	    }


}
