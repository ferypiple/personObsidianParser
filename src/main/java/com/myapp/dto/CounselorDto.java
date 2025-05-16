package com.myapp.dto;
import lombok.Data;

@Data
public class CounselorDto {
    private String fullName;
    private String description;
    private String courseStats;
    private String feedback;
    private String birthDate;          // добавили
    private String contacts;           // добавили
    private String squadInfo;          // «Работа с отрядом»
}