package com.lld.phase2.solid.isp.problem2;

import com.lld.phase2.solid.stubs.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// TODO: ISP VIOLATION — depends on full ProductRepository (10 methods) but uses only 3.
//
// Problems:
//   1. Any test must mock 7 irrelevant methods just to test catalog behavior
//   2. Swapping to Elasticsearch for catalog reads forces implementing all 10 methods
//   3. The dependency signature lies — implies write/admin/analytics access it doesn't need
//
// Your task: make ProductCatalogService depend on a focused read-only interface
// containing ONLY the 3 methods it actually uses.

@Service
public class ProductCatalogService {

    // VIOLATION: depends on full 10-method repository — 7 methods dragged in unnecessarily
    private final ProductRepository productRepository;

    public ProductCatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProduct(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
