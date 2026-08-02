package com.lld.phase2.solid.lsp.problem3;

import com.lld.phase2.solid.stubs.Order;

import java.util.Optional;

// TODO: LSP VIOLATION — "implements" OrderRepository but doesn't honor its contract.
//
// Contract of save():    persist the order so findById() can return it later.
// Contract of findById(): return the order if it was previously saved.
//
// LoggingOrderRepository.save() only prints — nothing is actually persisted.
// findById() always returns empty — even immediately after "save".
//
// Silent runtime bug:
//   OrderRepository repo = featureFlag.isDryRun()
//       ? new LoggingOrderRepository()   // compiles fine
//       : new JpaOrderRepository();
//
//   repo.save(order);
//   Order found = repo.findById(order.getId()).orElseThrow();  // throws in dry-run!
//
// Your task: redesign using the Decorator pattern.
// LoggingOrderRepository should WRAP a real OrderRepository, add logging ON TOP,
// then DELEGATE to the inner repo so the contract is always honored.

public class LoggingOrderRepository implements OrderRepository {

    @Override
    public void save(Order order) {
        // VIOLATION: does NOT persist — only logs. Contract broken.
        System.out.println("[DRY RUN] Would save order: " + order.getId());
    }

    @Override
    public Optional<Order> findById(String id) {
        // VIOLATION: always empty because save() never actually persisted anything
        System.out.println("[DRY RUN] Would find order: " + id);
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        // VIOLATION: does nothing
        System.out.println("[DRY RUN] Would delete order: " + id);
    }
}
