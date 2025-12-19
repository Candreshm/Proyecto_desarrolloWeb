/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tienda_vt.repository;

import com.tienda_vt.domain. Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lrq2a
 */

@Repository
public interface ProductoRepository extends JpaRepository <Producto, Integer> {
    
    public List<Producto> findByActivoTrue();
    
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(BigDecimal precioInf, BigDecimal precioSup);
    
    // Add this method to find products by category
    public List<Producto> findByCategoriaIdCategoria(Integer idCategoria);
    
    // Add this method to find active products by category
    public List<Producto> findByCategoriaIdCategoriaAndActivoTrue(Integer idCategoria);
    
    // Add search methods
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.detalle) LIKE LOWER(CONCAT('%', :buscar, '%'))")
    public List<Producto> buscarProductos(@Param("buscar") String buscar);
    
    @Query("SELECT p FROM Producto p WHERE " +
           "(LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.detalle) LIKE LOWER(CONCAT('%', :buscar, '%'))) AND " +
           "p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> buscarProductosPorPrecio(@Param("buscar") String buscar, 
                                                     @Param("precioInf") BigDecimal precioInf, 
                                                     @Param("precioSup") BigDecimal precioSup);
    
    @Query(value="SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(BigDecimal precioInf, BigDecimal precioSup);
    
    @Query(nativeQuery=true,
                    value="SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaSQL(BigDecimal precioInf, BigDecimal precioSup);
    
}
