package ru.edu.pr04;

import java.util.Objects;

public class Product {
    private final String id;
    private final String name;
    private final double price;

    public Product(String id, String name, double price) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Product id must not be empty");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name must not be empty");
        if (!Double.isFinite(price) || price <= 0) throw new IllegalArgumentException("Product price must be positive");
        this.id = id.trim();
        this.name = name.trim();
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}
