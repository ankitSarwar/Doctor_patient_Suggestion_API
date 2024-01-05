package com.example.DoctorAppointment.controller;

import com.example.DoctorAppointment.model.Doctor;
import com.example.DoctorAppointment.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
// http://localhost:8888/swagger-ui/index.html#/patient-controller/removePatient

    @Autowired
    DoctorService doctorService;

    @PostMapping("/add")
    public ResponseEntity<Doctor> addDoctor(@Valid @RequestBody Doctor doctor) {
        Doctor createdDoctor = doctorService.addDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
    }

    @DeleteMapping("delete/{doctorId}")
    public ResponseEntity<String> removeDoctor(@PathVariable Long doctorId) {
        try {
            doctorService.removeDoctor(doctorId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Handle the case where the doctor with the given ID is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found that ID");
        }
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok().body(doctors);
    }
}