package com.myapp.sql.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "person_tags")
public class PersonTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // дополнительные поля, если нужно (например, дата назначения)
}
