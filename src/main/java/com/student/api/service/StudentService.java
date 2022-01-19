package com.student.api.service;

import java.util.List;
import com.student.api.entity.Student;
import com.student.api.request.StudentRequest;
import com.student.api.response.StudentResponse;

public interface StudentService {
	
	public Student addStudent(StudentRequest student);
	
	public StudentResponse deleteStudentById(int rollNo);
	
	public StudentResponse updateStudentById(int rollNo,StudentRequest student);
	
	public List<Student> findAll();
	
	public List<Student> findById(int rollNo);
}
