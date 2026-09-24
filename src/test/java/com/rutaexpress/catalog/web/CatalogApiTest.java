package com.rutaexpress.catalog.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    void actualizarTarifaExistenteRetorna200() throws Exception {
        String created = mvc.perform(post("/api/catalog/services").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Temporal\",\"basePrice\":100,\"pricePerKm\":5,\"pricePerKg\":2,\"estimatedHours\":24}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = new ObjectMapper().readTree(created).get("id").asLong();

        mvc.perform(put("/api/catalog/services/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Temporal\",\"basePrice\":200,\"pricePerKm\":5,\"pricePerKg\":2,\"estimatedHours\":24}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value(200));
    }

    @Test
    void actualizarServicioInexistenteRetorna404() throws Exception {
        mvc.perform(put("/api/catalog/services/999999").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"basePrice\":1,\"pricePerKm\":1,\"pricePerKg\":1,\"estimatedHours\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reservarCapacidadDescuentaDisponible() throws Exception {
        String created = mvc.perform(post("/api/catalog/fleet").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehicleType\":\"Test\",\"maxWeightKg\":100,\"maxVolumeM3\":10}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = new ObjectMapper().readTree(created).get("id").asLong();

        mvc.perform(post("/api/catalog/fleet/{id}/reserve", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weightKg\":30,\"volumeM3\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableWeightKg").value(70.0))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void reservarSinCapacidadRetorna409() throws Exception {
        String created = mvc.perform(post("/api/catalog/fleet").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehicleType\":\"Mini\",\"maxWeightKg\":5,\"maxVolumeM3\":1}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = new ObjectMapper().readTree(created).get("id").asLong();

        mvc.perform(post("/api/catalog/fleet/{id}/reserve", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weightKg\":10,\"volumeM3\":1}"))
                .andExpect(status().isConflict());
    }
}
