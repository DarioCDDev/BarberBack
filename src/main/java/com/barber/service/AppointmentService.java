package com.barber.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barber.entities.Appointment;
import com.barber.entities.AvailabilityResponse;
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
	private StatusRepository statusRepository;
	
	@Autowired
	private UserRepository userRepository;

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
			if (appointment.getAppointmentTime().isBefore(now)) {
				Status appointmentStatus = appointment.getStatus();
				if (appointmentStatus != null && appointmentStatus.getIdStatus() == 1L) {
					appointment.setStatus(status);
					appointmentRepository.save(appointment);
					updatedAppointments.add(appointment);
				} else if (appointmentStatus == null) {
					// Manejo del caso en que el status de la cita es null
					System.out.println("El status de la cita es nulo");
				}
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

	public List<Appointment> findAppointmentsByBarberIdAndStatus(Long barberId, Long statusId) {
		return appointmentRepository.findByBarber_IdUserAndStatus_IdStatus(barberId, statusId);
	}

	// Find Appointments by Client ID
	public List<Appointment> findAppointmentsByClientId(Long clientId) {
		return appointmentRepository.findByClient_IdUser(clientId);
	}

	public List<Appointment> findAppointmentsByClientIdAndStatus(Long clientId, Long statusId) {
		return appointmentRepository.findByClient_IdUserAndStatus_IdStatus(clientId, statusId);
	}

	// Find Appointments by Date and Time Range
	public List<Appointment> findAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
		return appointmentRepository.findByAppointmentTimeBetween(start, end);
	}

	public ResponseEntity<AvailabilityResponse> getAvailabilityForBarber(Long barberId) {
		
		User barber = userRepository.findById(barberId).get();
		if (barber == null || barber.getSchedule() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Schedule barberSchedule = barber.getSchedule();
        Set<LocalTime> uniqueAvailableTimes = new HashSet<>();
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(2);

        // Iterar desde hoy hasta dentro de 2 meses
        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dayOfWeek = date.getDayOfWeek().toString();
            String normalizedDayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();

            List<TimeInterval> timeIntervals = barberSchedule.getWeeklySchedule().get(normalizedDayOfWeek);

            if (timeIntervals != null) {
                for (TimeInterval interval : timeIntervals) {
                    LocalTime startTime = LocalTime.parse(interval.getStartTime());
                    LocalTime endTime = LocalTime.parse(interval.getEndTime());
                    
                    // Añadir horas únicas a la lista (asumiendo intervalos de 30 minutos)
                    LocalTime slot = startTime;
                    while (slot.isBefore(endTime)) {
                        uniqueAvailableTimes.add(slot);
                        slot = slot.plusMinutes(30);
                    }
                }
            }
        }

        // Convertir a las listas requeridas para el objeto de respuesta
        List<String> dateTimestamps = generateDateTimestamps(today, endDate);
        List<String> availableTimes = uniqueAvailableTimes.stream()
                .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                .sorted()
                .collect(Collectors.toList());

        AvailabilityResponse response = new AvailabilityResponse(dateTimestamps, availableTimes);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private List<String> generateDateTimestamps(LocalDate start, LocalDate end) {
    	List<String> dateStrings = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            dateStrings.add(date.format(formatter));
        }
        return dateStrings;
    }

}
