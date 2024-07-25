package com.barber.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barber.entities.Appointment;
import com.barber.entities.Schedule;
import com.barber.entities.Status;
import com.barber.entities.TimeInterval;
import com.barber.entities.User;
import com.barber.repository.AppointmentRepository;
import com.barber.repository.StatusRepository;
import com.barber.repository.UserRepository;

@Transactional
@EnableScheduling
@Service
public class AppointmentService {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StatusRepository statusRepository;

	public ResponseEntity<?> createAppointment(Appointment appointment, Long statusId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LocalDateTime now = LocalDateTime.now();
			if (appointment.getAppointmentTime().isBefore(now)) {
				response.put("message", "Error, esa hora ya ha pasado");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// Verificar disponibilidad de la hora de la cita
			List<Appointment> allAppointments = appointmentRepository.findAll();
			for (Appointment existingAppointment : allAppointments) {
				if (appointment.getAppointmentTime().equals(existingAppointment.getAppointmentTime())) {
					response.put("message", "Error, esa hora ya está ocupada");
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}
			}

			// Verificar citas existentes del cliente
			List<Appointment> clientAppointments = appointment.getClient().getAppointmentsAsClient();
			for (Appointment existingClientAppointment : clientAppointments) {
				if (existingClientAppointment.getStatus().getIdStatus().equals(1L)) {
					response.put("message", "Error, ya tienes una cita asociada");
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}
			}

			// Obtener el horario del barbero y verificar disponibilidad
			User barber = appointment.getBarber();
			Schedule barberSchedule = barber.getSchedule();

			
			
			boolean isTimeAvailable = isAppointmentTimeWithinSchedule(appointment.getAppointmentTime(), barberSchedule);
			System.out.println("-----------------------------------");
			System.out.println(isTimeAvailable);
			if (!isTimeAvailable) {
				response.put("message", "Error, la hora solicitada no está dentro del horario disponible del barbero");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// Obtener el estado y guardar la cita
			Status status = statusRepository.findById(statusId)
					.orElseThrow(() -> new RuntimeException("Status not found"));
			appointment.setStatus(status);
			response.put("data", appointmentRepository.save(appointment));
			response.put("message", "Cita creada con éxito");
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.put("error", e.getMessage());
			response.put("message", "Error al crear la cita");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	public boolean isAppointmentTimeWithinSchedule(LocalDateTime appointmentTime, Schedule barberSchedule) {
		if (barberSchedule == null || barberSchedule.getWeeklySchedule() == null) {
			System.out.println("Entro en el false");
			return false;
		}

		// Obtener el día de la semana del LocalDateTime
		String dayOfWeek = appointmentTime.getDayOfWeek().toString();
		String normalizedDayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();
		
		System.out.println(normalizedDayOfWeek);

		// Obtener los intervalos de tiempo para el día de la semana
		List<TimeInterval> timeIntervals = barberSchedule.getWeeklySchedule().get(normalizedDayOfWeek);
		System.out.println(timeIntervals);

		if (timeIntervals == null || timeIntervals.isEmpty()) {
			return false;
		}

		LocalTime appointmentTimeOnly = appointmentTime.toLocalTime();

		for (TimeInterval interval : timeIntervals) {
			LocalTime startTime = LocalTime.parse(interval.getStartTime());
			LocalTime endTime = LocalTime.parse(interval.getEndTime());

			if (appointmentTimeOnly.isAfter(startTime) && appointmentTimeOnly.isBefore(endTime)) {
				return true;
			}
		}

		return false;
	}

	@Scheduled(fixedRate = 3600000)
	public List<Appointment> checkAppointments() {
		List<Appointment> listAppointments = appointmentRepository.findAll();
		List<Appointment> updatedAppointments = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();
		Status status = statusRepository.findById(3l).get();

		for (Appointment appointment : listAppointments) {
			System.out.println(appointment.getAppointmentTime().isBefore(now) && appointment.getStatus().getIdStatus() == 1l);
			if (appointment.getAppointmentTime().isBefore(now) && appointment.getStatus().getIdStatus() == 1l) {
				appointment.setStatus(status);
				appointmentRepository.save(appointment);
				updatedAppointments.add(appointment);
			}
		}

		return updatedAppointments;
	}

	// Read an Appointment by ID
	public Optional<Appointment> getAppointmentById(Long id) {
		return appointmentRepository.findById(id);
	}

	// Read all Appointments
	public List<Appointment> getAllAppointments() {
		return appointmentRepository.findAll();
	}

	// Update an existing Appointment
	public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
		Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(id);

		if (existingAppointmentOpt.isPresent()) {
			Appointment existingAppointment = existingAppointmentOpt.get();

			if (appointmentDetails.getBarber() != null) {
				existingAppointment.setBarber(appointmentDetails.getBarber());
			}
			if (appointmentDetails.getClient() != null) {
				existingAppointment.setClient(appointmentDetails.getClient());
			}
			if (appointmentDetails.getAppointmentTime() != null) {
				existingAppointment.setAppointmentTime(appointmentDetails.getAppointmentTime());
			}

			return appointmentRepository.save(existingAppointment);
		}
		return null; // or throw an exception if preferred
	}

	// Delete an Appointment by ID
	public void deleteAppointment(Long id) {
		appointmentRepository.deleteById(id);
	}

	// Find Appointments by Barber ID
	public List<Appointment> findAppointmentsByBarberId(Long barberId) {
		return appointmentRepository.findByBarber_IdUser(barberId);
	}

	// Find Appointments by Client ID
	public List<Appointment> findAppointmentsByClientId(Long clientId) {
		return appointmentRepository.findByClient_IdUser(clientId);
	}

	// Find Appointments by Date and Time Range
	public List<Appointment> findAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
		return appointmentRepository.findByAppointmentTimeBetween(start, end);
	}
}
