package com.vivero.vivero_backend;

import com.vivero.vivero_backend.api.model.*;
import com.vivero.vivero_backend.api.repository.*;
import com.vivero.vivero_backend.api.service.ClienteService;
import com.vivero.vivero_backend.api.service.ProductoService;
import com.vivero.vivero_backend.api.service.VentaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
    @InjectMocks
    private VentaService ventaService;
    
    

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
    
    // ─── VENTA ──────────────────────────────────────────────────────
    
    @Test
    void debeGuardarVentaConFechaPersonalizada() {
        // Arrange
        LocalDateTime fechaPersonalizada = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 15, 10, 30);

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setPrecioVenta(50.0);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        //detalle.setPrecio(50.0);

        Venta venta = new Venta();
        venta.setFecha(fechaPersonalizada);
        venta.setDetalles(new ArrayList<>(List.of(detalle)));

        when(ventaRepository.count()).thenReturn(0L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Venta resultado = ventaService.registrarVenta(venta);

        // Assert
        assertEquals(fechaPersonalizada, resultado.getFecha());
        assertNotEquals(LocalDateTime.now().getDayOfMonth(), resultado.getFecha().getDayOfMonth());
    }
    @Test
    void debeUsarFechaActualSiNoSeEnviaFecha() {
        Venta venta = new Venta();
        venta.setFecha(null);
        venta.setDetalles(new ArrayList<>());

        when(ventaRepository.count()).thenReturn(0L);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        Venta resultado = ventaService.registrarVenta(venta);

        assertNotNull(resultado.getFecha());
        assertEquals(LocalDateTime.now().getDayOfMonth(), resultado.getFecha().getDayOfMonth());
    }
    @Test
    void noDebePermitirFechaFutura() {
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now().plusDays(1));
        venta.setDetalles(new ArrayList<>());

        when(ventaRepository.count()).thenReturn(0L);
        //when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ventaService.registrarVenta(venta));

        assertEquals("La fecha no puede ser superior a la fecha actual", ex.getMessage());
    }

    @Test
    void noDebePermitirFechaDelAñoPasado() {
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.of(LocalDateTime.now().getYear() - 1, 1, 1, 0, 0).minusDays(1));
        venta.setDetalles(new ArrayList<>());

        when(ventaRepository.count()).thenReturn(0L);
        //when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ventaService.registrarVenta(venta));

        assertEquals("La fecha no puede ser del año pasado", ex.getMessage());
    }

}
