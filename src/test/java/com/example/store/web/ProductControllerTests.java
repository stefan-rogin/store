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
            .andExpect(jsonPath("$", Matchers.hasSize(3)))
            .andExpect(jsonPath("$[0].name").value("One"))
            .andExpect(jsonPath("$[0].price.amount").value(1.49))
            .andExpect(jsonPath("$[0].price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void getById() throws Exception {
        mockMvc.perform(get("/products/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Two"))
            .andExpect(jsonPath("$.price.amount").value(2.49))
            .andExpect(jsonPath("$.price.currency").value("EUR"));
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
                .content("{\"name\": \"Four\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4))
            .andExpect(jsonPath("$.name").value("Four"))
            .andExpect(jsonPath("$.price.amount").value(4.49))
            .andExpect(jsonPath("$.price.currency").value("EUR"));;
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(4)));    
    }

    @Test
    @Transactional
    void failCreateProductIncomplete() throws Exception {
        mockMvc.perform(post("/products")
            .header("Content-type", "application/json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateFullProduct() throws Exception {
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ThreeChanged"));
        mockMvc.perform(get("/products/3"))
            .andExpect(jsonPath("$.name").value("ThreeChanged"));
    }

    @Test
    @Transactional
    void failUpdateFullProductMissing() throws Exception {
        mockMvc.perform(put("/products/33")
                .header("Content-type", "application/json")
                .content("{\"name\": \"Four\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }")
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
        mockMvc.perform(delete("/products/2")).andExpect(status().isNoContent());
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    @Transactional
    void deleteProductByIdIgnoresMissing() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
        mockMvc.perform(delete("/products/4")).andExpect(status().isNoContent());
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }
}
