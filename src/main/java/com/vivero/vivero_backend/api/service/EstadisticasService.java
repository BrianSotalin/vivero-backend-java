package com.vivero.vivero_backend.api.service;

import com.vivero.vivero_backend.api.dto.DashboardDTO;
import com.vivero.vivero_backend.api.repository.DetalleVentaRepository;
import com.vivero.vivero_backend.api.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstadisticasService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    public DashboardDTO obtenerResumenGeneral() {
        // 1. Número total de tickets/facturas de venta
        Long conteoVentas = ventaRepository.count();
        
        // 2. Ingresos totales reales (Suma de la columna total de ventas)
        Double ingresos = ventaRepository.sumarTotalVentas();
        if (ingresos == null) ingresos = 0.0;

        // 3. Obtener el producto más vendido desde los detalles
        List<Object[]> resultados = detalleVentaRepository.encontrarProductoMasVendido();
        
        String nombreProducto = "N/A";
        Integer cantidadVendida = 0;

        if (!resultados.isEmpty()) {
            Object[] top = resultados.get(0); // El primero de la lista (el más vendido)
            nombreProducto = (String) top[0];
            cantidadVendida = ((Long) top[1]).intValue();
        }

        return new DashboardDTO(
            conteoVentas, 
            ingresos, 
            nombreProducto, 
            cantidadVendida
        );
    }
}