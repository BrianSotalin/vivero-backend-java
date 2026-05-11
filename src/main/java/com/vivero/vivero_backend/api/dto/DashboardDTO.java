package com.vivero.vivero_backend.api.dto;


import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class DashboardDTO {
    private Long totalVentasRealizadas;
    private Double ingresosTotales;
    private String productoMasVendido;
    private Integer unidadesDelProductoMasVendido;
}
