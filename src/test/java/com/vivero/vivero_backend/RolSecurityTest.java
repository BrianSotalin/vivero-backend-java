package com.vivero.vivero_backend;



import com.vivero.vivero_backend.api.config.JwtUtil;
import com.vivero.vivero_backend.api.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RolSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }


    // Genera un token para cada rol
    private String tokenParaRol(String rol) {
        Usuario u = new Usuario();
        u.setUsername("test_" + rol.toLowerCase());
        u.setRol(rol);
        return jwtUtil.generateTokenJWT(u);
    }

    // ─── ADMIN: acceso total ───────────────────────────────────────────

    @Test
    void admin_puedeAccederAUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Bearer " + tokenParaRol("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void admin_puedeAccederAProductos() throws Exception {
        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer " + tokenParaRol("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void admin_puedeAccederAClientes() throws Exception {
        mockMvc.perform(get("/api/clientes")
                .header("Authorization", "Bearer " + tokenParaRol("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void admin_puedeAccederAVentas() throws Exception {
        mockMvc.perform(get("/api/ventas")
                .header("Authorization", "Bearer " + tokenParaRol("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void admin_puedeAccederAEstadisticas() throws Exception {
        mockMvc.perform(get("/api/estadisticas/resumen")
                .header("Authorization", "Bearer " + tokenParaRol("ADMIN")))
                .andExpect(status().isOk());
    }

    // ─── USER: todo menos usuarios ────────────────────────────────────

    @Test
    void user_puedeAccederAProductos() throws Exception {
        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer " + tokenParaRol("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void user_puedeAccederAClientes() throws Exception {
        mockMvc.perform(get("/api/clientes")
                .header("Authorization", "Bearer " + tokenParaRol("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void user_puedeAccederAVentas() throws Exception {
        mockMvc.perform(get("/api/ventas")
                .header("Authorization", "Bearer " + tokenParaRol("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void user_noPuedeAccederAUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Bearer " + tokenParaRol("USER")))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    void user_puedeAccederAEstadisticas() throws Exception {
        mockMvc.perform(get("/api/estadisticas/resumen")
                .header("Authorization", "Bearer " + tokenParaRol("USER")))
                .andExpect(status().isOk());
    }

    // ─── EMPLOYEE: solo ventas ────────────────────────────────────────

    @Test
    void employee_puedeAccederAVentas() throws Exception {
        mockMvc.perform(get("/api/ventas")
                .header("Authorization", "Bearer " + tokenParaRol("EMPLOYEE")))
                .andExpect(status().isOk());
    }

    @Test
    void employee_noPuedeAccederAUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Bearer " + tokenParaRol("EMPLOYEE")))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    void employee_noPuedeAccederAProductos() throws Exception {
        mockMvc.perform(get("/api/productos")
                .header("Authorization", "Bearer " + tokenParaRol("EMPLOYEE")))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    void employee_noPuedeAccederAClientes() throws Exception {
        mockMvc.perform(get("/api/clientes")
                .header("Authorization", "Bearer " + tokenParaRol("EMPLOYEE")))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    void employee_noPuedeAccederAEstadisticas() throws Exception {
        mockMvc.perform(get("/api/estadisticas/resumen")
                .header("Authorization", "Bearer " + tokenParaRol("EMPLOYEE")))
                .andExpect(status().isForbidden()); // 403
    }

    // ─── Sin token: todo bloqueado ────────────────────────────────────

    @Test
    void sinToken_noPuedeAccederANada() throws Exception {
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isForbidden());
    }
}