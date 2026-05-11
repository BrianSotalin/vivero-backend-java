package com.vivero.vivero_backend.api.repository;


import com.vivero.vivero_backend.api.model.Venta;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
	// Busca por el campo 'codigo' de la entidad Venta
    Optional<Venta> findByCodigo(String codigo);
    
    // Busca por el ID del objeto 'cliente' dentro de Venta
    List<Venta> findByClienteId(Long clienteId);
    
    @Query("SELECT SUM(v.total) FROM Venta v")
    Double sumarTotalVentas();
}