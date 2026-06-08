package com.vivero.vivero_backend;

import com.vivero.vivero_backend.api.model.*;
import com.vivero.vivero_backend.api.repository.*;
import com.vivero.vivero_backend.api.service.ClienteService;
import com.vivero.vivero_backend.api.service.ProductoService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class VentaProteccionTest {
	
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private ProductRepository productoRepository;
    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private ClienteService clienteService;
    @InjectMocks
    private ProductoService productoService;

    // ─── CLIENTE ────────────────────────────────────────────────────

    @Test
    void noDebeEliminarClienteConVentasAsociadas() {
        // Arrange
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNombre("Sofia");

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setCliente(cliente);

        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        when(ventaRepository.findByClienteId(clienteId)).thenReturn(List.of(venta));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.eliminar(clienteId));

        assertEquals("No se puede eliminar el cliente con id: 1 porque tiene ventas asociadas.", ex.getMessage());
        verify(clienteRepository, never()).deleteById(clienteId);
    }

    @Test
    void debeEliminarClienteSinVentasAsociadas() {
        Long clienteId = 2L;

        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        when(ventaRepository.findByClienteId(clienteId)).thenReturn(List.of());

        assertDoesNotThrow(() -> clienteService.eliminar(clienteId));
        verify(clienteRepository).deleteById(clienteId);
    }

    // ─── PRODUCTO ───────────────────────────────────────────────────

    @Test
    void noDebeEliminarProductoConVentasAsociadas() {
        Long productoId = 1L;

        when(productoRepository.existsById(productoId)).thenReturn(true);
        when(detalleVentaRepository.existsByProductoId(productoId)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.eliminar(productoId));

        assertEquals("No se puede eliminar el producto con ID: 1 porque tiene detalles de venta asociados.", ex.getMessage());
        verify(productoRepository, never()).deleteById(productoId);
    }

    @Test
    void debeEliminarProductoSinVentasAsociadas() {
        Long productoId = 2L;

        when(productoRepository.existsById(productoId)).thenReturn(true);
        when(detalleVentaRepository.existsByProductoId(productoId)).thenReturn(false);

        assertDoesNotThrow(() -> productoService.eliminar(productoId));
        verify(productoRepository).deleteById(productoId);
    }

}
