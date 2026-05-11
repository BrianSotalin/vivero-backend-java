package com.vivero.vivero_backend.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivero.vivero_backend.api.model.Venta;
import com.vivero.vivero_backend.api.service.VentaService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Venta venta) {
    	
        try {
            Venta nuevaVenta = ventaService.registrarVenta(venta);
            return ResponseEntity.ok(nuevaVenta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la transacción: " + e.getMessage());
        }
    }
    //Editar una venta (ej: /api/ventas/1)
    @PatchMapping("/{id}")
    public ResponseEntity<?> editarVenta(@PathVariable Long id, @RequestBody Venta venta) {
        try {
            Venta actualizada = ventaService.actualizarVenta(id, venta);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al actualizar: " + e.getMessage());
        }
    }
    // Eliminar una venta (ej: /api/ventas/1)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            ventaService.eliminarVenta(id);
            return ResponseEntity.ok("Venta eliminada correctamente y stock de estadísticas actualizado.");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
 // Listar todas las ventas
    @GetMapping
    public List<Venta> listar() {
        return ventaService.listarTodas();
    }

    // Buscar una venta por su ID interno
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ventaService.obtenerPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Buscar por código (ej: /api/ventas/buscar/0001)
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(ventaService.obtenerPorCodigo(codigo));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Listar ventas de un cliente específico (ej: /api/ventas/cliente/1)
    @GetMapping("/cliente/{clienteId}")
    public List<Venta> listarPorCliente(@PathVariable Long clienteId) {
        return ventaService.listarPorCliente(clienteId);
    }
}