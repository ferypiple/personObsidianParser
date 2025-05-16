package com.myapp.sql.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String birthDate;
    private String parentContacts;
    private String childContacts;
    private String allergies;
    @Column(length = 20000)
    private String description;
    @Column(length = 10000)
    private String tags;
    @ElementCollection
    @CollectionTable(name = "person_counselors", joinColumns = @JoinColumn(name = "person_id"))
    @Column(name = "counselor_name")
    private List<String> counselors;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonTag> personTags = new ArrayList<>();
}