package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class Service {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;

    @Autowired
    public Service(AdminRepository adminRepository, DoctorRepository doctorRepository, 
                   PatientRepository patientRepository, TokenService tokenService) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
    }

    // 3. **validateToken Method**
    public boolean validateToken(String token, String role) {
        try {
            return tokenService.validateToken(token, role);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. **validateAdmin Method**
    public ResponseEntity<?> validateAdmin(Login login) {
        try {
            Optional<Admin> admin = adminRepository.findByUsername(login.getEmail());
            if (!admin.isPresent()) {
                return new ResponseEntity<>("Admin not found", HttpStatus.UNAUTHORIZED);
            }

            if (!admin.get().getPassword().equals(login.getPassword())) {
                return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
            }

            String token = tokenService.generateToken(login.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error during login: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. **filterDoctor Method**
    public List<Doctor> filterDoctor(String name, String specialty, String time, DoctorService doctorService) {
        try {
            if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
                return doctorService.filterDoctorsByNameSpecialtyAndTime(name, specialty, time);
            } else if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty()) {
                return doctorService.filterDoctorByNameAndSpecialty(name, specialty);
            } else if (name != null && !name.isEmpty() && time != null && !time.isEmpty()) {
                return doctorService.filterDoctorByNameAndTime(name, time);
            } else if (specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
                return doctorService.filterDoctorByTimeAndSpecialty(time, specialty);
            } else if (name != null && !name.isEmpty()) {
                return doctorService.findDoctorByName(name);
            } else if (specialty != null && !specialty.isEmpty()) {
                return doctorService.filterDoctorBySpecialty(specialty);
            } else if (time != null && !time.isEmpty()) {
                return doctorService.filterDoctorsByTime(time);
            } else {
                return doctorService.getDoctors();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 6. **validateAppointment Method**
    public int validateAppointment(long doctorId, LocalDate date, String time, DoctorService doctorService) {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(doctorId);
            if (!doctor.isPresent()) {
                return -1; // Doctor not found
            }

            List<java.time.LocalTime> availableSlots = doctorService.getDoctorAvailability(doctorId, date);
            java.time.LocalTime requestedTime = java.time.LocalTime.parse(time);

            if (availableSlots.contains(requestedTime)) {
                return 1; // Valid appointment time
            }
            return 0; // Invalid appointment time
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 7. **validatePatient Method**
    public boolean validatePatient(String email, String phone) {
        try {
            Optional<Patient> patient = patientRepository.findByEmailOrPhone(email, phone);
            return !patient.isPresent();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 8. **validatePatientLogin Method**
    public ResponseEntity<?> validatePatientLogin(Login login) {
        try {
            Optional<Patient> patient = patientRepository.findByEmail(login.getEmail());
            if (!patient.isPresent()) {
                return new ResponseEntity<>("Patient not found", HttpStatus.UNAUTHORIZED);
            }

            if (!patient.get().getPassword().equals(login.getPassword())) {
                return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
            }

            String token = tokenService.generateToken(login.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error during login: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 9. **filterPatient Method** (commented as it requires PatientService which handles filtering)
    // This method would filter patient appointments based on condition and doctor name
    // Implementation would delegate to PatientService methods
}
// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.

// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.

// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method** (commented as it requires PatientService which handles filtering)
// This method would filter patient appointments based on condition and doctor name
// Implementation would delegate to PatientService methods

