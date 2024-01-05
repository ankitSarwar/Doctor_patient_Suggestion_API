package com.example.DoctorAppointment.controller;

import com.example.DoctorAppointment.dto.DoctorSuggestionResponse;
import com.example.DoctorAppointment.dto.ErrorResponse;
import com.example.DoctorAppointment.model.*;
import com.example.DoctorAppointment.service.DoctorService;
import com.example.DoctorAppointment.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class DoctorSuggestionController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;


    @GetMapping("/suggestions/{patientId}")
    public ResponseEntity<?> suggestDoctorsForPatient(@PathVariable Long patientId) {
        Optional<Patient> patientOptional = patientService.findPatientById(patientId);
        if (patientOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Patient patient = patientOptional.get();
        City patientCity = City.valueOf(patient.getCity());
        Symptom patientSymptom = patient.getSymptom();

        // Check if the patient's city is within the supported locations
        if (!SupportedCity(patientCity) ) {
            return ResponseEntity.ok().body("We are still waiting to expand to your location");
        }
        if(!isSupportedCity(patientCity)){
            return ResponseEntity.ok().body("There isn’t any doctor present at your location for your symptom");
        }
        // Query the database to check if there are doctors available in the specified city
        List<Doctor> doctorsInCity = doctorService.getDoctorsByCity(patientCity);

        if (doctorsInCity.isEmpty()) {
            // If no doctors are found, return the response in string format
            return ResponseEntity.ok().body("No doctor found in your location");
        }
        Speciality patientSpeciality = getSpecialityBySymptom(patientSymptom);
        if (patientSpeciality == null) {
            return ResponseEntity.badRequest().body("There isn’t any doctor present at your location for your symptom");
        }

        List<Doctor> suggestedDoctors = doctorService.getDoctorsByLocationAndSpeciality(patientCity, patientSpeciality);

        if (suggestedDoctors.isEmpty()) {
            // Handle the case when no doctors are found
            return ResponseEntity.ok().body("No doctor found for this symptom on your location");
        } else {
            return ResponseEntity.ok().body(suggestedDoctors);
        }
    }
    private boolean SupportedCity(City city) {
        // Check if the city is within the supported locations
        List<City> supportedCities = List.of(City.DELHI, City.NOIDA, City.FARIDABAD);
        if (!supportedCities.contains(city)) {
            return false;
        }
        return true;
    }

    private boolean isSupportedCity(City city) {
        List<Doctor> doctorsInCity = doctorService.getDoctorsByCity(city);

        return !doctorsInCity.isEmpty();
    }

    private Speciality getSpecialityBySymptom(Symptom symptom) {
        if (symptom == Symptom.ARTHRITIS || symptom == Symptom.BACK_PAIN || symptom == Symptom.TISSUE_INJURIES) {
            return Speciality.ORTHOPEDIC;
        } else if (symptom == Symptom.DYSMENORRHEA) {
            return Speciality.GYNECOLOGY;
        } else if (symptom == Symptom.SKIN_INFECTION || symptom == Symptom.SKIN_BURN) {
            return Speciality.DERMATOLOGY;
        } else if (symptom == Symptom.EAR_PAIN) {
            return Speciality.ENT_SPECIALIST;
        } else {
            return null;
        }
    }
}
