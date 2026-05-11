package com.vivero.vivero_backend.api.repository;

import com.vivero.vivero_backend.api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	
}
