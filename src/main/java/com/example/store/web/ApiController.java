package com.example.store.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class ApiController {

    @GetMapping("/")
    public RedirectView redirectToProducts() {
        return new RedirectView("/products");
    }
}
