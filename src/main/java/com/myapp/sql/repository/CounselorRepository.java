package com.myapp.sql.repository;

import com.myapp.sql.entity.Counselor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface CounselorRepository extends JpaRepository<Counselor, Long> {
    Optional<Counselor> findByFullName(String fullName);

    @Query("SELECT c FROM Counselor c WHERE " +
           "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Counselor> search(@Param("query") String query);
}