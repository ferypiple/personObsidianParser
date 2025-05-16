package com.myapp.sql.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Counselor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    @Column(length = 10000)
    private String courseStats;
    private String birthDate;
    private String contacts;

    @Column(length = 100000)
    private String description;
    @Column(length = 10000)
    private String feedback;
    @Column(length = 10000)
    private String squadInfo;
}