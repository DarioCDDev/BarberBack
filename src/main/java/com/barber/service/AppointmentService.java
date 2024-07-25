package com.barber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barber.entities.Appointment;
import com.barber.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;

    // Create a new Appointment
    public Appointment createAppointment(Appointment appointment) {
    	System.out.println(appointment);
        return appointmentRepository.save(appointment);
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
