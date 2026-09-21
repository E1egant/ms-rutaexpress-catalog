package com.rutaexpress.catalog.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Perfil por defecto (dev): H2 en memoria, sin seguridad. */
@SpringBootTest
@AutoConfigureMockMvc
class CatalogApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void listaLosServiciosDelSeeder() throws Exception {
        mvc.perform(get("/api/catalog/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void listaLaFlota() throws Exception {
        mvc.perform(get("/api/catalog/fleet")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }

    @Test
    void servicioInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/catalog/services/999999")).andExpect(status().isNotFound());
    }

    @Test
    void crearServicioSinNombreRetorna400() throws Exception {
        mvc.perform(post("/api/catalog/services").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"basePrice\":1,\"pricePerKm\":1,\"pricePerKg\":1,\"estimatedHours\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearServicioValidoRetorna201() throws Exception {
        mvc.perform(post("/api/catalog/services").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nocturno\",\"basePrice\":2000,\"pricePerKm\":10,\"pricePerKg\":5,\"estimatedHours\":12}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nocturno"));
    }
}
