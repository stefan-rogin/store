package com.example.store.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.store.web.response.ProductResponse;

@RestController
public class ApiController {

    @GetMapping("/")
    public ProductResponse getProduct() {
        return new ProductResponse(0L, "Not set");
    }
}
