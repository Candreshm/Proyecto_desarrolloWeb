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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                          @RequestParam(required = false) BigDecimal precioInf,
                          @RequestParam(required = false) BigDecimal precioSup,
                          Model model) {
        var productos = productoService.getProductos(false);
        
        // Apply search filter if provided
        if (buscar != null && !buscar.trim().isEmpty()) {
            productos = productoService.buscarProductos(buscar);
        }
        
        // Apply price filter if provided
        if (precioInf != null && precioSup != null) {
            if (buscar != null && !buscar.trim().isEmpty()) {
                // Both search and price filter
                productos = productoService.buscarProductosPorPrecio(buscar, precioInf, precioSup);
            } else {
                // Only price filter
                productos = productoService.consultaDerivada(precioInf, precioSup);
            }
        }
        
        model.addAttribute("productos", productos);
        model.addAttribute("buscar", buscar);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        
        // Add categories for tabs
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        
        return "/consultas/listado";
    }
    
    // Remove the old consultaDerivada, consultaJPQL, consultaSQL methods
    // They are replaced by the combined search in listado method above

}
