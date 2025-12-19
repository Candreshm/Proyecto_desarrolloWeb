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
    
    public List<Producto> findByCategoriaIdCategoria(Integer idCategoria);
    
    public List<Producto> findByCategoriaIdCategoriaAndActivoTrue(Integer idCategoria);
    
    // Basic search
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "LOWER(p.detalle) LIKE LOWER(CONCAT('%', :buscar, '%'))")
    public List<Producto> buscarProductos(@Param("buscar") String buscar);
    
    // Advanced search with all filters
    @Query("SELECT p FROM Producto p WHERE " +
           "(:buscar IS NULL OR :buscar = '' OR " +
           "  LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
           "  LOWER(p.detalle) LIKE LOWER(CONCAT('%', :buscar, '%'))) AND " +
           "(:precioInf IS NULL OR :precioSup IS NULL OR p.precio BETWEEN :precioInf AND :precioSup) AND " +
           "(:ubicacion IS NULL OR :ubicacion = '' OR p.ubicacion = :ubicacion) AND " +
           "(:existenciasMin IS NULL OR p.existencias >= :existenciasMin) AND " +
           "(:existenciasMax IS NULL OR p.existencias <= :existenciasMax) " +
           "ORDER BY p.precio ASC")
    public List<Producto> busquedaAvanzada(@Param("buscar") String buscar,
                                            @Param("precioInf") BigDecimal precioInf,
                                            @Param("precioSup") BigDecimal precioSup,
                                            @Param("ubicacion") String ubicacion,
                                            @Param("existenciasMin") Integer existenciasMin,
                                            @Param("existenciasMax") Integer existenciasMax);
    
    // Search with multiple keywords
    @Query("SELECT DISTINCT p FROM Producto p WHERE " +
           "(:keywords IS NULL OR :keywords = '' OR " +
           "  EXISTS (SELECT 1 FROM Producto p2 WHERE p2.idProducto = p.idProducto AND " +
           "    (LOWER(p2.descripcion) LIKE LOWER(CONCAT('%', :keywords, '%')) OR " +
           "     LOWER(p2.detalle) LIKE LOWER(CONCAT('%', :keywords, '%')))))")
    public List<Producto> buscarPorKeywords(@Param("keywords") String keywords);
    
    @Query(value="SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(BigDecimal precioInf, BigDecimal precioSup);
    
    @Query(nativeQuery=true,
                    value="SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaSQL(BigDecimal precioInf, BigDecimal precioSup);
    
}
