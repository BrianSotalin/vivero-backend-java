package com.vivero.vivero_backend.api.service;


import com.vivero.vivero_backend.api.model.Cliente;
import com.vivero.vivero_backend.api.model.Venta;
import com.vivero.vivero_backend.api.repository.ClienteRepository;
import com.vivero.vivero_backend.api.repository.VentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private VentaRepository ventaRepository;


    // Listar todos
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // Crear cliente
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Buscar por ID
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Eliminar
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }
        List<Venta> ventas = ventaRepository.findByClienteId(id);
        if (!ventas.isEmpty()) {	
			throw new RuntimeException("No se puede eliminar el cliente con id: " + id + " porque tiene ventas asociadas.");
		}
        clienteRepository.deleteById(id);
    }

 // Editar solo el teléfono y el email (Lógica actualizada)
    public Cliente actualizarParcial(Long id, Cliente datosNuevos) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        
        // Si el frontend envió un teléfono nuevo, lo actualizamos
        if (datosNuevos.getTelefono() != null) {
            clienteExistente.setTelefono(datosNuevos.getTelefono());
        }
        
        // Si el frontend envió un email nuevo, lo actualizamos
        if (datosNuevos.getEmail() != null) {
            clienteExistente.setEmail(datosNuevos.getEmail());
        }
        
        // El nombre no se toca, se queda intacto como estaba en la base de datos
        return clienteRepository.save(clienteExistente);
    }
}