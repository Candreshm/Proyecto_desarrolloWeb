package com.tienda_vt.repository;

import com.tienda_vt.domain.Venta;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VentaRepository extends JpaRepository<Venta, Integer>{
    
    // Get all sales with product and factura details
    @Query("SELECT v FROM Venta v " +
           "LEFT JOIN FETCH v.producto p " +
           "LEFT JOIN FETCH v.factura f " +
           "LEFT JOIN FETCH f.usuario u " +
           "ORDER BY v.fechaCreacion DESC")
    List<Venta> findAllWithDetails();
    
    // Top selling products (by quantity sold)
    @Query("SELECT v.producto.descripcion as productName, " +
           "SUM(v.cantidad) as totalQuantity, " +
           "SUM(v.precioHistorico * v.cantidad) as totalRevenue " +
           "FROM Venta v " +
           "GROUP BY v.producto.idProducto, v.producto.descripcion " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts();
    
    // Sales by day
    @Query("SELECT DATE(v.fechaCreacion) as saleDate, " +
           "COUNT(v) as saleCount, " +
           "SUM(v.precioHistorico * v.cantidad) as totalRevenue " +
           "FROM Venta v " +
           "GROUP BY DATE(v.fechaCreacion) " +
           "ORDER BY saleDate DESC")
    List<Object[]> findSalesByDay();
}
