package com.ecomm.nrt.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecomm.nrt.dto.request.ProductRequest;
import com.ecomm.nrt.dto.response.ProductResponse;
import com.ecomm.nrt.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        ProductResponse savedProduct = productService.saveProduct(productRequest);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getAProduct(@PathVariable long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable long id) {
        productService.deleteProductById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProducts() {
        productService.deleteProducts();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProductById(
            @PathVariable long id,
            @RequestBody @Valid ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProductById(id, productRequest));
    }
}
