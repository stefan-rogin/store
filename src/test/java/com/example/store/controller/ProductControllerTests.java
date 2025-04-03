package com.example.store.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.example.store.security.config.TestSecurityConfig;

import jakarta.transaction.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("IntegrationTest")
@ContextConfiguration(classes = { TestSecurityConfig.class })
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static final int MAX_NAME_SIZE = 2000;
    private static final String UUID_ONE = "33b5785c-8d8a-4301-b5b3-b07b67347173";
    private static final String UUID_TWO = "a18920fb-56cd-41c5-8264-ed617c038524";
    private static final String UUID_THREE = "410d3bbb-67f1-479c-81b0-a852e6579eb4";
    private static final String UUID_FOUR = "6887bf30-c00c-4897-8423-c155bbff076b";
    private static final String UUID_MISS = "830e2825-67c5-4bec-8bda-eb16b4c03c50";

    @Test
    @Transactional
    void listAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(33))
                .andExpect(jsonPath("$.content[0].name").value("Annibale Colombo Bed"))
                .andExpect(jsonPath("$.content[0].price.amount").value(1899.99))
                .andExpect(jsonPath("$.content[0].price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void findById() throws Exception {
        mockMvc.perform(get("/products/" + UUID_TWO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Two"))
                .andExpect(jsonPath("$.id").value(UUID_TWO))
                .andExpect(jsonPath("$.price.amount").value(2.49))
                .andExpect(jsonPath("$.price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void failFindByIdMissing() throws Exception {
        mockMvc.perform(get("/products/" + UUID_MISS))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void failFindByIdBad() throws Exception {
        mockMvc.perform(get("/products/not-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void searchWithResults() throws Exception {
        mockMvc.perform(get("/products/search?searchTerm=oil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Cooking Oil"))
                .andExpect(jsonPath("$.content[1].name").value("Engine Oil"))
                .andExpect(jsonPath("$.content[0].price.amount").value(4.99))
                .andExpect(jsonPath("$.content[0].price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void searchWithoutResults() throws Exception {
        mockMvc.perform(get("/products/search?search=Nothing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(jsonPath("$.totalElements").value(33));
        mockMvc.perform(post("/products")
                .header("Content-type", "application/json")
                .content("{\"name\": \"Four\", \"price\": {\"amount\": 4.49, \"currency\": \"EUR\"} }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Four"))
                .andExpect(jsonPath("$.price.amount").value(4.49))
                .andExpect(jsonPath("$.price.currency").value("EUR"));
        mockMvc.perform(get("/products"))
                .andExpect(jsonPath("$.totalElements").value(34));
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
    void upsertExistingProductButNotChangingId() throws Exception {
        mockMvc.perform(put("/products/" + UUID_THREE)
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"id\": \"" + UUID_MISS + "\", \"price\": {\"amount\": 3.39, \"currency\": \"RON\"} }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ThreeChanged"))
                .andExpect(jsonPath("$.id").value(UUID_THREE))
                .andExpect(jsonPath("$.price.amount").value(3.39))
                .andExpect(jsonPath("$.price.currency").value("RON"));
        mockMvc.perform(get("/products/" + UUID_THREE))
                .andExpect(jsonPath("$.name").value("ThreeChanged"))
                .andExpect(jsonPath("$.id").value(UUID_THREE))
                .andExpect(jsonPath("$.price.amount").value(3.39))
                .andExpect(jsonPath("$.price.currency").value("RON"));
    }

    @Test
    @Transactional
    void upsertNewProductIgnoresPayloadId() throws Exception {
        // Take the path id over the payload id
        mockMvc.perform(put("/products/" + UUID_FOUR)
                .header("Content-type", "application/json")
                .content("{\"name\": \"Four\", \"id\": \"" + UUID_MISS + "\", \"price\": {\"amount\": 4.39, \"currency\": \"EUR\"} }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Four"))
                .andExpect(jsonPath("$.id").value(UUID_FOUR))
                .andExpect(jsonPath("$.price.amount").value(4.39))
                .andExpect(jsonPath("$.price.currency").value("EUR"));
        mockMvc.perform(get("/products/" + UUID_FOUR))
                .andExpect(jsonPath("$.name").value("Four"))
                .andExpect(jsonPath("$.id").value(UUID_FOUR))
                .andExpect(jsonPath("$.price.amount").value(4.39))
                .andExpect(jsonPath("$.price.currency").value("EUR"));
    }   

    @Test
    @Transactional
    void updateProductPriceWithRounding() throws Exception {
        mockMvc.perform(put("/products/" + UUID_THREE)
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"price\": {\"amount\": 33.395, \"currency\": \"EUR\"} }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ThreeChanged"))
                .andExpect(jsonPath("$.price.amount").value(33.4))
                .andExpect(jsonPath("$.price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void failUpdateProductIncomplete() throws Exception {
        mockMvc.perform(put("/products/" + UUID_THREE)
                .header("Content-type", "application/json"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/products/" + UUID_THREE)
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\", \"price\": {\"amount\": 3.39} }"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/products/" + UUID_THREE)
                .header("Content-type", "application/json")
                .content("{\"name\": \"\", \"price\": {\"amount\": 3.39, \"currency\": \"RON\"} }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void patchPrice() throws Exception {
        mockMvc.perform(patch("/products/" + UUID_ONE + "/price")
                .header("Content-type", "application/json")
                .content("{\"amount\": 1.99, \"currency\": \"EUR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("One"))
                .andExpect(jsonPath("$.price.amount").value(1.99));
        mockMvc.perform(patch("/products/" + UUID_TWO + "/price")
                .header("Content-type", "application/json")
                .content("{\"amount\": 2, \"currency\": \"RON\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.amount").value(2))
                .andExpect(jsonPath("$.price.currency").value("RON"));
    }

    @Test
    @Transactional
    void failPatchPrice() throws Exception {
        mockMvc.perform(patch("/products/" + UUID_ONE + "/price")
                .header("Content-type", "application/json")
                .content("{\"price\": {\"amount\": 0.2, \"currency\": \"RON\"}}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/products/" + UUID_ONE + "/price")
                .header("Content-type", "application/json")
                .content("{\"price\": {\"amount\": 0.2}}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/products/" + UUID_ONE + "/price")
                .header("Content-type", "application/json")
                .content("{\"price\": {\"currency\": \"RON\"}}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void patchName() throws Exception {
        mockMvc.perform(patch("/products/" + UUID_THREE + "/name")
                .header("Content-type", "application/json")
                .content("{\"name\": \"ThreeChanged\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ThreeChanged"));
        mockMvc.perform(get("/products/" + UUID_THREE))
                .andExpect(jsonPath("$.name").value("ThreeChanged"));
    }

    @Test
    @Transactional
    void patchNameIgnoresPrice() throws Exception {
        mockMvc.perform(patch("/products/" + UUID_ONE + "/name")
                .header("Content-type", "application/json")
                .content("{\"name\": \"OneChanged\", \"price\": {\"amount\": 4.99, \"currency\": \"RON\"} }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("OneChanged"))
                .andExpect(jsonPath("$.id").value(UUID_ONE))
                .andExpect(jsonPath("$.price.amount").value(1.49))
                .andExpect(jsonPath("$.price.currency").value("EUR"));
    }

    @Test
    @Transactional
    void failPatchName() throws Exception {
        mockMvc.perform(patch("/products/" + UUID_ONE + "/name")
                .header("Content-type", "application/json")
                .content("{\"name\": \" \"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/products/" + UUID_ONE + "/name")
                .header("Content-type", "application/json")
                .content("NewName"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteProductById() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(jsonPath("$.totalElements").value(33));
        mockMvc.perform(delete("/products/" + UUID_ONE))
                .andExpect(status().isOk());
        mockMvc.perform(get("/products"))
                .andExpect(jsonPath("$.totalElements").value(32));
    }

    @Test
    @Transactional
    void deleteProductByIdIgnoresMissing() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(jsonPath("$.totalElements").value(33));
        mockMvc.perform(delete("/products/" + UUID_MISS))
                .andExpect(status().isOk());
        mockMvc.perform(get("/products"))
                .andExpect(jsonPath("$.totalElements").value(33));
    }

    @Test
    @Transactional
    void failDeleteMalformed() throws Exception {
        mockMvc.perform(delete("/products/abcd"))
                .andExpect(status().isBadRequest());
    }
}
