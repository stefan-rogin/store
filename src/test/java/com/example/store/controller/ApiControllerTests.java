package com.example.store.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.store.security.config.TestSecurityConfig;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("IntegrationTest")
@ContextConfiguration(classes = {TestSecurityConfig.class})
class ApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

	@Test
	void getRoot() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is(302))
            .andExpect(header().string("Location", "/products"));
    }

}
