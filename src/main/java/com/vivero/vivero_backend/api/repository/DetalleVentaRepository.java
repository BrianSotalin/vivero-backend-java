package com.vivero.vivero_backend.api.repository;


import com.vivero.vivero_backend.api.model.DetalleVenta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
	@Query("SELECT d.producto.producto, SUM(d.cantidad) as total " +
	           "FROM DetalleVenta d " +
	           "GROUP BY d.producto.id, d.producto.producto " +
	           "ORDER BY total DESC")
	    List<Object[]> encontrarProductoMasVendido();
	 // Nueva query limitada a top 5
	    @Query("SELECT d.producto.producto, SUM(d.cantidad) as total " +
	           "FROM DetalleVenta d " +
	           "GROUP BY d.producto.id, d.producto.producto " +
	           "ORDER BY total DESC " +
	           "LIMIT 5")
	    List<Object[]> encontrarTop5ProductosMasVendidos();
	    // Método para verificar si existen detalles de venta asociados a un producto específico
	    boolean existsByProductoId(Long productoId);
}
