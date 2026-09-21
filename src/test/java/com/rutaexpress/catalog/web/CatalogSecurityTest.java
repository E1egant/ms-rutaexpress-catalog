package com.rutaexpress.catalog.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Perfil `secure`: exige JWT; crear servicios solo para Admin. Los tokens se simulan con jwt(). */
@SpringBootTest(properties = {"AZURE_TENANT_ID=test-tenant", "AZURE_CLIENT_ID=test-client"})
@AutoConfigureMockMvc
@ActiveProfiles("secure")
class CatalogSecurityTest {

    private static final String BODY =
            "{\"name\":\"Seguro\",\"basePrice\":1,\"pricePerKm\":1,\"pricePerKg\":1,\"estimatedHours\":1}";

    @Autowired
    MockMvc mvc;

    @Test
    void sinTokenRetorna401() throws Exception {
        mvc.perform(get("/api/catalog/services")).andExpect(status().isUnauthorized());
    }

    @Test
    void cualquierUsuarioAutenticadoPuedeListar() throws Exception {
        mvc.perform(get("/api/catalog/services").with(jwt())).andExpect(status().isOk());
    }

    @Test
    void operadorNoPuedeCrearServicios() throws Exception {
        mvc.perform(post("/api/catalog/services").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Operador")))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPuedeCrearServicios() throws Exception {
        mvc.perform(post("/api/catalog/services").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Admin")))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated());
    }
}
