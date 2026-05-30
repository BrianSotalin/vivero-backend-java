package com.vivero.vivero_backend.api.controller;


import com.vivero.vivero_backend.api.dto.DashboardDTO;
import com.vivero.vivero_backend.api.dto.ProductoVendidoDTO;
import com.vivero.vivero_backend.api.dto.VentasPorMesDTO;
import com.vivero.vivero_backend.api.service.EstadisticasService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping("/resumen")
    public DashboardDTO getResumen() {
        return estadisticasService.obtenerResumenGeneral();
    }
    @GetMapping("/ventas-por-mes")
    public List<VentasPorMesDTO> getVentasPorMes() {
        return estadisticasService.obtenerVentasPorMes();
    }

    @GetMapping("/top-productos")
    public List<ProductoVendidoDTO> getTopProductos() {
        return estadisticasService.obtenerTop5Productos();
    }
}
