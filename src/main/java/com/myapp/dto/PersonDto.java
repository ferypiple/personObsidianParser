package com.myapp.dto;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PersonDto {
    private String fullName;
    private String birthDate;
    private String parentContacts;
    private String childContacts;
    private List<String> allergies;
    private String description;
    private List<String> counselors;
    private List<String> activities;
    private String workshopName;     // Название мастерской (например, "Музыка")
    private Integer workshopRating;  // Оценка мастерской (например, 10)
    private Map<String, List<Integer>> counselorRatings = new HashMap<>();

}