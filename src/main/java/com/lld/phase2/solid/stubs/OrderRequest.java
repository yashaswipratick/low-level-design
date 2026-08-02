package com.lld.phase2.solid.stubs;

import java.math.BigDecimal;

/** Stub — incoming HTTP request DTO for order placement. */
public class OrderRequest {
    private String     productId;
    private int        quantity;
    private BigDecimal price;
    private String     email;
    private String     phone;

    public String     getProductId()     { return productId; }
    public int        getQuantity()      { return quantity; }
    public BigDecimal getPrice()         { return price; }
    public String     getEmail()         { return email; }
    public String     getCustomerEmail() { return email; }
    public String     getCustomerPhone() { return phone; }
}
