package com.example.identityservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<String> getProducts() {
        return List.of(
                "Paracetamol",
                "Amoxicillin",
                "Vitamin C"
        );
    }
}