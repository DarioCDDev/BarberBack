package com.barber.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.barber.dto.AppointmentDTO;
import com.barber.dto.CreateAppointmentRequest;
import com.barber.entities.Appointment;
import com.barber.entities.User;
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
        User barber = userService.findUserById(appointmentRequest.getAppointment().getBarberId());
        User client = userService.findUserById(appointmentRequest.getAppointment().getClientId());
        Appointment appointment = new Appointment(barber, client, appointmentRequest.getAppointment().getAppointmentTime());
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

    @GetMapping("/appointments/barber/{barberId}")
    public List<Appointment> findAppointmentsByBarberId(@PathVariable Long barberId) {
        return appointmentService.findAppointmentsByBarberId(barberId);
    }

    @GetMapping("/appointments/client/{clientId}")
    public List<Appointment> findAppointmentsByClientId(@PathVariable Long clientId) {
        return appointmentService.findAppointmentsByClientId(clientId);
    }

    @GetMapping("/appointments/date-range")
    public List<Appointment> findAppointmentsByDateRange(
        @RequestParam("start") LocalDateTime start,
        @RequestParam("end") LocalDateTime end) {
        return appointmentService.findAppointmentsByDateRange(start, end);
    }
}
