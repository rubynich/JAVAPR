package ru.edu.pr04;

import java.util.Objects;

public class OrderService {
    public Order createOrder(Employee e) {
        return new Order(Objects.requireNonNull(e, "Employee must not be null"));
    }

    public void validateForConfirm(Order order) {
        Objects.requireNonNull(order, "Order must not be null");
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot confirm an empty order");
        }
        if (!Double.isFinite(order.total()) || order.total() <= 0) {
            throw new IllegalStateException("Order total must be positive");
        }
    }

    public void confirmOrder(Order order) {
        validateForConfirm(order);
        order.confirm();
    }
}
