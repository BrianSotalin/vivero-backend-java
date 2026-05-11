package com.vivero.vivero_backend.api.service;


import com.vivero.vivero_backend.api.model.Producto;
import com.vivero.vivero_backend.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto guardar(Producto producto) {

        return productoRepository.save(producto);
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }
    public Producto actualizarParcial(Long id, Producto datosNuevos) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Validamos cada campo: si no es nulo, se actualiza
        if (datosNuevos.getProducto() != null) {
            productoExistente.setProducto(datosNuevos.getProducto());
        }
        if (datosNuevos.getCategoria() != null) {
            productoExistente.setCategoria(datosNuevos.getCategoria());
        }
        if (datosNuevos.getPrecioCompra() != null) {
            productoExistente.setPrecioCompra(datosNuevos.getPrecioCompra());
        }
        if (datosNuevos.getPrecioVenta() != null) {
            productoExistente.setPrecioVenta(datosNuevos.getPrecioVenta());
        }

        return productoRepository.save(productoExistente);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}