package com.tienda_vt.service;

import com.tienda_vt.domain.Venta;
import com.tienda_vt.repository.VentaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Venta> getAllSales() {
        return ventaRepository.findAllWithDetails();
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopSellingProducts() {
        List<Object[]> results = ventaRepository.findTopSellingProducts();
        List<Map<String, Object>> products = new ArrayList<>();
        
        for (Object[] row : results) {
            Map<String, Object> product = new HashMap<>();
            product.put("name", row[0] != null ? row[0].toString() : ""); // productName
            product.put("quantity", row[1] != null ? ((Number) row[1]).longValue() : 0L); // totalQuantity
            product.put("revenue", row[2] != null ? ((Number) row[2]).doubleValue() : 0.0); // totalRevenue
            products.add(product);
        }
        
        return products;
    }
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSalesByDay() {
        List<Object[]> results = ventaRepository.findSalesByDay();
        List<Map<String, Object>> salesByDay = new ArrayList<>();
        
        for (Object[] row : results) {
            Map<String, Object> daySale = new HashMap<>();
            
            // Handle date conversion - could be LocalDate, java.sql.Date, or String
            Object dateObj = row[0];
            String dateStr = "";
            if (dateObj instanceof LocalDate) {
                dateStr = ((LocalDate) dateObj).format(DATE_FORMATTER);
            } else if (dateObj instanceof java.sql.Date) {
                dateStr = ((java.sql.Date) dateObj).toLocalDate().format(DATE_FORMATTER);
            } else if (dateObj != null) {
                dateStr = dateObj.toString();
            }
            
            daySale.put("date", dateStr);
            daySale.put("count", row[1] != null ? ((Number) row[1]).longValue() : 0L); // saleCount
            daySale.put("revenue", row[2] != null ? ((Number) row[2]).doubleValue() : 0.0); // totalRevenue
            salesByDay.add(daySale);
        }
        
        return salesByDay;
    }
}
