package com.student.api.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.student.api.entity.Student;
import com.student.api.repository.StudentRepository;
import com.student.api.request.StudentRequest;
import com.student.api.response.StudentResponse;



@Service
public class StudentServiceImpl implements StudentService {
	
	@Autowired
	private StudentRepository repository;

	@Override
	public List<Student> findAll() {
		List<Student> reStudent = repository.findAll();
		return reStudent;
	}

	@Override
	public StudentResponse deleteStudentById(int rollNo) {
		StudentResponse response = new StudentResponse();
		
		List<Student> list = repository.findByRollNo(rollNo);
		
		if(list.size() == 0)
		{
			response.setMessage("Student Record is not present with this Roll Number "+rollNo);
		}
		else {
			repository.deleteById(rollNo);
			response.setMessage("Student Record deleted successfully having the Roll Number "+rollNo);
		}
		return response;
	}

	@Override
	public StudentResponse updateStudentById(int rollNo,StudentRequest student) {
		StudentResponse response = new StudentResponse();
		List<Student> list = repository.findByRollNo(rollNo);
		Student reStudent = new Student();
		if (list.get(0).getRollNo()==rollNo) {
			reStudent.setStudentName(student.getName());
			reStudent.setAge(student.getAge());
			reStudent.setGender(student.getGender());
			reStudent.setMobileNumber(student.getMobileNumber());
			repository.save(reStudent);
			response.setMessage("Student Record Updated Successfully with Roll Number => "+rollNo);
		}
		else
		{
			response.setMessage("No Student is Present in DB with this Roll Number");
		}
		return response;
	}

	@Override
	public List<Student> findById(int rollNo) {
		List<Student> reStudent = repository.findByRollNo(rollNo);
		return reStudent;
	}

	@Override
	public Student addStudent(StudentRequest student) {
		Student reStudent = new Student();
		
			reStudent.setStudentName(student.getName());
			reStudent.setAge(student.getAge());
			reStudent.setGender(student.getGender());
			reStudent.setMobileNumber(student.getMobileNumber());
			
			return repository.save(reStudent);
	}
	
}
