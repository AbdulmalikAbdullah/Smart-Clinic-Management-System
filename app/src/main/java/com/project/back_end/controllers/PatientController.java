package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;

    @Autowired
    public PatientController(PatientService patientService,
                             Service service,
                             TokenService tokenService,
                             PatientRepository patientRepository) {
        this.patientService = patientService;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
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

        Map<String, Object> response = new HashMap<>();
        response.put("patient", patient.get());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@Valid @RequestBody Patient patient) {
        if (!service.validatePatient(patient.getEmail(), patient.getPhone())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Patient with this email or phone already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Patient created successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Failed to create patient");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{user}/{token}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable long id,
                                                   @PathVariable String user,
                                                   @PathVariable String token) {
        if (!service.validateToken(token, user)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        List<AppointmentDTO> appointments = patientService.getPatientAppointment(id);
        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(@PathVariable String condition,
                                                      @PathVariable String name,
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

        long patientId = patient.get().getId();
        String normalizedCondition = normalizeParam(condition);
        String normalizedName = normalizeParam(name);

        List<AppointmentDTO> appointments;
        if (normalizedCondition != null && normalizedName != null) {
            appointments = patientService.filterByDoctorAndCondition(patientId, normalizedName, normalizedCondition);
        } else if (normalizedCondition != null) {
            appointments = patientService.filterByCondition(patientId, normalizedCondition);
        } else if (normalizedName != null) {
            appointments = patientService.filterByDoctor(patientId, normalizedName);
        } else {
            appointments = patientService.getPatientAppointment(patientId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String normalizeParam(String param) {
        if (param == null || param.isEmpty() || "null".equalsIgnoreCase(param)) {
            return null;
        }
        return param;
    }
}
