package com.vivero.vivero_backend.api.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String producto;
    private String categoria;
    
    @Column(name = "precio_compra")
    private Double precioCompra;

    @Column(name = "precio_venta")
    private Double precioVenta;

}
