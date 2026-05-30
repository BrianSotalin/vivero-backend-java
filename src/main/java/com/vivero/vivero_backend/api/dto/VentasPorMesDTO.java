package com.vivero.vivero_backend.api.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VentasPorMesDTO {
    private int mes;
    private int anio;
    private long cantidad;
}
