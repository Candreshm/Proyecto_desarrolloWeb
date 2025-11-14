/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda_vt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.tienda_vt.service.CategoriaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.context.MessageSource;
import com.tienda_vt.domain.Categoria;
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
@RequestMapping("/categoria")
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping("/listado")
    public String listado (Model model) {
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        
        return "/categoria/listado";
    
    }
    
    @Autowired
    private MessageSource messageSource;
    
    @PostMapping("/guardar")
    public String guardar (@Valid Categoria categoria, MultipartFile imagenFile, RedirectAttributes redirectAttributes) {
        categoriaService.save(categoria, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/categoria/listado";
    }
    
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCategoria, RedirectAttributes redirectAttributes) {
        String titulo="todoOk";
        String detalle="mensaje.eliminado";
        
        try {
            categoriaService.eliminar(idCategoria);
        } catch (IllegalArgumentException e) {
            titulo="error";
            detalle="categoria.error01";
        } catch (IllegalStateException e) {
            titulo="error";
            detalle="categoria.error02";
        } catch (Exception e) {
            titulo="error";
            detalle="categoria.error03";
        }
        
        redirectAttributes.addFlashAttribute(titulo,messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/categoria/listado";
    }
    
    @GetMapping("/modificar/{idCategoria}")
    public String modificar (@PathVariable("idCategoria") Integer idCategoria, RedirectAttributes redirectAttributes, Model model) {
        Optional<Categoria> categoriaOpt = categoriaService.getCategoria(idCategoria);
        
        if (categoriaOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("categoria.error01", null, Locale.getDefault()));
            return "redirect:/categoria/listado";
        }
        model.addAttribute("categoria", categoriaOpt.get());
        return "/categoria/modifica";
    }
    
}
