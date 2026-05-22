package com.vivero.vivero_backend.api.controller;

import com.vivero.vivero_backend.api.model.Cliente;
import com.vivero.vivero_backend.api.repository.ClienteRepository;
import com.vivero.vivero_backend.api.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ClienteService clienteService; // Inyectamos el servicio, no el repo

    // Listar todos los clientes (Requiere Token)
    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }
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