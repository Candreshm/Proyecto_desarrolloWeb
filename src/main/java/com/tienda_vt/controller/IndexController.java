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

    @Autowired
    private ProductoService productoService;
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "/index";
    }

    @GetMapping("/consultas/{idCategoria}")
    public String listado(@PathVariable("idCategoria") Integer idCategoria, Model model) {

        var categoriaOpt = categoriaService.getCategoria(idCategoria);
        if (categoriaOpt.isEmpty()) {
            //No encontro la categoria
            model.addAttribute("productos", java.util.Collections.EMPTY_LIST);
        } else {
            var categoria = categoriaOpt.get();
            model.addAttribute("productos", categoria.getProductos());
        }

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "/index";
    }
}
