package com.myapp.sql.repository;

import com.myapp.sql.entity.PersonTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonTagRepository extends JpaRepository<PersonTag, Long> {
    // при необходимости
}