package com.myapp.sql.repository;

import com.myapp.sql.entity.PersonActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonActivityRepository extends JpaRepository<PersonActivity, Long> {}