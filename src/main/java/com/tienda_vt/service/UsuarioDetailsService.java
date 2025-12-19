/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tienda_vt.service;

import com.tienda_vt.domain.Usuario;
import com.tienda_vt.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lrq2a
 */
@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final HttpSession session;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository, HttpSession session) {
        this.usuarioRepository = usuarioRepository;
        this.session = session;
    }

    //Este metodo busca el registro con el username del login en la tabla usuario
    //Si lo encuentra guarda la foto del usuario en una sesion y devuelve los roles del usuario
    @Override
    @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {

        //Se busca el usuario de ese username
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " +username));
        // Si estamos aqui... se encontro el usuario y se guarda la foto
        session.removeAttribute("imagenUsuario");
        session.setAttribute("imagenUsuario", usuario.getRutaImagen());
        
        //Se cargan los roles del usuario y se generan como roles de seguridad
        var roles = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_"+rol.getRol()))
                .collect(Collectors.toSet());
        
        //Se retorna el usuario con su rol
        return new User(usuario.getUsername(),usuario.getPassword(),roles);
        
    }
}
