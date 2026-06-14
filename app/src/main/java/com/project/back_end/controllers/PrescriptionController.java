package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(@Valid @RequestBody Prescription prescription,
                                              @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        appointmentService.changeAppointmentStatus(prescription.getAppointmentId(), 1);
        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable long appointmentId,
                                             @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ResponseEntity<?> serviceResponse = prescriptionService.getPrescription(appointmentId);
        if (serviceResponse.getStatusCode() == HttpStatus.OK && serviceResponse.getBody() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) serviceResponse.getBody();
            if (body.containsKey("prescriptions")) {
                Map<String, Object> response = new HashMap<>();
                response.put("prescription", body.get("prescriptions"));
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return serviceResponse;
    }
}
