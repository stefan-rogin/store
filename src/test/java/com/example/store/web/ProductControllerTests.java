package com.example.store.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void listAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    @Transactional
    void getById() throws Exception {
        mockMvc.perform(get("/products/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Two"));
    }

    @Test
    @Transactional
    void failGetByIdMissing() throws Exception {
        mockMvc.perform(get("/products/4"))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
        mockMvc.perform(post("/products")
                .header("Content-type", "application/json")
                .content("{\"name\": \"Four\"}")
            )
            .andExpect(status().isOk());
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(4)));    
    }

    @Test
    @Transactional
    void failCreateProductIncomplete() throws Exception {
        mockMvc.perform(post("/products")
                .header("Content-type", "application/json")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateFullProduct() throws Exception {
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ThreeChanged"));
        mockMvc.perform(get("/products/3"))
            .andExpect(jsonPath("$.name").value("ThreeChanged"));
    }

    @Test
    @Transactional
    void failUpdateFullProductMissing() throws Exception {
        mockMvc.perform(put("/products/4")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\"}")
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void failUpdateFullProductIncomplete() throws Exception {
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteProductById() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
        mockMvc.perform(delete("/products/2")).andExpect(status().isOk());
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    @Transactional
    void deleteProductByIdIgnoresMissing() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
        mockMvc.perform(delete("/products/4")).andExpect(status().isOk());
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }
}
