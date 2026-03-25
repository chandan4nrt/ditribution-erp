package com.ecomm.nrt.service;

import java.util.List;
import com.ecomm.nrt.dto.request.ProductRequest;
import com.ecomm.nrt.dto.response.ProductResponse;

public interface ProductService {
    ProductResponse saveProduct(ProductRequest productRequest);

    List<ProductResponse> getProducts();

    ProductResponse getProductById(long id);

    void deleteProductById(long id);

    void deleteProducts();

    ProductResponse updateProductById(long id, ProductRequest productRequest);
}
