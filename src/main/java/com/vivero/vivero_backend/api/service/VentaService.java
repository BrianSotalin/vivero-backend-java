package com.vivero.vivero_backend.api.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vivero.vivero_backend.api.model.DetalleVenta;
import com.vivero.vivero_backend.api.model.Producto;
import com.vivero.vivero_backend.api.model.Venta;
import com.vivero.vivero_backend.api.repository.ProductRepository;
import com.vivero.vivero_backend.api.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private ProductRepository productoRepository; // Para actualizar el producto

    @Transactional // <--- ESTO ASEGURA QUE SI ALGO FALLA, NADA SE GUARDA
    public Venta registrarVenta(Venta venta) {
        long count = ventaRepository.count() + 1;
        venta.setCodigo(String.format("%04d", count));

        double totalCalculado = 0.0;

        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setVenta(venta);
                
                // Si el precio no viene, lo buscamos del producto
                if (detalle.getPrecio() == null) {
                    Producto p = productoRepository.findById(detalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    detalle.setPrecio(p.getPrecioVenta());
                }

                totalCalculado += detalle.getCantidad() * detalle.getPrecio();
            }
        }

        venta.setTotal(totalCalculado);
        return ventaRepository.save(venta);
    }
    @Transactional
    public Venta actualizarVenta(Long id, Venta datosNuevos) {
        Venta ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        // 1. Actualizar Cliente si se envía
        if (datosNuevos.getCliente() != null && datosNuevos.getCliente().getId() != null) {
            ventaExistente.setCliente(datosNuevos.getCliente());
        }

        // 2. Actualizar Fecha si se envía
        if (datosNuevos.getFecha() != null) {
            ventaExistente.setFecha(datosNuevos.getFecha());
        }

        // 3. Actualizar Detalles (si se envían, reemplazamos los anteriores)
        if (datosNuevos.getDetalles() != null && !datosNuevos.getDetalles().isEmpty()) {
            
            // Al usar orphanRemoval = true en la entidad, esto borrará los detalles viejos de la DB
            ventaExistente.getDetalles().clear();

            double nuevoTotal = 0.0;
            for (DetalleVenta nuevoDetalle : datosNuevos.getDetalles()) {
                nuevoDetalle.setVenta(ventaExistente);
                
                // Si el precio no viene en el JSON, lo recuperamos del producto
                if (nuevoDetalle.getPrecio() == null) {
                    Producto p = productoRepository.findById(nuevoDetalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + nuevoDetalle.getProducto().getId()));
                    nuevoDetalle.setPrecio(p.getPrecioVenta());
                }

                nuevoTotal += nuevoDetalle.getCantidad() * nuevoDetalle.getPrecio();
                ventaExistente.getDetalles().add(nuevoDetalle);
            }
            
            // El total se actualiza automáticamente basado en los nuevos detalles
            ventaExistente.setTotal(nuevoTotal);
        }

        return ventaRepository.save(ventaExistente);
    }
    @Transactional
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        // Ya no necesitamos recorrer productos para restar ventas.
        // Solo borramos la venta y los detalles se borran por cascada.
        ventaRepository.delete(venta);
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Venta obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
    }

    public Venta obtenerPorCodigo(String codigo) {
        return ventaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con código: " + codigo));
    }

    public List<Venta> listarPorCliente(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }
}
