package com.vivero.vivero_backend.api.controller;


import com.vivero.vivero_backend.api.model.Producto;
import com.vivero.vivero_backend.api.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión del inventario de productos")
@SecurityRequirement(name = "bearerAuth")

public class ProductoController {

    @Autowired
    private ProductoService productoService;
    

    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida")

    @GetMapping
    public List<Producto> listar() {
        return productoService.listarTodos();
    }

    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de validación incorrectos")
    })
    
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productoService.obtenerPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    @Operation(summary = "Actualizar parcialmente un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> editarParcial(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto actualizado = productoService.actualizarParcial(id, producto);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    @Operation(summary = "Eliminar un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
    			try {
			productoService.eliminar(id);
			return ResponseEntity.ok(Map.of("message", "Producto eliminado"));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e.getMessage());
		}
    }
}