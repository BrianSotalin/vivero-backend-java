package com.vivero.vivero_backend.api.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductoVendidoDTO {
    private String nombre;
    private long cantidad;
}
