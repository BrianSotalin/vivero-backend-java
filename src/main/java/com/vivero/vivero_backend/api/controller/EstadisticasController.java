package com.vivero.vivero_backend.api.controller;


import com.vivero.vivero_backend.api.dto.DashboardDTO;
import com.vivero.vivero_backend.api.dto.ProductoVendidoDTO;
import com.vivero.vivero_backend.api.dto.VentasPorMesDTO;
import com.vivero.vivero_backend.api.service.EstadisticasService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estadisticas")
@Tag(name = "Estadísticas", description = "Dashboard y métricas del sistema")
@SecurityRequirement(name = "bearerAuth")
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;
    
    @Operation(
            summary = "Resumen general",
            description = "Devuelve el total de ventas, ingresos totales y el producto más vendido."
        )
        @ApiResponse(responseCode = "200", description = "Resumen obtenido con éxito")
    

    @GetMapping("/resumen")
    public DashboardDTO getResumen() {
        return estadisticasService.obtenerResumenGeneral();
    }
    
    @Operation(
            summary = "Ventas por mes",
            description = "Devuelve el conteo de ventas agrupadas por mes y año."
        )
        @ApiResponse(responseCode = "200", description = "Datos de ventas por mes obtenidos")
    
    @GetMapping("/ventas-por-mes")
    public List<VentasPorMesDTO> getVentasPorMes() {
        return estadisticasService.obtenerVentasPorMes();
    }
    @Operation(
            summary = "Top 5 productos más vendidos",
            description = "Devuelve los 5 productos con mayor cantidad de unidades vendidas."
        )
        @ApiResponse(responseCode = "200", description = "Top 5 productos obtenidos")
    
    @GetMapping("/top-productos")
    public List<ProductoVendidoDTO> getTopProductos() {
        return estadisticasService.obtenerTop5Productos();
    }
}
