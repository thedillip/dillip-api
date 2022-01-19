package com.student.api.request;

import com.student.api.entity.Person;

public class PersonProduct {
	private Person person;

	public PersonProduct() {
		super();
	}

	public PersonProduct(Person person) {
		super();
		this.person = person;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	
}
