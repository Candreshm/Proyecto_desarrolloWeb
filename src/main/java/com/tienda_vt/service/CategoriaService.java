/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda_vt.service;

import com.tienda_vt.domain.Categoria;
import com.tienda_vt.repository.CategoriaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.tienda_vt.service.FirebaseStorageService;
        
/**
 *
 * @author lrq2a
 */
@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Transactional(readOnly=true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }  
    
    @Transactional(readOnly=true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }
    
    @Transactional
    public void eliminar(Integer idCategoria) {
        if (!categoriaRepository.existsById(idCategoria)){
            throw new IllegalArgumentException("La categoria "+idCategoria+"no existe");
        }
        try {
            categoriaRepository.deleteById(idCategoria);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException ("no se puede eliminar porque tiene datos asociados"+e);
        }
    }

    @Autowired
    private FirebaseStorageService firebaseStorageService;
    
    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile) {
        categoria = categoriaRepository.save(categoria);
        if (!imagenFile.isEmpty()){
            try {
                String rutaImagen = firebaseStorageService.uploadImage(imagenFile, "categoria", categoria.getIdCategoria());
                categoria.setRutaImagen(rutaImagen);
                categoriaRepository.save(categoria);
            } catch (IOException e) {
            }
        }
    }
    
}
