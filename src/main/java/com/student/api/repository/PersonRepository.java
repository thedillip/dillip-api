package com.student.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.student.api.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {

}
