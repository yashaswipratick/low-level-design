package com.lld.phase2.solid.lsp.problem3;

import com.lld.phase2.solid.stubs.Order;

import java.util.Optional;

/** Order persistence interface — do not change. */
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
    void delete(String id);
}
