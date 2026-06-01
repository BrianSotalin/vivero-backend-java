package com.vivero.vivero_backend.api.controller;

import java.util.List;
import java.util.Map;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Gestión del historial de ventas")
@SecurityRequirement(name = "bearerAuth")
public class VentaController {

    @Autowired
    private VentaService ventaService;
    
    @Operation(
            summary = "Crear una nueva venta",
            description = "Registra una venta con sus detalles, cliente opcional y estado de pago (0=PAGADO, 1=DEUDA, 2=ABONADO)."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta creada con éxito"),
            @ApiResponse(responseCode = "500", description = "Error en la transacción")
        })

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Venta venta) {
    	
        try {
            Venta nuevaVenta = ventaService.registrarVenta(venta);
            return ResponseEntity.ok(nuevaVenta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la transacción: " + e.getMessage());
        }
    }
    
    @Operation(
            summary = "Actualizar una venta",
            description = "Permite actualizar el cliente, estado, abono y/o detalles de una venta existente."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta actualizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Error al actualizar la venta")
        })
    
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
    
    @Operation(summary = "Eliminar una venta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta eliminada con éxito"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    
    // Eliminar una venta (ej: /api/ventas/1)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            ventaService.eliminarVenta(id);
            return ResponseEntity.ok(Map.of("message","Venta eliminada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    
    @Operation(summary = "Listar todas las ventas")
    @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida")
    
 // Listar todas las ventas
    @GetMapping
    public List<Venta> listar() {
        return ventaService.listarTodas();
    }

    @Operation(summary = "Obtener venta por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta encontrada"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    
    // Buscar una venta por su ID interno
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ventaService.obtenerPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    
    @Operation(
            summary = "Buscar venta por código",
            description = "Busca una venta usando su código de comprobante (ej: 0001)."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta encontrada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
        })

    // Buscar por código (ej: /api/ventas/buscar/0001)
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(ventaService.obtenerPorCodigo(codigo));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Listar ventas por cliente",
            description = "Devuelve todas las ventas asociadas a un cliente específico."
        )
        @ApiResponse(responseCode = "200", description = "Lista de ventas del cliente obtenida")
    
    // Listar ventas de un cliente específico (ej: /api/ventas/cliente/1)
    @GetMapping("/cliente/{clienteId}")
    public List<Venta> listarPorCliente(@PathVariable Long clienteId) {
        return ventaService.listarPorCliente(clienteId);
    }
}