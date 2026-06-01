package com.vivero.vivero_backend.api.controller;

import com.vivero.vivero_backend.api.model.Cliente;
import com.vivero.vivero_backend.api.repository.ClienteRepository;
import com.vivero.vivero_backend.api.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión del directorio de clientes")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ClienteService clienteService; // Inyectamos el servicio, no el repo
    
    @Operation(summary = "Listar todos los clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida")
    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Operation(summary = "Obtener cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })


    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
			Cliente cliente = clienteService.buscarPorId(id)
					.orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
			return ResponseEntity.ok(cliente);
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e.getMessage());
		}
    }
    
    @Operation(summary = "Crear un nuevo cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente creado con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })

    // Crear un nuevo cliente (Requiere Token)
    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        try {
    	Cliente nuevoCliente = clienteService.guardar(cliente);
        return ResponseEntity.ok(nuevoCliente);
        } catch (Exception e) {
			return ResponseEntity.status(500).build();
		}
    }
    @Operation(summary = "Actualizar parcialmente un cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado con éxito"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> editarParcial(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            // Llamamos a la nueva lógica que acabamos de crear
            Cliente actualizado = clienteService.actualizarParcial(id, cliente);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    @Operation(summary = "Eliminar un cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            clienteService.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Cliente eliminado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}