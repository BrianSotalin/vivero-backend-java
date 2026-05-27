package com.vivero.vivero_backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vivero.vivero_backend.api.model.DetalleVenta;
import com.vivero.vivero_backend.api.model.Producto;
import com.vivero.vivero_backend.api.model.Venta;
import com.vivero.vivero_backend.api.repository.ProductRepository;
import com.vivero.vivero_backend.api.repository.VentaRepository;
import com.vivero.vivero_backend.api.service.VentaService;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private VentaService ventaService; // El servicio que contiene tu método registrarVenta

    private Producto productoSimulado;

    @BeforeEach
    void setUp() {
        // Configuramos un producto de prueba (ej. una Orquídea)
        productoSimulado = new Producto();
        productoSimulado.setId(1L);
        productoSimulado.setProducto("Orquídea Ornamental");
        productoSimulado.setPrecioVenta(15.00); // 15 dólares
    }

    @Test
    void debeRegistrarVentaConEstadoDeudaYCalcularTotal() {
        // 1. ARRANGE (Preparar los datos de entrada y mocks)
        Venta ventaNueva = new Venta();
        ventaNueva.setEstado(1); // <--- ESTADO 1: DEUDA

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(productoSimulado);
        detalle.setCantidad(2);
        // Dejamos el precio en null para forzar al servicio a buscarlo en el productoRepository

        List<DetalleVenta> detalles = new ArrayList<>();
        detalles.add(detalle);
        ventaNueva.setDetalles(detalles);

        // Simulamos el comportamiento de los repositorios
        when(ventaRepository.count()).thenReturn(5L); // Ya hay 5 ventas, esta debería ser la 0006
        when(productRepository.findById(1L)).thenReturn(Optional.of(productoSimulado));
        
        // Simulamos que al guardar, retorna el mismo objeto mapeado
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT (Ejecutar el método que estamos probando)
        Venta ventaResultado = ventaService.registrarVenta(ventaNueva);

        // 3. ASSERT (Verificar que todo se haya calculado y asignado correctamente)
        assertNotNull(ventaResultado);
        
        // Verificar que conserve el Estado 1 (Deuda)
        assertEquals(1, ventaResultado.getEstado(), "El estado debería ser 1 (Deuda)");
        
        // Verificar el autogenerado de código ("0005" + 1 = "0006")
        assertEquals("0006", ventaResultado.getCodigo(), "El código generado debería ser 0006");
        
        // Verificar que el cálculo de la matemática sea correcto (2 plantas * $15.00 = $30.00)
        assertEquals(30.00, ventaResultado.getTotal(), "El total calculado debería ser 30.00");
        
        // Verificar que el precio fue rescatado del producto y asignado al detalle
        assertEquals(15.00, ventaResultado.getDetalles().get(0).getPrecio());
        
        // Verificar que se haya establecido la relación bidireccional
        assertEquals(ventaResultado, ventaResultado.getDetalles().get(0).getVenta());

        // Asegurarnos de que los repositorios fueron llamados las veces correctas
        verify(ventaRepository, times(1)).count();
        verify(productRepository, times(1)).findById(1L);
        verify(ventaRepository, times(1)).save(ventaNueva);
    }
    @Test
    void debeRegistrarVentaConEstadoAbonadoYGuardarMontoAbono() {
        // 1. ARRANGE (Preparar los datos de entrada)
        Venta ventaAbonada = new Venta();
        ventaAbonada.setEstado(2);  // <--- ESTADO 2: ABONADO
        ventaAbonada.setAbono(20.00); // <--- El abono de $20 dólares

        // Creamos un detalle de venta (ej. 3 plantas a $15.00 cada una = $45.00 en total)
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(productoSimulado);
        detalle.setCantidad(3);

        List<DetalleVenta> detalles = new ArrayList<>();
        detalles.add(detalle);
        ventaAbonada.setDetalles(detalles);

        // Simulamos el comportamiento de los repositorios
        when(ventaRepository.count()).thenReturn(10L); // Ya hay 10 ventas, esta será la 0011
        when(productRepository.findById(1L)).thenReturn(Optional.of(productoSimulado));
        
        // Al guardar, Mockito devuelve el mismo objeto
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT (Ejecutar el método del servicio)
        Venta ventaResultado = ventaService.registrarVenta(ventaAbonada);

        // 3. ASSERT (Validaciones)
        assertNotNull(ventaResultado);
        
        // Verificar que el estado se guarde como 2 (Abonado)
        assertEquals(2, ventaResultado.getEstado(), "El estado debería ser 2 (Abonado)");
        
        // Verificar que el abono se mantenga en 20.00
        assertEquals(20.00, ventaResultado.getAbono(), "El valor del abono debería ser 20.00");
        
        // Verificar el código secuencial (10 + 1 = 11 -> "0011")
        assertEquals("0011", ventaResultado.getCodigo(), "El código generado debería ser 0011");
        
        // Verificar que el cálculo total refleje el valor real de la compra (3 * 15 = 45)
        // El total sigue siendo 45, el abono no altera el costo total, solo registra el pago parcial.
        assertEquals(45.00, ventaResultado.getTotal(), "El total calculado de los productos debería ser 45.00");

        // Verificaciones de comportamiento de los mocks
        verify(ventaRepository, times(1)).count();
        verify(productRepository, times(1)).findById(1L);
        verify(ventaRepository, times(1)).save(ventaAbonada);
    }
}