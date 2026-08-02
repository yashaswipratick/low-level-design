package com.lld.phase6.patterns.behavioral.strategy.problem1.impl;

import com.lld.phase2.solid.stubs.Product;
import com.lld.phase6.patterns.behavioral.strategy.problem1.SortingStrategy;

import java.util.List;

public class ProductService {
    private SortingStrategy sortingStrategy;

    public ProductService(SortingStrategy sortingStrategy) {
        this.sortingStrategy = sortingStrategy;
    }

    public void setSortingStrategy(SortingStrategy sortingStrategy) {
        this.sortingStrategy = sortingStrategy;
    }

    public List<Product> sort(List<Product> products) {
       return sortingStrategy.sort(products);
    }
}
