package com.vivero.vivero_backend.api.repository;


import com.vivero.vivero_backend.api.model.DetalleVenta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
	
	// Query para encontrar el producto más vendido considerando solo ventas pagadas (estado = 0)
	@Query("SELECT d.producto.producto, SUM(d.cantidad) as total " +
	           "FROM DetalleVenta d " +
	           "WHERE d.venta.estado = 0 " +
	           "GROUP BY d.producto.id, d.producto.producto " +
	           "ORDER BY total DESC")
	    List<Object[]> encontrarProductoMasVendido();
	    
	 // Query para encontrar los 5 productos más vendidos considerando solo ventas pagadas (estado = 0)
	    @Query("SELECT d.producto.producto, SUM(d.cantidad) as total " +
	           "FROM DetalleVenta d " +
	           "WHERE d.venta.estado = 0 " +
	           "GROUP BY d.producto.id, d.producto.producto " +
	           "ORDER BY total DESC " +
	           "LIMIT 5")
	    List<Object[]> encontrarTop5ProductosMasVendidos();
	    // Método para verificar si existen detalles de venta asociados a un producto específico
	    boolean existsByProductoId(Long productoId);
}
