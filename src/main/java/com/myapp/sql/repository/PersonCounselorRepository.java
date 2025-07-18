package com.myapp.sql.repository;

import com.myapp.sql.entity.PersonCounselor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonCounselorRepository extends JpaRepository<PersonCounselor, Long> {

    boolean existsByPersonIdAndCounselorId(Long personId, Long counselorId);
}
