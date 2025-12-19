/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tienda_vt.repository;

import com.tienda_vt.domain. Ruta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lrq2a
 */

@Repository
public interface RutaRepository extends JpaRepository <Ruta, Integer> {
    
    public List<Ruta> findAllByOrderByRequiereRolAsc();
    
}
