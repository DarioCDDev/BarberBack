package com.barber.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.barber.dto.BarberAppointmentDTO;
import com.barber.dto.CreateAppointmentRequest;
import com.barber.entities.Appointment;
import com.barber.entities.AvailabilityResponse;
import com.barber.entities.Service;
import com.barber.entities.User;
import com.barber.repository.ServiceRepository;
import com.barber.service.AppointmentService;
import com.barber.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ServiceRepository serviceRepository;

	@GetMapping("/appointments")
	public List<Appointment> getAllAppointments() {
		return appointmentService.getAllAppointments();
	}

	@GetMapping("/appointments/{id}")
	public Appointment getAppointmentById(@PathVariable Long id) {
		return appointmentService.getAppointmentById(id).orElse(null);
	}

	@PostMapping("/appointments")
	public ResponseEntity<?> createAppointment(@RequestBody CreateAppointmentRequest appointmentRequest) {
		System.out.println(appointmentRequest);
		User barber = userService.findUserById(appointmentRequest.getAppointment().getBarberId());
		User client = userService.findUserById(appointmentRequest.getAppointment().getClientId());
		Service service = serviceRepository.findById(appointmentRequest.getServiceId()).get();
		Appointment appointment = new Appointment(barber, client,
				appointmentRequest.getAppointment().getAppointmentTime(),service,  appointmentRequest.getComment());
		return appointmentService.createAppointment(appointment, appointmentRequest.getStatusId());
	}

	@PutMapping("/appointments/{id}")
	public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
		return appointmentService.updateAppointment(id, appointment);
	}

	@DeleteMapping("/appointments/{id}")
	public void deleteAppointment(@PathVariable Long id) {
		appointmentService.deleteAppointment(id);
	}

	@GetMapping("/appointments/barber")
	public List<BarberAppointmentDTO> findAppointmentsByBarberId(@RequestParam Long barberId) {
		return appointmentService.findAppointmentsByBarberId(barberId);
	}

	@GetMapping("/appointments/barber/status")
	public List<Appointment> findAppointmentsByBarberIdAndStatus(@RequestParam Long barberId,
			@RequestParam Long statusId) {
		return appointmentService.findAppointmentsByBarberIdAndStatus(barberId, statusId);
	}

	@GetMapping("/appointments/client")
	public List<Appointment> findAppointmentsByClientId(@RequestParam Long clientId) {
		return appointmentService.findAppointmentsByClientId(clientId);
	}

	@GetMapping("/appointments/client/status")
	public List<Appointment> findAppointmentsByClientIdAndStatus(@RequestParam Long clientId,
			@RequestParam Long statusId) {
		return appointmentService.findAppointmentsByClientIdAndStatus(clientId, statusId);
	}

	@GetMapping("/appointments/fullCalendar")
	public ResponseEntity<AvailabilityResponse> getAvailabilityForBarber(@RequestParam Long barberId) {
		return appointmentService.getAvailabilityForBarber(barberId);
	}

	@GetMapping("/appointments/date-range")
	public List<Appointment> findAppointmentsByDateRange(@RequestParam("start") LocalDateTime start,
			@RequestParam("end") LocalDateTime end) {
		return appointmentService.findAppointmentsByDateRange(start, end);
	}
}
