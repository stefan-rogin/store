package com.example.store.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static final int MAX_NAME_SIZE = 2000;

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
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("Four"))
            .andExpect(jsonPath("$.price.amount").value(4.49))
            .andExpect(jsonPath("$.price.currency").value("EUR"));
        mockMvc.perform(get("/products"))
            .andExpect(jsonPath("$", Matchers.hasSize(4)));    
    }

    @Test
    @Transactional
    void createProductWithRounding() throws Exception {
        mockMvc.perform(post("/products")
                .header("Content-type", "application/json")
                .content("{\"name\": \"Four\", \"price\": {\"amount\": 4.495, \"currency\": \"EUR\"} }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price.amount").value(4.5));
    }

    @Test
    @Transactional
    void failCreateProductIncomplete() throws Exception {
        mockMvc.perform(post("/products")
            .header("Content-type", "application/json"))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/products")
            .header("Content-type", "application/json")
            .content("{\"name\": \"One\", \"price\": {\"amount\": 4.49} }"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void failCreateProductOtherCases() throws Exception {
        String tooLong = "a".repeat(MAX_NAME_SIZE + 1);
        String whiteSpace = " ";
        mockMvc.perform(post("/products")
            .header("Content-type", "application/json")
            .content("{\"name\": \"" + tooLong + "\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }"))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/products")
            .header("Content-type", "application/json")
            .content("{\"name\": \"" + whiteSpace + "\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }"))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/products")
            .header("Content-type", "application/json")
            .content("{\"name\": \"Negative\", \"price\": {\"amount\": -4.49, \"currency\": \"EUR\"} }"))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/products")
            .header("Content-type", "application/json")
            .content("{\"name\": \"Literal\", \"price\": {\"amount\": \"a\", \"currency\": \"EUR\"} }"))
            .andExpect(status().isBadRequest());    
    }

    @Test
    @Transactional
    void updateProduct() throws Exception {
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ThreeChanged"))
            .andExpect(jsonPath("$.price.amount").value(4.49))
            .andExpect(jsonPath("$.price.currency").value("EUR"));
        mockMvc.perform(get("/products/3"))
            .andExpect(jsonPath("$.name").value("ThreeChanged"));
    }

    @Test
    @Transactional
    void updateProductWithWhitespace() throws Exception {
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ThreeChanged"))
            .andExpect(jsonPath("$.price.amount").value(4.49))
            .andExpect(jsonPath("$.price.currency").value("EUR"));
        mockMvc.perform(get("/products/3"))
            .andExpect(jsonPath("$.name").value("ThreeChanged"));
    }

    @Test
    @Transactional
    void updateProductPrice() throws Exception {
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"price\": {\"amount\": 3.39, \"currency\": \"EUR\"} }")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ThreeChanged"))
            .andExpect(jsonPath("$.price.amount").value(3.39))
            .andExpect(jsonPath("$.price.currency").value("EUR"));
        mockMvc.perform(get("/products/3"))
            .andExpect(jsonPath("$.name").value("ThreeChanged"))
            .andExpect(jsonPath("$.price.amount").value(3.39))
            .andExpect(jsonPath("$.price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void updateProductPriceWithRounding() throws Exception {
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"price\": {\"amount\": 33.395, \"currency\": \"EUR\"} }")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ThreeChanged"))
            .andExpect(jsonPath("$.price.amount").value(33.4))
            .andExpect(jsonPath("$.price.currency").value("EUR"));
        mockMvc.perform(get("/products/3"))
            .andExpect(jsonPath("$.name").value("ThreeChanged"))
            .andExpect(jsonPath("$.price.amount").value(33.4))
            .andExpect(jsonPath("$.price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void failUpdateProductMissing() throws Exception {
        mockMvc.perform(put("/products/33")
                .header("Content-type", "application/json")
                .content("{\"name\": \"Four\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }")
            )
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void failUpdateProductIncomplete() throws Exception {
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
                .content("{\"name\": \"Four\", \"price\": {\"amount\": 4.49} }"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/products/3")
                .header("Content-type", "application/json")
                .content("{\"name\": \"\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void patchPrice() throws Exception {
        mockMvc.perform(patch("/products/3/price")
                .header("Content-type", "application/json")
                .content("{\"amount\": 3.99, \"currency\": \"EUR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.amount").value(3.99));
        mockMvc.perform(patch("/products/2/price")
                .header("Content-type", "application/json")
                .content("{\"amount\": 2, \"currency\": \"RON\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("price.amount").value(2))
                .andExpect(jsonPath("price.currency").value("RON"));
    }

    @Test
    @Transactional
    void failPatchPrice() throws Exception {
        mockMvc.perform(patch("/products/3/price")
                .header("Content-type", "application/json")
                .content("{\"price\": {\"amount\": 0.2, \"currency\": \"RON\"}}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/products/3/price")
                .header("Content-type", "application/json")
                .content("{\"price\": {\"amount\": 0.2}}"))
                .andExpect(status().isBadRequest());    
        mockMvc.perform(patch("/products/3/price")
                .header("Content-type", "application/json")
                .content("{\"price\": {\"currency\": \"RON\"}}"))
                .andExpect(status().isBadRequest());    
    }

    @Test
    @Transactional
    void patchName() throws Exception {
        mockMvc.perform(patch("/products/3/name")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ThreeChanged"));
    }

    @Test
    @Transactional
    void patchNameIgnoresPrice() throws Exception {
        mockMvc.perform(patch("/products/3/name")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"price\": {\"amount\": 4.99, \"currency\": \"RON\"} }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ThreeChanged"))
                .andExpect(jsonPath("$.price.amount").value(3.49))
                .andExpect(jsonPath("$.price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void failPatchName() throws Exception {
        mockMvc.perform(patch("/products/3/name")
                .header("Content-type", "application/json")
                .content("{\"name\": \" \"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/products/3/name")
                .header("Content-type", "application/json")
                .content("NewName"))
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
