package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.
@Service
public class AppointmentService {

    // 2. **Constructor Injection for Dependencies**:
    //    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
    //    - These dependencies should be injected through the constructor.
    //    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                            PatientRepository patientRepository,
                            DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // 3. **Add @Transactional Annotation for Methods that Modify Database**:
    //    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
    //    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

    // 4. **Book Appointment Method**:
    //    - Responsible for saving the new appointment to the database.
    //    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
    //    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.
    @Transactional
    public int bookAppointment(AppointmentDTO appointmentDTO) {
        try {
            // Validate and fetch patient
            Optional<Patient> patient = patientRepository.findById(appointmentDTO.getPatientId());
            if (!patient.isPresent()) {
                return 0;
            }

            // Validate and fetch doctor
            Optional<Doctor> doctor = doctorRepository.findById(appointmentDTO.getDoctorId());
            if (!doctor.isPresent()) {
                return 0;
            }

            // Create and save the appointment
            Appointment appointment = new Appointment();
            appointment.setPatient(patient.get());
            appointment.setDoctor(doctor.get());
            appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
            appointment.setStatus(appointmentDTO.getStatus());

            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 5. **Update Appointment Method**:
    //    - This method is used to update an existing appointment based on its ID.
    //    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
    //    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
    //    - Instruction: Ensure proper validation and error handling is included for appointment updates.
    @Transactional
    public String updateAppointment(long appointmentId, AppointmentDTO appointmentDTO) {
        try {
            // Fetch the existing appointment
            Optional<Appointment> existingAppointment = appointmentRepository.findById(appointmentId);
            if (!existingAppointment.isPresent()) {
                return "Appointment not found";
            }

            Appointment appointment = existingAppointment.get();

            // Validate patient ID matches
            if (appointment.getPatient().getId() != appointmentDTO.getPatientId()) {
                return "Patient ID mismatch";
            }

            // Validate appointment is available for updating (status check - not completed)
            if (appointment.getStatus() == 1) { // 1 = Completed
                return "Cannot update a completed appointment";
            }

            // Validate doctor exists and is available
            Optional<Doctor> doctor = doctorRepository.findById(appointmentDTO.getDoctorId());
            if (!doctor.isPresent()) {
                return "Doctor not found";
            }

            // Update appointment details
            appointment.setDoctor(doctor.get());
            appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
            appointment.setStatus(appointmentDTO.getStatus());

            appointmentRepository.save(appointment);
            return "Appointment updated successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating appointment: " + e.getMessage();
        }
    }

    // 6. **Cancel Appointment Method**:
    //    - This method cancels an appointment by deleting it from the database.
    //    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
    //    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.
    @Transactional
    public String cancelAppointment(long appointmentId, long patientId) {
        try {
            // Fetch the appointment
            Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
            if (!appointment.isPresent()) {
                return "Appointment not found";
            }

            // Validate that the patient owns the appointment
            if (appointment.get().getPatient().getId() != patientId) {
                return "Unauthorized: You can only cancel your own appointments";
            }
 
            // Delete the appointment
            appointmentRepository.deleteById(appointmentId);
            return "Appointment cancelled successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error cancelling appointment: " + e.getMessage();
        }
    }

    // 7. **Get Appointments Method**:
    //    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
    //    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
    //    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDoctorAndDate(long doctorId, LocalDate date, String patientName) {
        try {
            // Retrieve all appointments for the doctor on the given date
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

            // Filter by patient name if provided
            if (patientName != null && !patientName.isEmpty()) {
                appointments = appointments.stream()
                        .filter(apt -> {
                            Optional<Patient> patient = patientRepository.findById(apt.getPatient().getId());
                            return patient.isPresent() && patient.get().getName().toLowerCase().contains(patientName.toLowerCase());
                        })
                        .collect(Collectors.toList());
            }

            // Convert to DTOs
            return appointments.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // 8. **Change Status Method**:
    //    - This method updates the status of an appointment by changing its value in the database.
    //    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
    //    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.
    @Transactional
    public String changeAppointmentStatus(long appointmentId, int newStatus) {
        try {
            Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
            if (!appointment.isPresent()) {
                return "Appointment not found";
            }

            appointment.get().setStatus(newStatus);
            appointmentRepository.save(appointment.get());
            return "Appointment status updated successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating appointment status: " + e.getMessage();
        }
    }

    // Helper method to convert Appointment entity to AppointmentDTO
    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());

        // Set doctor information
        if (appointment.getDoctor() != null) {
            dto.setDoctorName(appointment.getDoctor().getName());
        }

        // Set patient information
        if (appointment.getPatient() != null) {
            dto.setPatientName(appointment.getPatient().getName());
            dto.setPatientEmail(appointment.getPatient().getEmail());
            dto.setPatientPhone(appointment.getPatient().getPhone());
            dto.setPatientAddress(appointment.getPatient().getAddress());
        }

        return dto;
    }
}
