package com.tienda_vt;

import com.tienda_vt.domain.Ruta;
import com.tienda_vt.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //Este metodo genera el proceso de autorizacion...
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Lazy RutaService rutaService)
            throws Exception {
        
        var rutas = rutaService.getRutas();
        
        http.authorizeHttpRequests(requests -> {
                // Allow public access to products and categories views
                requests.requestMatchers("/producto/listado", "/producto/listado/**").permitAll();
                requests.requestMatchers("/categoria/listado", "/categoria/listado/**").permitAll();
                requests.requestMatchers("/consultas/listado", "/consultas/**").permitAll();
                
                // Process dynamic routes from database
                for (Ruta ruta : rutas){
                    if(ruta.isRequiereRol()){
                        requests.requestMatchers(ruta.getRuta()).hasRole(ruta.getRol().getRol());
                    } else {
                        requests.requestMatchers(ruta.getRuta()).permitAll();
                    }
                }
                // Require authentication for cart operations
                requests.requestMatchers("/carrito/**").authenticated();
                requests.anyRequest().authenticated();
        });

        http.formLogin(login -> login.loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll());

        http.logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll());

        http.exceptionHandling(exception -> exception.accessDeniedPage("/acceso_denegado"));
        http.sessionManagement(session -> session.maximumSessions(1)
                .maxSessionsPreventsLogin(false));

        return http.build();
    }
    
    //Metodo para crear el procedimiento de encriptacion
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
