package com.lld.phase2.solid.stubs;

import java.util.Optional;

/** Stub product repository — used in DIP problems (not the violation class in ISP p2). */
public interface ProductRepository {
    Optional<Product> findById(String id);
    void save(Product product);
}
