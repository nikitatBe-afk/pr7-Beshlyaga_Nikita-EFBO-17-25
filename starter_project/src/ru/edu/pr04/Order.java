package ru.edu.pr04;
import java.util.*;
public class Order {
    private final Employee employee; private final List<OrderItem> items=new ArrayList<>(); private OrderStatus status=OrderStatus.DRAFT;
    public Order(Employee employee) { this.employee=Objects.requireNonNull(employee); }


public void addProduct(Product product, int quantity) {

    if (status == OrderStatus.CONFIRMED) {
        throw new IllegalStateException("Order is confirmed");
    }

    Objects.requireNonNull(product);

    if (quantity < 1 || quantity > 100) {
        throw new IllegalArgumentException("Invalid quantity");
    }

    for (OrderItem item : items) {
        if (item.getProduct().getId().equals(product.getId())) {

            item.increase(quantity);
            return;
        }
    }

    // ВАРИАНТ 4: максимум 10 уникальных позиций
    if (items.size() >= 10) {
        throw new IllegalStateException(
                "Order cannot contain more than 10 positions");
    }

    // Если товар новый и место есть — добавляем
    items.add(new OrderItem(product, quantity));
}



public void removeProduct(String productId) {

    if (status == OrderStatus.CONFIRMED) {
        throw new IllegalStateException("Order is confirmed");
    }

    Objects.requireNonNull(productId);

    Iterator<OrderItem> iterator = items.iterator();

    while (iterator.hasNext()) {
        OrderItem item = iterator.next();

        if (item.getProduct().getId().equals(productId)) {

            iterator.remove();
            return;
        }
    }
}


public double total() {
    double sum = 0;

    for (OrderItem item : items) {

        sum += item.getLineTotal();
    }

    return sum;
}


public void confirm() {

    if (status == OrderStatus.CONFIRMED) {
        throw new IllegalStateException("Order already confirmed");
    }

    if (items.isEmpty()) {
        throw new IllegalStateException("Order is empty");
    }

    this.status = OrderStatus.CONFIRMED;
}

    public List<OrderItem> getItems() { return List.copyOf(items); }
    public OrderStatus getStatus() { return status; }


public int addPositionCount(Order order) {

    // Проверяем, что заказ передан
    Objects.requireNonNull(order);

    // Возвращаем количество уникальных позиций
    return order.getItems().size();
}

}
