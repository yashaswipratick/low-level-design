package com.lld.phase2.solid.stubs;

/** Stub domain Product — used in ISP and DIP problems. */
public class Product {
    private final String id;
    private final String name;
    private final String category;
    private int viewCount;
    private int stock;
    private int price;
    private int ratings;

    public Product(String id, String name, String category, int stock, int price, int ratings) {
        this.id       = id;
        this.name     = name;
        this.category = category;
        this.stock    = stock;
        this.price     = price;
        this.ratings  = ratings;
    }

    public void incrementViewCount()         { this.viewCount++; }
    public void decrementStock(int quantity) { this.stock -= quantity; }

    public String getId()        { return id; }
    public String getName()      { return name; }
    public String getCategory()  { return category; }
    public int    getViewCount() { return viewCount; }
    public int    getStock()     { return stock; }
    public int getPrice() {
        return price;
    }
    public int getRatings() {
        return ratings;
    }
}
