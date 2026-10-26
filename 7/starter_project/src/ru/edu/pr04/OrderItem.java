package ru.edu.pr04;

import java.util.Objects;

public class OrderItem {
    private final Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = Objects.requireNonNull(product, "Product must not be null");
        setQuantity(quantity);
    }

    private void setQuantity(int quantity) {
        if (quantity < 1 || quantity > 100) {
            throw new IllegalArgumentException("Quantity must be between 1 and 100");
        }
        this.quantity = quantity;
    }

    public void increase(int delta) {
        if (delta < 1) throw new IllegalArgumentException("Increase must be positive");
        long newQuantity = (long) quantity + delta;
        if (newQuantity > 100) throw new IllegalArgumentException("Quantity must not exceed 100");
        this.quantity = (int) newQuantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getLineTotal() { return product.getPrice() * quantity; }
}
