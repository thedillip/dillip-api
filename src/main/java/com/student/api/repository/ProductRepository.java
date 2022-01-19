package com.student.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.student.api.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
