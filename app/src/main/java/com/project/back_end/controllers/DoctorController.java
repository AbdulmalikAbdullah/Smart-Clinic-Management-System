package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
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

@RestController
@RequestMapping({"${api.path}doctor", "${api.path}doctors"})
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;
    private final TokenService tokenService;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service, TokenService tokenService) {
        this.doctorService = doctorService;
        this.service = service;
        this.tokenService = tokenService;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user,
                                                 @PathVariable long doctorId,
                                                 @PathVariable String date,
                                                 @PathVariable String token) {
        if (!service.validateToken(token, user)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        List<?> availability = doctorService.getDoctorAvailability(doctorId, appointmentDate);

        Map<String, Object> response = new HashMap<>();
        response.put("availability", availability);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<?> getDoctor() {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(@Valid @RequestBody Doctor doctor,
                                        @PathVariable String token) {
        if (!service.validateToken(token, "admin")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == -1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (result == 1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor saved successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Failed to save doctor");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@Valid @RequestBody Login login) {
        Doctor doctor = doctorService.validateDoctorLogin(login.getEmail(), login.getPassword());
        if (doctor == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(login.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login successful");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(@Valid @RequestBody Doctor doctor,
                                          @PathVariable String token) {
        if (!service.validateToken(token, "admin")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.updateDoctor(doctor.getId(), doctor);
        if (result == -1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (result == 1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Failed to update doctor");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/{doctorId}/{token}")
    public ResponseEntity<?> deleteDoctor(@PathVariable long doctorId,
                                          @PathVariable String token) {
        if (!service.validateToken(token, "admin")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.deleteDoctor(doctorId);
        if (result == -1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (result == 1) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Failed to delete doctor");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<?> filter(@PathVariable String name,
                                    @PathVariable String time,
                                    @PathVariable String speciality) {
        List<Doctor> doctors = service.filterDoctor(
                normalizeParam(name),
                normalizeParam(speciality),
                normalizeParam(time),
                doctorService);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String normalizeParam(String param) {
        if (param == null || param.isEmpty() || "null".equalsIgnoreCase(param)) {
            return null;
        }
        return param;
    }
}
