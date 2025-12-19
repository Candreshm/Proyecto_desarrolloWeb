/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda_vt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.tienda_vt.service.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import com.tienda_vt.service.CategoriaService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 *
 * @author lrq2a
 */
@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ConsultaController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) String buscar,
                          @RequestParam(required = false) String keywords,
                          @RequestParam(required = false) BigDecimal precioInf,
                          @RequestParam(required = false) BigDecimal precioSup,
                          @RequestParam(required = false) String ubicacion,
                          @RequestParam(required = false) Integer existenciasMin,
                          @RequestParam(required = false) Integer existenciasMax,
                          Model model) {
        
        List<com.tienda_vt.domain.Producto> productos;
        
        // Check if any advanced filter is being used
        boolean hasAdvancedFilters = (precioInf != null && precioSup != null) ||
                                     (ubicacion != null && !ubicacion.trim().isEmpty()) ||
                                     (existenciasMin != null) ||
                                     (existenciasMax != null) ||
                                     (keywords != null && !keywords.trim().isEmpty());
        
        if (hasAdvancedFilters || (buscar != null && !buscar.trim().isEmpty())) {
            // Use advanced search
            productos = productoService.busquedaAvanzada(
                buscar != null ? buscar : keywords, // Use keywords if buscar is empty
                precioInf,
                precioSup,
                ubicacion,
                existenciasMin,
                existenciasMax
            );
            
            // If keywords are provided separately, filter by multiple keywords
            if (keywords != null && !keywords.trim().isEmpty()) {
                List<com.tienda_vt.domain.Producto> keywordResults = productoService.buscarPorKeywords(keywords);
                // Intersection with existing results
                productos.retainAll(keywordResults);
            }
        } else {
            // No filters, show all products
            productos = productoService.getProductos(false);
        }
        
        model.addAttribute("productos", productos);
        model.addAttribute("buscar", buscar);
        model.addAttribute("keywords", keywords);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        model.addAttribute("ubicacion", ubicacion);
        model.addAttribute("existenciasMin", existenciasMin);
        model.addAttribute("existenciasMax", existenciasMax);
        
        // Add categories for tabs
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        
        return "/consultas/listado";
    }
}
