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
    
    // Cuenta cuántas ventas tienen estado = 0 (pagadas)
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = 0")
    Long contarVentasPagadas();
    
    // Suma el total de ventas con estado = 0 (pagadas)
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.estado = 0")
    Double sumarTotalVentas();

    // Cuenta cuántas ventas se han realizado por mes (solo las pagadas, estado = 0)
    @Query("SELECT MONTH(v.fecha), YEAR(v.fecha), COUNT(v) " +
    	       "FROM Venta v " +
    	       "WHERE v.estado = 0 " +
    	       "GROUP BY YEAR(v.fecha), MONTH(v.fecha) " +
    	       "ORDER BY YEAR(v.fecha), MONTH(v.fecha)")
    	List<Object[]> contarVentasPorMes();
}