package com.ecomm.nrt.master.service;

import com.ecomm.nrt.master.dto.ProductMasterRequest;
import com.ecomm.nrt.master.dto.ProductMasterResponse;
import com.ecomm.nrt.master.entity.ProductMaster;
import com.ecomm.nrt.master.repository.ProductMasterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductMasterService {

    private final ProductMasterRepository productMasterRepository;
    private final ModelMapper modelMapper;

    public Page<ProductMasterResponse> getAll(String search, Pageable pageable) {
        Page<ProductMaster> page = (search != null && !search.isBlank())
                ? productMasterRepository.searchActive(search, pageable)
                : productMasterRepository.findByIsActiveTrue(pageable);
        return page.map(p -> modelMapper.map(p, ProductMasterResponse.class));
    }

    public ProductMasterResponse getById(Long id) {
        return modelMapper.map(findOrThrow(id), ProductMasterResponse.class);
    }

    public List<ProductMasterResponse> getLowStockProducts() {
        return productMasterRepository.findLowStockProducts().stream()
                .map(p -> modelMapper.map(p, ProductMasterResponse.class))
                .toList();
    }

    @Transactional
    @SuppressWarnings("null")
    public ProductMasterResponse create(ProductMasterRequest request) {
        if (productMasterRepository.existsByCode(request.getCode()))
            throw new IllegalArgumentException("Product code already exists: " + request.getCode());
        ProductMaster product = modelMapper.map(request, ProductMaster.class);
        product.setIsActive(true);
        product.setCurrentStock(0);
        return modelMapper.map(productMasterRepository.save(product), ProductMasterResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public ProductMasterResponse update(Long id, ProductMasterRequest request) {
        ProductMaster product = findOrThrow(id);
        // preserve stock — only update master fields
        int currentStock = product.getCurrentStock();
        modelMapper.map(request, product);
        product.setCurrentStock(currentStock);
        return modelMapper.map(productMasterRepository.save(product), ProductMasterResponse.class);
    }

    @Transactional
    public void deactivate(Long id) {
        ProductMaster product = findOrThrow(id);
        product.setIsActive(false);
        productMasterRepository.save(product);
    }

    public ProductMaster findOrThrow(Long id) {
        java.util.Objects.requireNonNull(id, "Product ID must not be null");
        return productMasterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public void updateProductStock(Long productId, Integer quantity) {
        ProductMaster product = findOrThrow(productId);
        product.setCurrentStock(quantity);
        productMasterRepository.save(product);
    }
}
