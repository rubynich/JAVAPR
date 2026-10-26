package ru.edu.pr04;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    // Вариант 6: при объединении повторного товара суммарное количество не должно превышать 35.
    public static final int DUPLICATE_MERGE_LIMIT = 35;

    private final Employee employee;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(Employee employee) {
        this.employee = Objects.requireNonNull(employee, "Employee must not be null");
    }

    public Employee getEmployee() { return employee; }

    private void ensureDraft() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Confirmed order cannot be modified");
        }
    }

    public void addProduct(Product product, int quantity) {
        ensureDraft();
        Objects.requireNonNull(product, "Product must not be null");
        // Проверяем количество независимо от того, новый это товар или повторный.
        if (quantity < 1 || quantity > 100) {
            throw new IllegalArgumentException("Quantity must be between 1 and 100");
        }
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                if (!item.getProduct().getName().equals(product.getName())
                        || Double.compare(item.getProduct().getPrice(), product.getPrice()) != 0) {
                    throw new IllegalArgumentException("Conflicting data for product id: " + product.getId());
                }
                long mergedQuantity = (long) item.getQuantity() + quantity;
                if (mergedQuantity > DUPLICATE_MERGE_LIMIT) {
                    throw new IllegalArgumentException("Merged quantity for a repeated product must not exceed "
                            + DUPLICATE_MERGE_LIMIT);
                }
                item.increase(quantity);
                return;
            }
        }
        items.add(new OrderItem(product, quantity));
    }

    public void removeProduct(String productId) {
        ensureDraft();
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product id must not be empty");
        }
        boolean removed = items.removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) throw new IllegalArgumentException("Product not found in order: " + productId);
    }

    public double total() {
        return items.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }

    public void confirm() {
        ensureDraft();
        if (items.isEmpty()) throw new IllegalStateException("Cannot confirm an empty order");
        status = OrderStatus.CONFIRMED;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public OrderStatus getStatus() { return status; }
}
