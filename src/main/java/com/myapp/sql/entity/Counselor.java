package com.myapp.sql.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"fullName", "birthDate"})}
)
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
    @OneToMany(mappedBy = "counselor")
    private List<PersonCounselor> personLinks = new ArrayList<>();

}