/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda_vt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.tienda_vt.service.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.context.MessageSource;
import com.tienda_vt.domain.Producto;
import com.tienda_vt.service.CategoriaService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Locale;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
/**
 *
 * @author lrq2a
 */

@Controller
@RequestMapping("/producto")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping("/listado")
    public String listado (Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        
        return "/producto/listado";
    
    }
    
    @Autowired
    private MessageSource messageSource;
    
    @PostMapping("/guardar")
    public String guardar (@Valid Producto producto, MultipartFile imagenFile, RedirectAttributes redirectAttributes) {
        productoService.save(producto, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/producto/listado";
    }
    
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idProducto, RedirectAttributes redirectAttributes) {
        String titulo="todoOk";
        String detalle="mensaje.eliminado";
        
        try {
            productoService.eliminar(idProducto);
        } catch (IllegalArgumentException e) {
            titulo="error";
            detalle="producto.error01";
        } catch (IllegalStateException e) {
            titulo="error";
            detalle="producto.error02";
        } catch (Exception e) {
            titulo="error";
            detalle="producto.error03";
        }
        
        redirectAttributes.addFlashAttribute(titulo,messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/producto/listado";
    }
    
    @GetMapping("/modificar/{idProducto}")
    public String modificar (@PathVariable("idProducto") Integer idProducto, RedirectAttributes redirectAttributes, Model model) {
        Optional<Producto> productoOpt = productoService.getProducto(idProducto);
        
        if (productoOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("producto.error01", null, Locale.getDefault()));
            return "redirect:/producto/listado";
        }
        model.addAttribute("producto", productoOpt.get());
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "/producto/modifica";
    }
    
}
