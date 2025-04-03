package com.example.store.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Root controller for redirecting to the products page.
 */
@RestController
public class ApiController {

    @GetMapping("/")
    public RedirectView redirectToProducts() {
        return new RedirectView("/products");
    }
}
