package com.lld.phase6.patterns.creational.builder.problem1;

import java.util.ArrayList;
import java.util.List;

public class Pizza {

    // Required fields — always set
    private final String size;
    private final String crust;

    // Optional fields — have defaults
    private final String sauce;
    private final boolean hasCheese;
    private final List<String> toppings;

    // Private constructor — only Builder can create Pizza
    private Pizza(PizzaBuilder builder) {
        this.size = builder.size;
        this.crust = builder.crust;
        this.sauce = builder.sauce;
        this.hasCheese = builder.hasCheese;
        this.toppings = List.copyOf(builder.toppings);
    }

    @Override
    public String toString() {
        return size + " pizza | Crust: " + crust + " | Sauce: " + sauce
                + " | Cheese: " + hasCheese + " | Toppings: " + toppings;
    }

    public static class PizzaBuilder {
        private String size;
        private String crust;

        // Optional — with defaults
        private String sauce = "tomato";
        private boolean hasCheese = true;
        private List<String> toppings = new ArrayList<>();

        public PizzaBuilder(String size, String crust) {
            if (size == null || crust == null) throw new IllegalArgumentException("size and crust are required");
            this.size = size;
            this.crust = crust;
        }

        public PizzaBuilder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }

        public PizzaBuilder noCheese() {
            this.hasCheese = false;
            return this;
        }

        public PizzaBuilder addTopping(String topping) {
            this.toppings.add(topping);
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}
