package com.example.DoctorAppointment.dto;

import com.example.DoctorAppointment.model.Doctor;
import lombok.Data;

import java.util.List;

@Data
public class DoctorSuggestionResponse {
    private List<Doctor> doctors;
    private List<String> errorMessages;

    public DoctorSuggestionResponse(List<Doctor> doctors, List<String> errorMessages) {
        this.doctors = doctors;
        this.errorMessages = errorMessages;
        System.out.println(errorMessages);
    }

}