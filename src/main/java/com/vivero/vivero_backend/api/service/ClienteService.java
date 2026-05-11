package com.vivero.vivero_backend.api.service;


import com.vivero.vivero_backend.api.model.Cliente;
import com.vivero.vivero_backend.api.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

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
        clienteRepository.deleteById(id);
    }

    // Editar solo el teléfono (La lógica que pediste)
    public Cliente actualizarTelefono(Long id, String nuevoTelefono) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        
        cliente.setTelefono(nuevoTelefono);
        return clienteRepository.save(cliente);
    }
}