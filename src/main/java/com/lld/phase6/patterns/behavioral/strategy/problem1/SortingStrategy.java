package com.lld.phase6.patterns.behavioral.strategy.problem1;

import com.lld.phase2.solid.stubs.Product;

import java.util.List;

public interface SortingStrategy {
    List<Product> sort(List<Product> products);
}
