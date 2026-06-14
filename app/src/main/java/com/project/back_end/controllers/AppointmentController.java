package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;
    private final DoctorService doctorService;
    private final TokenService tokenService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public AppointmentController(AppointmentService appointmentService,
                                 Service service,
                                 DoctorService doctorService,
                                 TokenService tokenService,
                                 DoctorRepository doctorRepository,
                                 PatientRepository patientRepository) {
        this.appointmentService = appointmentService;
        this.service = service;
        this.doctorService = doctorService;
        this.tokenService = tokenService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String patientName,
                                             @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String email = tokenService.extractEmail(token);
        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (!doctor.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        String normalizedPatientName = normalizeParam(patientName);
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorAndDate(
                doctor.get().getId(), appointmentDate, normalizedPatientName);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(@Valid @RequestBody Appointment appointment,
                                             @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        long doctorId = appointment.getDoctor().getId();
        LocalDate date = appointment.getAppointmentTime().toLocalDate();
        String time = appointment.getAppointmentTime().toLocalTime().toString();

        int validationResult = service.validateAppointment(doctorId, date, time, doctorService);
        if (validationResult == -1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid doctor ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (validationResult == 0) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Appointment slot is not available");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        AppointmentDTO appointmentDTO = toAppointmentDTO(appointment);
        int result = appointmentService.bookAppointment(appointmentDTO);
        if (result == 1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Appointment booked successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Failed to book appointment");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(@Valid @RequestBody Appointment appointment,
                                               @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        AppointmentDTO appointmentDTO = toAppointmentDTO(appointment);
        String result = appointmentService.updateAppointment(appointment.getId(), appointmentDTO);

        if ("Appointment updated successfully".equals(result)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> cancelAppointment(@PathVariable long appointmentId,
                                               @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String email = tokenService.extractEmail(token);
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (!patient.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        String result = appointmentService.cancelAppointment(appointmentId, patient.get().getId());
        if ("Appointment cancelled successfully".equals(result)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private AppointmentDTO toAppointmentDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());
        return dto;
    }

    private String normalizeParam(String param) {
        if (param == null || param.isEmpty() || "null".equalsIgnoreCase(param)) {
            return null;
        }
        return param;
    }
}
