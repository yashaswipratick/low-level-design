package com.lld.phase2.solid.isp.problem2;

import com.lld.phase2.solid.stubs.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: ISP VIOLATION — 10 methods spanning 4 different concerns in one interface.
//
// Who uses what:
//   ProductCatalogService   (read-only, public-facing) → findById, findAll, findByCategory
//   OrderService            (write)                    → save, delete
//   AnalyticsDashboard      (reporting)                → findTopSellers, countByCategory
//   ProductAdminService     (privileged)               → findLowStock, bulkUpdatePrice, count
//
// ProductCatalogService must depend on 7 methods it will NEVER use.
// Mocking this in a test requires stubbing all 10 methods.
// Swapping ProductCatalogService's backing store (e.g., to Elasticsearch)
// forces implementing all 10 methods even though only 3 are used.
//
// Your task: group the 10 methods by role and split into 4 focused interfaces.

public interface ProductRepository {

    // --- READ (3 methods) ---
    Optional<Product> findById(String id);
    List<Product>     findAll();
    List<Product>     findByCategory(String category);
    long              count();

    // --- WRITE (2 methods) ---
    void save(Product product);
    void delete(String id);

    // --- ANALYTICS (2 methods) ---
    List<Product>         findTopSellers(int limit);
    Map<String, Long>     countByCategory();

    // --- ADMIN (2 methods) ---
    List<Product> findLowStock(int threshold);
    void          bulkUpdatePrice(String category, BigDecimal priceFactor);
}
