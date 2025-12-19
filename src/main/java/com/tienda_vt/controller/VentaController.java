package com.tienda_vt.controller;

import com.tienda_vt.service.VentaService;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VentaController {
    
    private final VentaService ventaService;
    
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }
    
    @GetMapping("/venta/listado")
    public String listado(Model model) {
        // Get all sales
        model.addAttribute("ventas", ventaService.getAllSales());
        
        // Get top selling products for chart
        List<Map<String, Object>> topProducts = ventaService.getTopSellingProducts();
        model.addAttribute("topProducts", topProducts);
        
        // Get sales by day for chart
        List<Map<String, Object>> salesByDay = ventaService.getSalesByDay();
        model.addAttribute("salesByDay", salesByDay);
        
        return "venta/listado";
    }
}
