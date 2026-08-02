package com.lld.phase6.patterns.behavioral.strategy.problem1;

import com.lld.phase2.solid.stubs.Product;
import com.lld.phase6.patterns.behavioral.strategy.problem1.impl.*;

import java.util.Arrays;
import java.util.List;

public class ProductServiceDriver {

    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
                new Product("1", "Zebra Pen",   "Stationery", 100, 30,  4),
                new Product("2", "Apple Juice", "Beverages",  50,  120, 5),
                new Product("3", "Mango Juice", "Beverages",  80,  80,  3),
                new Product("4", "Bolt Shoes",  "Footwear",   30,  999, 5),
                new Product("5", "Ant Spray",   "Home",       60,  250, 2)
        );

        ProductService service = new ProductService(new NameSortingStrategy());

        System.out.println("=== Sort by NAME ===");
        service.setSortingStrategy(new NameSortingStrategy());
        service.sort(products)
               .forEach(p -> System.out.println(p.getName() + " | price=" + p.getPrice() + " | rating=" + p.getRatings()));

        System.out.println("\n=== Sort by PRICE ===");
        service.setSortingStrategy(new PriceSortingStrategy());
        service.sort(products)
               .forEach(p -> System.out.println(p.getName() + " | price=" + p.getPrice()));

        System.out.println("\n=== Sort by RATINGS ===");
        service.setSortingStrategy(new RatingSortingStrategy());
        service.sort(products)
               .forEach(p -> System.out.println(p.getName() + " | rating=" + p.getRatings()));

        System.out.println("\n=== Sort by RELEVANCE ===");
        service.setSortingStrategy(new RelevanceSortingStrategy());
        service.sort(products)
               .forEach(p -> System.out.println(p.getName() + " | viewCount=" + p.getViewCount()));
    }
}

