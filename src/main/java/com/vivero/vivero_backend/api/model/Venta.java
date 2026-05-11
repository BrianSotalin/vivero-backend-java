package com.vivero.vivero_backend.api.model;


import java.time.LocalDateTime;
import java.util.List; 
import java.util.ArrayList; // Recomendado para inicializar

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ventas")
@Data
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String codigo;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    private LocalDateTime fecha = LocalDateTime.now();
    private Double total;

    // Es mejor inicializar la lista como un ArrayList vacío
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
}