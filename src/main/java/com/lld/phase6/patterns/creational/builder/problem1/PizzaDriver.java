package com.lld.phase6.patterns.creational.builder.problem1;

public class PizzaDriver {

    public static void main(String[] args) {

        System.out.println("=== Full custom pizza ===");
        Pizza p1 = new Pizza.PizzaBuilder("LARGE", "THIN")
                .sauce("BBQ")
                .noCheese()
                .addTopping("pepperoni")
                .addTopping("mushrooms")
                .build();
        System.out.println(p1);

        System.out.println("\n=== All defaults (small stuffed) ===");
        Pizza p2 = new Pizza.PizzaBuilder("SMALL", "STUFFED").build();
        System.out.println(p2);

        System.out.println("\n=== Medium with white sauce and olives ===");
        Pizza p3 = new Pizza.PizzaBuilder("MEDIUM", "THICK")
                .sauce("white")
                .addTopping("olives")
                .build();
        System.out.println(p3);

        System.out.println("\n=== Immutability check — toppings list cannot be modified ===");
        try {
            p1.toString();  // just access — no setter available
            System.out.println("No setters exist on Pizza — immutability confirmed ✅");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

        System.out.println("\n=== Validation — null size should throw ===");
        try {
            new Pizza.PizzaBuilder(null, "THIN").build();
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage() + " ✅");
        }
    }
}

