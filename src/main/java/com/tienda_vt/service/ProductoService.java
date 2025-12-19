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
