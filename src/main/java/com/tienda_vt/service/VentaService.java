package com.tienda_vt.service;

import com.tienda_vt.domain.Venta;
import com.tienda_vt.repository.VentaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {
    
    private final VentaRepository ventaRepository;
    
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
            product.put("name", row[0]); // productName
            product.put("quantity", ((Number) row[1]).longValue()); // totalQuantity
            product.put("revenue", row[2]); // totalRevenue
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
            daySale.put("date", row[0]); // saleDate (LocalDate)
            daySale.put("count", ((Number) row[1]).longValue()); // saleCount
            daySale.put("revenue", row[2]); // totalRevenue
            salesByDay.add(daySale);
        }
        
        return salesByDay;
    }
}
