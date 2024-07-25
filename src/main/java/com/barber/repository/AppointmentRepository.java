package com.barber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.barber.entities.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	List<Appointment> findByBarber_IdUser(Long barberId);

	List<Appointment> findByClient_IdUser(Long clientId);

	List<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

}
