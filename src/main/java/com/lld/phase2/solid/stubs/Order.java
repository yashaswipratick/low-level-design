package com.lld.phase2.solid.stubs;

import java.math.BigDecimal;

/** Stub domain object — used across multiple SOLID practice problems. */
public class Order {
    private final String id;
    private final String productId;
    private final int    quantity;
    private final BigDecimal total;

    public Order(String productId, int quantity, BigDecimal total) {
        this.id        = java.util.UUID.randomUUID().toString();
        this.productId = productId;
        this.quantity  = quantity;
        this.total     = total;
    }

    public static Order create(String productId, int quantity, BigDecimal price) {
        return new Order(productId, quantity, price);
    }

    public String     getId()        { return id; }
    public String     getProductId() { return productId; }
    public int        getQuantity()  { return quantity; }
    public BigDecimal getTotal()     { return total; }
}
