package com.ecomm.nrt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ecomm.nrt.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// Custom query methods can go here later
	
}
