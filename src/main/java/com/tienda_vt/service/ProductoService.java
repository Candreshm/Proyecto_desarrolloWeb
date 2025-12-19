/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda_vt.service;

import com.tienda_vt.domain.Producto;
import com.tienda_vt.repository.ProductoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.tienda_vt.service.FirebaseStorageService;
import java.math.BigDecimal;
import java.util.ArrayList;
        
/**
 *
 * @author lrq2a
 */
@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Transactional(readOnly=true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }
    
    // Add this method to get products by category
    @Transactional(readOnly=true)
    public List<Producto> getProductosByCategoria(Integer idCategoria, boolean activo) {
        if (activo) {
            return productoRepository.findByCategoriaIdCategoriaAndActivoTrue(idCategoria);
        }
        return productoRepository.findByCategoriaIdCategoria(idCategoria);
    }
    
    // Add search methods
    @Transactional(readOnly=true)
    public List<Producto> buscarProductos(String buscar) {
        if (buscar == null || buscar.trim().isEmpty()) {
            return getProductos(false);
        }
        return productoRepository.buscarProductos(buscar.trim());
    }
    
    @Transactional(readOnly=true)
    public List<Producto> buscarProductosPorPrecio(String buscar, BigDecimal precioInf, BigDecimal precioSup) {
        // Use the advanced search method instead
        return busquedaAvanzada(buscar, precioInf, precioSup, null, null, null);
    }
    
    // Advanced search method
    @Transactional(readOnly=true)
    public List<Producto> busquedaAvanzada(String buscar, 
                                           BigDecimal precioInf, 
                                           BigDecimal precioSup,
                                           String ubicacion,
                                           Integer existenciasMin,
                                           Integer existenciasMax) {
        // Normalize empty strings to null
        String buscarNormalized = (buscar != null && !buscar.trim().isEmpty()) ? buscar.trim() : null;
        String ubicacionNormalized = (ubicacion != null && !ubicacion.trim().isEmpty()) ? ubicacion.trim() : null;
        
        return productoRepository.busquedaAvanzada(
            buscarNormalized,
            precioInf,
            precioSup,
            ubicacionNormalized,
            existenciasMin,
            existenciasMax
        );
    }
    
    // Search with multiple keywords (comma or space separated)
    @Transactional(readOnly=true)
    public List<Producto> buscarPorKeywords(String keywords) {
        if (keywords == null || keywords.trim().isEmpty()) {
            return getProductos(false);
        }
        // Split by comma or space and search for each keyword
        String[] keywordArray = keywords.split("[,\\s]+");
        List<Producto> resultados = new ArrayList<>();
        
        for (String keyword : keywordArray) {
            if (!keyword.trim().isEmpty()) {
                List<Producto> productos = productoRepository.buscarProductos(keyword.trim());
                if (resultados.isEmpty()) {
                    resultados = productos;
                } else {
                    // Intersection: keep only products that match all keywords
                    resultados.retainAll(productos);
                }
            }
        }
        
        return resultados;
    }
    
    @Transactional(readOnly=true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }
    
    @Transactional
    public void eliminar(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)){
            throw new IllegalArgumentException("La producto "+idProducto+"no existe");
        }
        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException ("no se puede eliminar porque tiene datos asociados"+e);
        }
    }

    @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        producto = productoRepository.save(producto);
        if (!imagenFile.isEmpty()){
            try {
                String rutaImagen = firebaseStorageService.uploadImage(imagenFile, "producto", producto.getIdProducto());
                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);
            } catch (IOException e) {
            }
        }
    }
    
    @Transactional(readOnly=true)
    public List<Producto> consultaDerivada(BigDecimal precioInf, BigDecimal precioSup) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
    }
    
    /*@Transactional(readOnly=true)
    public List<Producto> consultaJPQL(BigDecimal precioInf, BigDecimal precioSup) {
        return productoRepository.consultaJPQL(precioInf, precioSup);
    }*/
    
    @Transactional(readOnly=true)
    public List<Producto> consultaJPQL(BigDecimal precioInf, BigDecimal precioSup) {
        System.out.println("Calling consultaJPQL with " + precioInf + " - " + precioSup);
        var result = productoRepository.consultaJPQL(precioInf, precioSup);
        System.out.println("Returned " + result.size() + " productos");
        return result;
    }
    
    @Transactional(readOnly=true)
    public List<Producto> consultaSQL(BigDecimal precioInf, BigDecimal precioSup) {
        return productoRepository.consultaSQL(precioInf, precioSup);
    }
    
}
