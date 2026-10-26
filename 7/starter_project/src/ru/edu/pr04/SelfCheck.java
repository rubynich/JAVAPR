package ru.edu.pr04;

public class SelfCheck {
    private static int passed = 0;

    private static void check(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS: " + name);
        } catch (Throwable e) {
            System.out.println("FAIL: " + name + " — " + e.getMessage());
        }
    }

    private static void expectException(Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
            throw new AssertionError("Expected exception: " + type.getSimpleName());
        } catch (Throwable e) {
            if (!type.isInstance(e)) throw new AssertionError("Expected " + type.getSimpleName()
                    + ", got " + e.getClass().getSimpleName());
        }
    }

    public static void main(String[] args) {
        Product mouse = new Product("P1", "Mouse", 1000);
        Employee employee = new Employee("E1", "Иванов Иван");

        check("корректный товар", () -> {
            if (mouse.getPrice() != 1000) throw new AssertionError();
        });
        check("отрицательная цена отклоняется", () ->
                expectException(IllegalArgumentException.class, () -> new Product("P2", "Keyboard", -1)));
        check("количество 1 принимается", () -> new OrderItem(mouse, 1));
        check("количество 100 принимается", () -> new OrderItem(mouse, 100));
        check("количество 0 отклоняется", () ->
                expectException(IllegalArgumentException.class, () -> new OrderItem(mouse, 0)));
        check("количество 101 отклоняется", () ->
                expectException(IllegalArgumentException.class, () -> new OrderItem(mouse, 101)));
        check("повторный товар объединяется", () -> {
            Order order = new Order(employee);
            order.addProduct(mouse, 2);
            order.addProduct(mouse, 3);
            if (order.getItems().size() != 1 || order.getItems().get(0).getQuantity() != 5)
                throw new AssertionError("Repeated product was not merged");
        });
        check("вариант 6: объединение до 35 разрешено", () -> {
            Order order = new Order(employee);
            order.addProduct(mouse, 20);
            order.addProduct(mouse, 15);
            if (order.getItems().get(0).getQuantity() != 35) throw new AssertionError();
        });
        check("вариант 6: объединение выше 35 запрещено", () -> {
            Order order = new Order(employee);
            order.addProduct(mouse, 20);
            expectException(IllegalArgumentException.class, () -> order.addProduct(mouse, 16));
        });
        check("итоговая стоимость рассчитывается", () -> {
            Order order = new Order(employee);
            order.addProduct(mouse, 2);
            if (order.total() != 2000) throw new AssertionError("Expected 2000, got " + order.total());
        });
        check("после подтверждения нельзя добавить товар", () -> {
            Order order = new Order(employee);
            order.addProduct(mouse, 1);
            order.confirm();
            expectException(IllegalStateException.class, () -> order.addProduct(mouse, 1));
        });
        check("внешний код не может очистить список позиций", () -> {
            Order order = new Order(employee);
            order.addProduct(mouse, 1);
            try {
                order.getItems().clear();
                throw new AssertionError("List should be immutable");
            } catch (UnsupportedOperationException expected) { }
        });

        System.out.println("Итого: " + passed + " проверок успешно.");
        if (passed != 12) throw new AssertionError("Some checks failed");
    }
}
