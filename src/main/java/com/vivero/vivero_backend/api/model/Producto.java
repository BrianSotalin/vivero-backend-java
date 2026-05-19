package com.vivero.vivero_backend.api.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String producto;
 // 1. CATEGORÍA OBLIGATORIA
    @NotBlank(message = "La categoría es obligatoria y no puede estar vacía")
    private String categoria;
    
 // 2. EVITAR PRECIOS NEGATIVOS (Permite 0 o más)
    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    @Column(name = "precio_compra")
    private Double precioCompra;

    @PositiveOrZero(message = "El precio de venta no puede ser negativo")
    @Column(name = "precio_venta")
    private Double precioVenta;

}
