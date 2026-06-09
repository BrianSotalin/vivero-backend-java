package com.vivero.vivero_backend.api.service;



import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.vivero.vivero_backend.api.model.DetalleVenta;
import com.vivero.vivero_backend.api.model.Producto;
import com.vivero.vivero_backend.api.model.Venta;
import com.vivero.vivero_backend.api.repository.ProductRepository;
import com.vivero.vivero_backend.api.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private ProductRepository productoRepository; // Para actualizar el producto

    @Transactional // <--- ESTO ASEGURA QUE SI ALGO FALLA, NADA SE GUARDA
    public Venta registrarVenta(Venta venta) {
        long count = ventaRepository.count() + 1;
        venta.setCodigo(String.format("%04d", count));

        double totalCalculado = 0.0;
        
        // Validación de fecha
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioAñoPasado = LocalDateTime.of(ahora.getYear() - 1, 1, 1, 0, 0);
        
        if (venta.getFecha() != null) {
            if (venta.getFecha().isAfter(ahora)) {
                throw new RuntimeException("La fecha no puede ser superior a la fecha actual");
            }
            if (venta.getFecha().isBefore(inicioAñoPasado)) {
                throw new RuntimeException("La fecha no puede ser del año pasado");
            }
        } else {
            venta.setFecha(ahora);
        }

        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setVenta(venta);
                
                // Si el precio no viene, lo buscamos del producto
                if (detalle.getPrecio() == null) {
                    Producto p = productoRepository.findById(detalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    detalle.setPrecio(p.getPrecioVenta());
                }

                totalCalculado += detalle.getCantidad() * detalle.getPrecio();
            }
        }

        venta.setTotal(totalCalculado);
        return ventaRepository.save(venta);
    }
    
    @Transactional
    public Venta actualizarVenta(Long id, Venta datosNuevos) {
        Venta ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        // 1. Actualizar Cliente si se envía
        if (datosNuevos.getCliente() != null && datosNuevos.getCliente().getId() != null) {
            ventaExistente.setCliente(datosNuevos.getCliente());
        }

        // 2. Actualizar Fecha si se envía
        if (datosNuevos.getFecha() != null) {
            ventaExistente.setFecha(datosNuevos.getFecha());
        }

        // 3. Actualizar Estado si se envía (0=Pagado, 1=Deuda, 2=Abonado)
        if (datosNuevos.getEstado() >= 0) {
            ventaExistente.setEstado(datosNuevos.getEstado());

            // Si cambia a Pagado o Deuda, limpiamos el abono
            if (datosNuevos.getEstado() == 0 || datosNuevos.getEstado() == 1) {
                ventaExistente.setAbono(0.0);
            }
        }

        // 4. Actualizar Abono solo si estado es 2 (Abonado)
        if (datosNuevos.getEstado() == 2 && datosNuevos.getAbono() != null) {
            ventaExistente.setAbono(datosNuevos.getAbono());
        }

        // 3. Actualizar Detalles (si se envían, reemplazamos los anteriores)
        if (datosNuevos.getDetalles() != null && !datosNuevos.getDetalles().isEmpty()) {
            
            // Al usar orphanRemoval = true en la entidad, esto borrará los detalles viejos de la DB
            ventaExistente.getDetalles().clear();

            double nuevoTotal = 0.0;
            for (DetalleVenta nuevoDetalle : datosNuevos.getDetalles()) {
                nuevoDetalle.setVenta(ventaExistente);
                
                // Si el precio no viene en el JSON, lo recuperamos del producto
                if (nuevoDetalle.getPrecio() == null) {
                    Producto p = productoRepository.findById(nuevoDetalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + nuevoDetalle.getProducto().getId()));
                    nuevoDetalle.setPrecio(p.getPrecioVenta());
                }

                nuevoTotal += nuevoDetalle.getCantidad() * nuevoDetalle.getPrecio();
                ventaExistente.getDetalles().add(nuevoDetalle);
            }
            
            // El total se actualiza automáticamente basado en los nuevos detalles
            ventaExistente.setTotal(nuevoTotal);
        }

        return ventaRepository.save(ventaExistente);
    }
    @Transactional
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        // Ya no necesitamos recorrer productos para restar ventas.
        // Solo borramos la venta y los detalles se borran por cascada.
        ventaRepository.delete(venta);
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    public Venta obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
    }

    public Venta obtenerPorCodigo(String codigo) {
        return ventaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con código: " + codigo));
    }

    public List<Venta> listarPorCliente(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }
    /**
     * Genera el PDF de una venta y lo retorna codificado en un String Base64.
     */
    public String generarPdfVentaBase64(Long id) {
        Venta venta = obtenerPorId(id);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(); // Tamaño A4 por defecto
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Fuentes
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fuenteNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            
            // --- ENCABEZADO ---
            Paragraph titulo = new Paragraph("COMPROBANTE DE VENTA", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(15);
            document.add(titulo);
            
            // Tabla de información general (Vivero vs Factura)
            PdfPTable tablaEncabezado = new PdfPTable(2);
            tablaEncabezado.setWidthPercentage(100);
            
            PdfPCell celdaIzquierda = new PdfPCell(new Phrase("VIVERO LA VEGA\nDirección: Santo Domingo\nContacto: lavega@vivero.com", fuenteNormal));
            celdaIzquierda.setBorder(PdfPCell.NO_BORDER);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaFormateada = venta.getFecha().format(formatter);
            
            PdfPCell celdaDerecha = new PdfPCell(new Phrase(
                "Código Venta: " + venta.getCodigo() + "\n" +
                "Fecha: " + fechaFormateada, fuenteNormal
            ));
            celdaDerecha.setBorder(PdfPCell.NO_BORDER);
            celdaDerecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            tablaEncabezado.addCell(celdaIzquierda);
            tablaEncabezado.addCell(celdaDerecha);
            tablaEncabezado.setSpacingAfter(20);
            document.add(tablaEncabezado);
            
            // --- DATOS DEL CLIENTE ---
            Paragraph tituloCliente = new Paragraph("Datos del Cliente", fuenteSubtitulo);
            tituloCliente.setSpacingAfter(5);
            document.add(tituloCliente);
            
            String nombreCliente = (venta.getCliente() != null) ? venta.getCliente().getNombre() : "Público General";
            // Si tu entidad cliente tiene más datos (ej: teléfono o RFC), agrégalos aquí
            Paragraph datosCliente = new Paragraph("Nombre / Razón Social: " + nombreCliente, fuenteNormal);
            datosCliente.setSpacingAfter(20);
            document.add(datosCliente);
            
            // --- TABLA DE DETALLES ---
            PdfPTable tablaDetalles = new PdfPTable(4); // 4 Columnas
            tablaDetalles.setWidthPercentage(100);
            tablaDetalles.setWidths(new float[]{40f, 15f, 20f, 25f}); // Anchos relativos
            
            // Cabeceras
            String[] cabeceras = {"Producto", "Cant.", "Precio Unit.", "Subtotal"};
            for (String cabecera : cabeceras) {
                PdfPCell cell = new PdfPCell(new Phrase(cabecera, fuenteNegrita));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                tablaDetalles.addCell(cell);
            }
            
            // Filas de productos
            for (DetalleVenta detalle : venta.getDetalles()) {
                String nombreProd = (detalle.getProducto() != null) ? detalle.getProducto().getProducto() : "Producto Desconocido";
                double subtotal = detalle.getCantidad() * detalle.getPrecio();
                
                tablaDetalles.addCell(new PdfPCell(new Phrase(nombreProd, fuenteNormal)));
                
                PdfPCell cellCant = new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), fuenteNormal));
                cellCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaDetalles.addCell(cellCant);
                
                PdfPCell cellPrecio = new PdfPCell(new Phrase(String.format("$%.2f", detalle.getPrecio()), fuenteNormal));
                cellPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaDetalles.addCell(cellPrecio);
                
                PdfPCell cellSub = new PdfPCell(new Phrase(String.format("$%.2f", subtotal), fuenteNormal));
                cellSub.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaDetalles.addCell(cellSub);
            }
            
            tablaDetalles.setSpacingAfter(15);
            document.add(tablaDetalles);
            
            // --- TOTALES Y ESTADOS ---
            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(100);
            tablaTotales.setWidths(new float[]{60f, 40f});
            
            // Estado del pago
            String txtEstado = "Estado: ";
            if (venta.getEstado() == 0) txtEstado += "PAGADO";
            else if (venta.getEstado() == 1) txtEstado += "DEUDA PENDIENTE";
            else if (venta.getEstado() == 2) txtEstado += "ABONADO (Restante: $" + String.format("%.2f", (venta.getTotal() - venta.getAbono())) + ")";
            
            PdfPCell celdaEstado = new PdfPCell(new Phrase(txtEstado, fuenteNegrita));
            celdaEstado.setBorder(PdfPCell.NO_BORDER);
            
            // Bloque de importes numéricos
            String desgloseTotales = "Total: " + String.format("$%.2f", venta.getTotal());
            if (venta.getEstado() == 2 && venta.getAbono() != null) {
                desgloseTotales += "\nMonto Abonado: " + String.format("$%.2f", venta.getAbono());
            }
            
            PdfPCell celdaTotalValores = new PdfPCell(new Phrase(desgloseTotales, fuenteNegrita));
            celdaTotalValores.setBorder(PdfPCell.NO_BORDER);
            celdaTotalValores.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            tablaTotales.addCell(celdaEstado);
            tablaTotales.addCell(celdaTotalValores);
            document.add(tablaTotales);
            
        } catch (DocumentException e) {
            throw new RuntimeException("Error al estructurar el PDF", e);
        } finally {
            document.close();
        }
        
        // Convertimos el flujo de bytes del PDF generado a String Base64
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
