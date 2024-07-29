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
import com.barber.entities.Status;
import com.barber.entities.TimeInterval;
import com.barber.entities.User;
import com.barber.repository.RolRepository;
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
		Status pasadoStatus = new Status(3l, "Pasado");

		statusRepository.save(activoStatus);
		statusRepository.save(eliminadoStatus);
		statusRepository.save(pasadoStatus);
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

		User barber = new User(1l, "Barber", "666323254", "barber@gamil.com", "$2a$10$tqGiMZ8tuv3AkNGmbxe08e7Uw7GDHZkRbrUQL9ex8QiPWYESg.uXO", barberRol, schedule);

		User cliente = new User(2l, "Dario", "663580414", "dariocd0808@gamil.com","$2a$10$tqGiMZ8tuv3AkNGmbxe08e7Uw7GDHZkRbrUQL9ex8QiPWYESg.uXO", clienteRol);

		userRepository.save(barber);
		userRepository.save(cliente);

	}

}
