package com.student.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.student.api.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
	public List<Student> findByRollNo(int rollNo);
}
