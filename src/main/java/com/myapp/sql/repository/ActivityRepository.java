package com.myapp.sql.repository;

import com.myapp.sql.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByName(String name);
    @Query("SELECT a FROM Activity a WHERE LOWER(a.name) = LOWER(:name)")
    Optional<Activity> findByNameIgnoreCase(@Param("name") String name);

}
