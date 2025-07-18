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
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String fullName;
    @Column(length = 1000)
    private String birthDate;
    @Column(length = 1000)
    private String parentContacts;
    @Column(length = 1000)
    private String childContacts;
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonActivity> personActivities = new ArrayList<>();
    @Column(length = 1000)
    private String allergies;
    @Column(length = 20000)
    private String description;
    private String workshopName;
    private Integer workshopRating;
    //    @ElementCollection
//    @CollectionTable(name = "person_counselors", joinColumns = @JoinColumn(name = "person_id"))
//    @Column(name = "counselor_name",length = 10000)
//    private List<String> counselors;
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonCounselor> counselorLinks = new ArrayList<>();

}
