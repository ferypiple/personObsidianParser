package com.myapp.sql.repository;

import com.myapp.sql.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    // Метод для проверки по ФИО и дате рождения
    boolean existsByFullNameAndBirthDate(String fullName, String birthDate);
    boolean existsByFullNameAndBirthDateAndYearAndShift(String fullName, String birthDate, Integer year, Integer shift);

}
