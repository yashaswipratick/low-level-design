package com.lld.phase6.patterns.behavioral.strategy.problem1.impl;

import com.lld.phase2.solid.stubs.Product;
import com.lld.phase6.patterns.behavioral.strategy.problem1.SortingStrategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RelevanceSortingStrategy implements SortingStrategy {

    @Override
    public List<Product> sort(List<Product> products) {
        //sorts in ascending order
        List<Product> sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(Comparator.comparingInt(Product::getViewCount));
        return sortedProducts;
    }
}
