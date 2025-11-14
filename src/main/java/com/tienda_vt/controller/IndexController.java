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
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author lrq2a
 */
@Controller
public class IndexController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    public IndexController(CategoriaService cSrv, ProductoService pSrv) {
        this.categoriaService = cSrv;
        this.productoService = pSrv;
    }

    @GetMapping({"/", "/inicio"})
    public String home(Model model) {
        // active categories (or all)
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        // pick some “populars” – adapt to your logic (e.g., by sales or just first N actives)
        var all = productoService.getProductos(true);
        model.addAttribute("productosPopulares", all.size() > 8 ? all.subList(0, 8) : all);
        return "index";
    }

}
