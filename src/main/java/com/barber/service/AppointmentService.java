package com.barber.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barber.dto.BarberAppointmentDTO;
import com.barber.dto.ServiceDTO;
import com.barber.dto.StatusDTO;
import com.barber.dto.UserDTO;
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
	@Autowired
	private JavaMailSender mailSender;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy 'a las' HH:mm", new Locale("es", "ES"));

	public ResponseEntity<?> createAppointment(Appointment appointment, Long statusId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LocalDateTime now = LocalDateTime.now();
			if (appointment.getAppointmentTime().isBefore(now)) {
				response.put("message", "Error, esa hora ya ha pasado");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// Verificar disponibilidad de la hora de la cita
//			List<Appointment> allAppointments = appointmentRepository.findAll();
//			for (Appointment existingAppointment : allAppointments) {
//				if (appointment.getAppointmentTime().equals(existingAppointment.getAppointmentTime())) {
//					response.put("message", "Error, esa hora ya está ocupada");
//					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//				}
//			}

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
			SimpleMailMessage message = new SimpleMailMessage();
			
			// Formatear la fecha y hora de la cita en español
			String formattedDateTime = appointment.getAppointmentTime().format(formatter);

			// Mensaje para el barbero
			message.setTo(barber.getEmail());
			message.setSubject("Nueva cita");
			String barberMessage = "Nueva cita con el cliente: " + appointment.getClient().getName() + ", a fecha de: " + formattedDateTime;
			message.setText(barberMessage);
			mailSender.send(message);

			// Mensaje para el cliente
			message.setTo(appointment.getClient().getEmail());
			message.setSubject("Nueva cita");
			String clientMessage = "Cita creada con éxito con el barbero: " + barber.getName() + ", a fecha de: " + formattedDateTime;
			message.setText(clientMessage);
			mailSender.send(message);
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
		System.out.println(id);
		System.out.println(appointmentDetails.getStatus());
		Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(id);
		System.out.println(existingAppointmentOpt.isPresent());
		SimpleMailMessage message = new SimpleMailMessage();

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
			if (appointmentDetails.getStatus() != null) {
				existingAppointment.setStatus(appointmentDetails.getStatus());
				if (appointmentDetails.getStatus().getIdStatus() == 2l) {
					String formattedDateTime = existingAppointment.getAppointmentTime().format(formatter);
					message.setTo(existingAppointment.getBarber().getEmail());
					message.setSubject("Cita cancelada");
					String barberMessage = "Estimado/a " + existingAppointment.getBarber().getName()+ ",\n\n"
	                         + "Le informamos que el cliente " + existingAppointment.getClient().getName() + " ha cancelado la cita programada para el " + formattedDateTime + ".\n\n"
	                         + "Saludos cordiales,\n"
	                         + "El equipo de [Nombre del Barbería]";
					message.setText(barberMessage);
					mailSender.send(message);
				}
				if (appointmentDetails.getStatus().getIdStatus() == 3l) {
					// Formatear la fecha y hora de la cita en español
					String formattedDateTime = existingAppointment.getAppointmentTime().format(formatter);
					// Mensaje para el barbero
					message.setTo(existingAppointment.getBarber().getEmail());
					message.setSubject("Cita marcada como hecha automaticamnte");
					String barberMessage = "Estimado/a "+ existingAppointment.getBarber().getName()+",\n\n"
	                         + "La siguiente cita ha sido marcada como 'Hecha' automáticamente:\n\n"
	                         + "Cliente: " + existingAppointment.getClient().getName() + "\n"
	                         + "Fecha y hora: " + formattedDateTime + "\n\n"
	                         + "Por favor, confirme la cita manualmente en el sistema para proceder a marcarla como 'Completada'.\n\n"
	                         + "Saludos cordiales,\n"
	                         + "El equipo de [Nombre del Barbería]";
					message.setText(barberMessage);
					mailSender.send(message);
				}
				if (appointmentDetails.getStatus().getIdStatus() == 4l) {
				    // Formatear la fecha y hora de la cita en español
				    String formattedDateTime = existingAppointment.getAppointmentTime().format(formatter);
				    
				    // Mensaje para el cliente
				    message.setTo(existingAppointment.getClient().getEmail());
				    message.setSubject("¡Cita completada con éxito!");
				    
				    // Crear mensaje más formal y amable
				    String barberMessage = "Estimado/a " + existingAppointment.getClient().getName() + ",\n\n"
				                         + "Nos complace informarle que su cita el " + formattedDateTime + " se ha completado exitosamente.\n\n"
				                         + "Esperamos que haya tenido una experiencia agradable con nosotros. Le invitamos a agendar su próxima cita en cualquier momento que lo desee.\n\n"
				                         + "Además, si su experiencia fue satisfactoria, nos encantaría que compartiera su opinión dejando una reseña en el siguiente enlace: [aquí iría el sitio de la reseña].\n\n"
				                         + "¡Gracias por confiar en nosotros!\n"
				                         + "Saludos cordiales,\n"
				                         + "El equipo de [Nombre del Barbería]";
				    
				    message.setText(barberMessage);
				    mailSender.send(message);
				}
				
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
	public List<BarberAppointmentDTO> findAppointmentsByBarberId(Long barberId) {
	    return appointmentRepository.findByBarber_IdUser(barberId)
	        .stream()
	        .map(appointment -> {
	            BarberAppointmentDTO dto = new BarberAppointmentDTO();

	            // Mapeo de la cita (Appointment)
	            dto.setIdAppointment(appointment.getIdAppointment());

	            // Mapeo del Barbero
	            UserDTO barberDTO = new UserDTO();
	            barberDTO.setUserId(appointment.getBarber().getIdUser());
	            barberDTO.setName(appointment.getBarber().getName());
	            barberDTO.setEmail(appointment.getBarber().getEmail());
	            barberDTO.setPhone(appointment.getBarber().getPhone());
	            barberDTO.setPhoto(appointment.getBarber().getPhoto());
	            dto.setBarber(barberDTO);

	            // Mapeo del Cliente
	            UserDTO clientDTO = new UserDTO();
	            clientDTO.setUserId(appointment.getClient().getIdUser());
	            clientDTO.setName(appointment.getClient().getName());
	            clientDTO.setEmail(appointment.getClient().getEmail());
	            clientDTO.setPhone(appointment.getClient().getPhone());
	            clientDTO.setPhoto(appointment.getClient().getPhoto());
	            dto.setClient(clientDTO);

	            // Mapeo del appointmentTime
	            dto.setAppointmentTime(appointment.getAppointmentTime());

	            // Mapeo del Estado (Status)
	            StatusDTO statusDTO = new StatusDTO();
	            statusDTO.setIdStatus(appointment.getStatus().getIdStatus());
	            statusDTO.setName(appointment.getStatus().getName());
	            dto.setStatus(statusDTO);

	            // Mapeo del Servicio (Service)
	            ServiceDTO serviceDTO = new ServiceDTO();
	            serviceDTO.setIdService(appointment.getService().getIdService());
	            serviceDTO.setName(appointment.getService().getName());
	            serviceDTO.setPrice(appointment.getService().getPrice());
	            dto.setService(serviceDTO);

	            // Mapeo de comentarios
	            dto.setComments(appointment.getComments());

	            return dto;
	        })
	        .collect(Collectors.toList());
	}
	
	public List<Appointment> findAppointmentsByBarberIdAndStatus(Long barberId, Long statusId) {
		return appointmentRepository.findByBarber_IdUserAndStatus_IdStatus(barberId, statusId);
	}

	// Find Appointments by Client ID
	public List<Appointment> findAppointmentsByClientId(Long clientId) {
        List<Appointment> appointments = appointmentRepository.findByClient_IdUser(clientId);
        
        return appointments.stream()
            .sorted(Comparator.comparing((Appointment a) -> a.getStatus().getIdStatus() != 1)
                .thenComparing(Appointment::getAppointmentTime))
            .limit(10)
            .collect(Collectors.toList());
    }

	public List<Appointment> findAppointmentsByClientIdAndStatus(Long clientId, Long statusId) {
		return appointmentRepository.findByClient_IdUserAndStatus_IdStatus(clientId, statusId);
	}

	// Find Appointments by Date and Time Range
	public List<Appointment> findAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
		return appointmentRepository.findByAppointmentTimeBetween(start, end);
	}

	public ResponseEntity<AvailabilityResponse> getAvailabilityForBarber(Long barberId) {
	    // Obtener el barbero con el horario en una sola consulta
	    Optional<User> barberOpt = userRepository.findById(barberId);
	    if (barberOpt.isEmpty() || barberOpt.get().getSchedule() == null) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }

	    Schedule barberSchedule = barberOpt.get().getSchedule();
	    LocalDate today = LocalDate.now();
	    LocalDate endDate = today.plusMonths(2);

	    // Pre-cargar citas del barbero para evitar múltiples consultas dentro del bucle
	    List<Appointment> barberAppointments = appointmentRepository.findByBarber_IdUser(barberId);
	    Set<LocalDateTime> unavailableSlots = barberAppointments.stream()
	        .filter(appointment -> appointment.getStatus().getIdStatus().equals(1L) || appointment.getStatus().getIdStatus().equals(4L))
	        .map(Appointment::getAppointmentTime)
	        .collect(Collectors.toSet());

	    Map<String, List<Map<String, String>>> availability = new LinkedHashMap<>();

	    // Iterar desde hoy hasta dentro de 2 meses
	    for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
	        String dayOfWeek = date.getDayOfWeek().toString();
	        String normalizedDayOfWeek = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1).toLowerCase();

	        List<TimeInterval> timeIntervals = barberSchedule.getWeeklySchedule().get(normalizedDayOfWeek);
	        if (timeIntervals != null) {
	            List<Map<String, String>> dailyAvailability = new ArrayList<>();
	            for (TimeInterval interval : timeIntervals) {
	                LocalTime startTime = LocalTime.parse(interval.getStartTime());
	                LocalTime endTime = LocalTime.parse(interval.getEndTime());

	                // Verificar intervalos de 30 minutos y añadir estado
	                for (LocalTime slot = startTime; slot.isBefore(endTime); slot = slot.plusMinutes(30)) {
	                    LocalDateTime slotDateTime = LocalDateTime.of(date, slot);
	                    String status = unavailableSlots.contains(slotDateTime) ? "not available" : "available";

	                    Map<String, String> timeStatus = new HashMap<>();
	                    timeStatus.put("time", slot.format(DateTimeFormatter.ofPattern("HH:mm")));
	                    timeStatus.put("status", status);
	                    dailyAvailability.add(timeStatus);
	                }
	            }
	            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	            availability.put(dateStr, dailyAvailability);
	        }
	    }

	    // Convertir las fechas a lista ordenada para la respuesta
	    List<String> dateTimestamps = new ArrayList<>(availability.keySet());

	    AvailabilityResponse response = new AvailabilityResponse(dateTimestamps, availability);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}



}
