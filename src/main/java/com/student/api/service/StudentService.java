package com.student.api.service;

import java.util.List;

import com.student.api.dto.StudentDTO;
import com.student.api.request.StudentRequest;

public interface StudentService {
	
	String addStudent(StudentRequest student);
	
	List<StudentDTO> findAllStudentDetails();
	
	StudentDTO findStudentDetailsByRollNumber(int rollNo);
	
	String updateStudentDetailsByRollNumber(int rollNo,StudentRequest student);
	
	String deleteStudentByRollNumber(int rollNo);
	
	String deleteAllStudent();
	
}
