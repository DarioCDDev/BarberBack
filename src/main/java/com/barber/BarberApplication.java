package com.barber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.barber.entities.Rol;
import com.barber.entities.Schedule;
import com.barber.entities.Service;
import com.barber.entities.Status;
import com.barber.entities.TimeInterval;
import com.barber.entities.User;
import com.barber.repository.RolRepository;
import com.barber.repository.ServiceRepository;
import com.barber.repository.StatusRepository;
import com.barber.repository.UserRepository;

@SpringBootApplication
@EnableScheduling
public class BarberApplication implements CommandLineRunner {

	@Autowired
	private RolRepository rolRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ServiceRepository serviceRepository;


	public static void main(String[] args) {
		SpringApplication.run(BarberApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {

		Rol barberRol = new Rol(1l, "Barbero");
		Rol clienteRol = new Rol(2l, "Cliente");

		rolRepository.save(barberRol);
		rolRepository.save(clienteRol);

		Status activoStatus = new Status(1l, "Activo");
		Status eliminadoStatus = new Status(2l, "Eliminado");
		Status pasadoStatus = new Status(3l, "Hecha");
		Status DoneStatus = new Status(4l, "Completada");

		statusRepository.save(activoStatus);
		statusRepository.save(eliminadoStatus);
		statusRepository.save(pasadoStatus);
		statusRepository.save(DoneStatus);
		
		Service corteNormal = new Service(1l, "Corte de pelo", 12f);
		Service corteBarba = new Service(2l, "Corte de barba", 10f);
		Service corteNormalBarba = new Service(3l, "Corte de pelo y barba", 16f);
		
		serviceRepository.save(corteNormal);
		serviceRepository.save(corteBarba);
		serviceRepository.save(corteNormalBarba);
		
		TimeInterval interval1 = new TimeInterval("09:00", "13:00");
		TimeInterval interval2 = new TimeInterval("15:00", "21:00");

		// Crear la lista de intervalos para cada día de la semana
		List<TimeInterval> mondayIntervals = Arrays.asList(interval1, interval2);
		List<TimeInterval> tuesdayIntervals = Arrays.asList(interval1, interval2);
		List<TimeInterval> wednesdayIntervals = Arrays.asList(interval1, interval2);
		List<TimeInterval> thursdayIntervals = Arrays.asList(interval1, interval2);
		List<TimeInterval> fridayIntervals = Arrays.asList(interval1, interval2);

		// Crear el mapa para el horario semanal
		Map<String, List<TimeInterval>> weeklySchedule = new HashMap<>();
		weeklySchedule.put("Monday", mondayIntervals);
		weeklySchedule.put("Tuesday", tuesdayIntervals);
		weeklySchedule.put("Wednesday", wednesdayIntervals);
		weeklySchedule.put("Thursday", thursdayIntervals);
		weeklySchedule.put("Friday", fridayIntervals);

		Schedule schedule = new Schedule(weeklySchedule);
		
		User barber1 = new User(1l, "Barbero 1", "663580414141", "dariocd0808@gmail.com", "$2a$10$zbgTKjwn3iEKH4jntXLfL.KpNHYyk4mnuHEhYbU9Pej6afhkLx4wK", barberRol, schedule);
		barber1.setVerified(true);
		User barber2 = new User(2l, "Barbero 2", "663580414142", "barbero2@gmail.com", "$2a$10$zbgTKjwn3iEKH4jntXLfL.KpNHYyk4mnuHEhYbU9Pej6afhkLx4wK", barberRol, schedule);
		barber2.setVerified(true);
		User barber3 = new User(3l, "Barbero 3", "663580414143", "barbero3@gmail.com", "$2a$10$zbgTKjwn3iEKH4jntXLfL.KpNHYyk4mnuHEhYbU9Pej6afhkLx4wK", barberRol, schedule);
		barber3.setVerified(true);
		User barber4 = new User(4l, "Barbero 4", "663580414144", "barbero4@gmail.com", "$2a$10$zbgTKjwn3iEKH4jntXLfL.KpNHYyk4mnuHEhYbU9Pej6afhkLx4wK", barberRol, schedule);
		barber4.setVerified(true);
		
		userRepository.save(barber1);
		userRepository.save(barber2);
		userRepository.save(barber3);
		userRepository.save(barber4);

		
	}

}
