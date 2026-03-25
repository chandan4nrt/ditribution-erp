package com.ecomm.nrt.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ecomm.nrt.dto.request.ProductRequest;
import com.ecomm.nrt.dto.response.ProductResponse;
import com.ecomm.nrt.entity.Product;
import com.ecomm.nrt.repository.ProductRepository;
import com.ecomm.nrt.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    @SuppressWarnings("null")
    public ProductResponse saveProduct(ProductRequest productRequest) {
        Product product = modelMapper.map(productRequest, Product.class);
        return modelMapper.map(productRepository.save(product), ProductResponse.class);
    }

    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public void deleteProductById(long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void deleteProducts() {
        productRepository.deleteAll();
    }

    @Override
    @SuppressWarnings("null")
    public ProductResponse updateProductById(long id, ProductRequest productRequest) {
        // Find existing product first
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Use modelMapper to map source DTO TO the EXISTING entity.
        modelMapper.map(productRequest, existingProduct);

        // Save updated entity and map back to response DTO
        return modelMapper.map(productRepository.save(existingProduct), ProductResponse.class);
    }
}
