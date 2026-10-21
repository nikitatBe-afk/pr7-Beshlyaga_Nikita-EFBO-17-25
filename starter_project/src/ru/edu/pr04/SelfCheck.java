
package ru.edu.pr04;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class SelfCheck {

    // Счётчики unit-тестов
    private static int passed = 0;
    private static int total = 0;

    // Проверка результата теста
    private static void check(boolean condition, String name) {
        total++;

        if (condition) {
            passed++;
            System.out.println("PASS " + total + " - " + name);
        } else {
            System.out.println("FAIL " + total + " - " + name);
        }
    }

    public static void main(String[] args) {

        System.out.println("=== UNIT TESTS ===");

        // Общие объекты
        Product p1 = new Product("P1", "Mouse", 1000);
        Employee employee = new Employee("E01", "Ivan Ivanov");
        OrderService service = new OrderService();

        // Тест 1: создание товара
        check(p1.getPrice() == 1000, "Product creation");

        // Тест 2: нулевая цена
        try {
            new Product("P2", "X", 0);
            check(false, "Zero price");
        } catch (IllegalArgumentException e) {
            check(true, "Zero price");
        }

        // Тест 3: отсутствующий ID
        try {
            new Product(null, "X", 100);
            check(false, "Null product ID");
        } catch (IllegalArgumentException e) {
            check(true, "Null product ID");
        }

        // Тест 4: корректное количество
        OrderItem item = new OrderItem(p1, 5);
        check(item.getQuantity() == 5, "OrderItem quantity");

        // Тест 5: превышение количества
        try {
            item.increase(96);
            check(false, "Quantity overflow");
        } catch (IllegalArgumentException e) {
            check(true, "Quantity overflow");
        }

        // Тест 6: стоимость позиции
        OrderItem item2 = new OrderItem(p1, 3);
        check(item2.getLineTotal() == 3000.0, "Line total");

        // Тест 7: добавление товара
        Order order1 = service.createOrder(employee);
        order1.addProduct(p1, 2);
        check(order1.getItems().size() == 1, "Add product");

        // Тест 8: объединение одинаковых товаров
        order1.addProduct(p1, 3);

        check(order1.getItems().size() == 1
                && order1.getItems().get(0).getQuantity() == 5,
                "Merge products");

        // Тест 9: запрет добавления после подтверждения
        order1.confirm();

        try {
            order1.addProduct(p1, 1);
            check(false, "Add after confirm");
        } catch (IllegalStateException e) {
            check(true, "Add after confirm");
        }

        // Тест 10: превышение при объединении
        Order order2 = service.createOrder(employee);
        order2.addProduct(p1, 50);

        try {
            order2.addProduct(p1, 51);
            check(false, "Merge overflow");
        } catch (IllegalArgumentException e) {
            check(true, "Merge overflow");
        }

        // Тест 11: удаление товара
        Order order3 = service.createOrder(employee);
        order3.addProduct(p1, 2);
        order3.removeProduct("P1");

        check(order3.getItems().size() == 0, "Remove product");

        // Тест 12: удаление отсутствующего товара
        order3.addProduct(p1, 2);
        order3.removeProduct("P99");

        check(order3.getItems().size() == 1,
                "Remove missing product");

        // Тест 13: сумма пустого заказа
        Order order4 = service.createOrder(employee);
        check(order4.total() == 0.0, "Empty order total");

        // Тест 14: общая стоимость
        Product p2 = new Product("P2", "Keyboard", 500);

        order4.addProduct(p1, 2);
        order4.addProduct(p2, 3);

        check(order4.total() == 3500.0, "Order total");

        // Тест 15: подтверждение заказа
        order4.confirm();

        check(order4.getStatus() == OrderStatus.CONFIRMED,
                "Confirm order");

        // Тест 16: повторное подтверждение
        try {
            order4.confirm();
            check(false, "Double confirm");
        } catch (IllegalStateException e) {
            check(true, "Double confirm");
        }

        // Тест 17: защита списка позиций
        try {
            order4.getItems().add(new OrderItem(p1, 1));
            check(false, "Immutable items");
        } catch (UnsupportedOperationException e) {
            check(true, "Immutable items");
        }

        // Тест 18: подтверждение пустого заказа
        Order order5 = service.createOrder(employee);

        try {
            order5.confirm();
            check(false, "Confirm empty order");
        } catch (IllegalStateException e) {
            check(true, "Confirm empty order");
        }

        // =========================================
        // ИНДИВИДУАЛЬНЫЙ ВАРИАНТ 4
        // =========================================

        System.out.println("\n=== VARIANT 4 TESTS ===");

        Order variantOrder = service.createOrder(employee);

        // Заполняем заказ десятью разными товарами
        for (int i = 1; i <= 10; i++) {
            Product product = new Product(
                    "V" + i, "Product " + i, 1000);

            variantOrder.addProduct(product, 1);
        }

        // Тест 19: максимум 10 позиций
        check(variantOrder.getItems().size() == 10,
                "Maximum 10 positions");

        // Тест 20: запрет одиннадцатой позиции
        try {
            Product extra = new Product("V11", "Extra", 1000);
            variantOrder.addProduct(extra, 1);

            check(false, "Reject 11th position");
        } catch (IllegalStateException e) {
            check(true, "Reject 11th position");
        }

        // Тест 21: объединение при десяти позициях
        variantOrder.addProduct(
                new Product("V1", "Product 1", 1000), 2);

        check(variantOrder.getItems().size() == 10
                && variantOrder.getItems().get(0).getQuantity() == 3,
                "Merge with 10 positions");

        // Тест 22: подсчёт уникальных позиций
        check(variantOrder.addPositionCount(variantOrder) == 10,
                "Position count method");

        // Тест 23: добавление после удаления
        variantOrder.removeProduct("V10");

        variantOrder.addProduct(
                new Product("V11", "Extra", 1000), 1);

        check(variantOrder.getItems().size() == 10,
                "Add after removing position");

        // Тест 24: подсчёт позиций в пустом заказе
        Order emptyVariantOrder = service.createOrder(employee);

        check(emptyVariantOrder.addPositionCount(emptyVariantOrder) == 0,
                "Position count - empty order");

        // Тест 25: подсчёт позиций после удаления
        variantOrder.removeProduct("V11");

        check(variantOrder.addPositionCount(variantOrder) == 9,
                "Position count - after remove");

        // Общий результат unit-тестирования
        System.out.println("==========================");
        System.out.println("Unit tests: "
                + passed + "/" + total + " passed");

        // Запуск следующих режимов
        runDataDriven();
        runInteractive();
    }

    // =========================================
    // DATA-DRIVEN ТЕСТИРОВАНИЕ
    // =========================================

    private static void runDataDriven() {

        System.out.println("\n=== DATA-DRIVEN TESTS ===");

        int passedData = 0;
        int totalData = 0;

        int expectedPositions = -1;
        double expectedSum = -1;

        Employee employee = new Employee("E01", "Ivan Ivanov");
        Order order = new OrderService().createOrder(employee);

        try {
            // Читаем CSV-файл
            List<String> lines = Files.readAllLines(
                    Paths.get("data/sample.csv"));

            // Пропускаем заголовок
            for (int i = 1; i < lines.size(); i++) {

                String line = lines.get(i).trim();

                // Пропускаем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Разделяем строку по точке с запятой
                String[] parts = line.split(";", -1);

                // Строка с итоговыми значениями
                if (parts[0].equals("TOTAL")) {

                    expectedPositions = Integer.parseInt(
                            parts[1].split("=")[1]);

                    expectedSum = Double.parseDouble(
                            parts[2].split("=")[1]);

                    continue;
                }

                totalData++;
                boolean ok = false;

                int positionsBefore = order.getItems().size();

                try {
                    // Получаем данные товара
                    String id = parts[0];
                    String name = parts[1];

                    double price = Double.parseDouble(parts[2]);
                    int quantity = Integer.parseInt(parts[3]);

                    String expected = parts[4];

                    // Проверяем наличие товара до добавления
                    boolean existed = false;

                    for (OrderItem item : order.getItems()) {
                        if (item.getProduct().getId().equals(id)) {
                            existed = true;
                            break;
                        }
                    }

                    // Создаём и добавляем товар
                    Product product = new Product(id, name, price);
                    order.addProduct(product, quantity);

                    // Ищем фактическое количество
                    int actualQuantity = 0;

                    for (OrderItem item : order.getItems()) {
                        if (item.getProduct().getId().equals(id)) {
                            actualQuantity = item.getQuantity();
                            break;
                        }
                    }

                    // Обычное добавление
                    if (expected.startsWith("OK:qty=")) {

                        int qty = Integer.parseInt(
                                expected.substring("OK:qty=".length()));

                        ok = !existed
                                && actualQuantity == qty
                                && order.getItems().size() == positionsBefore + 1;

                    // Объединение товаров
                    } else if (expected.startsWith("OK:merged,qty=")) {

                        int qty = Integer.parseInt(
                                expected.substring("OK:merged,qty=".length()));

                        ok = existed
                                && actualQuantity == qty
                                && order.getItems().size() == positionsBefore;
                    }

                } catch (IllegalArgumentException e) {

                    // Ожидаемая ошибка входных данных
                    if (parts.length >= 5
                            && parts[4].startsWith("SKIP:")) {

                        String reason = parts[4].substring(5);

                        ok = e.getMessage() != null
                                && e.getMessage().contains(reason);
                    }

                } catch (IllegalStateException e) {

                    // Вариант 4: превышен лимит позиций
                    if (parts.length >= 5
                            && parts[4].equals("SKIP:limit")) {

                        ok = e.getMessage() != null
                                && e.getMessage().contains("10 positions");
                    }
                }

                // Результат проверки строки
                if (ok) {
                    passedData++;
                    System.out.println("PASS data/" + totalData);
                } else {
                    System.out.println("FAIL data/" + totalData);
                }
            }

            // Проверяем итоговое количество и сумму
            totalData++;

            boolean totalOk = order.getItems().size() == expectedPositions
                    && Math.abs(order.total() - expectedSum) < 0.001;

            if (totalOk) {
                passedData++;
                System.out.println("PASS data/" + totalData + " - TOTAL");
            } else {
                System.out.println("FAIL data/" + totalData + " - TOTAL");
            }

            // Состав заказа
            System.out.println("\nOrder items:");

            for (OrderItem item : order.getItems()) {

                System.out.println(
                        item.getProduct().getId()
                        + " x " + item.getQuantity()
                        + " = " + item.getLineTotal());
            }

            // Итоговые данные
            System.out.println("Total: " + order.total());
            System.out.println("Status: " + order.getStatus());
            System.out.println("Positions: " + order.getItems().size());

            // Метод варианта 4
            System.out.println("Position count: "
                    + order.addPositionCount(order));

            System.out.println("\nData-driven: "
                    + passedData + "/" + totalData + " passed");

        } catch (Exception e) {
            System.out.println("Data-driven error: " + e.getMessage());
        }
    }

    // =========================================
    // ИНТЕРАКТИВНЫЙ РЕЖИМ
    // =========================================

    @SuppressWarnings("resource")
    private static void runInteractive() {

        Scanner scanner = new Scanner(System.in);

        Employee employee = new Employee("E01", "Ivan Ivanov");
        Order order = new OrderService().createOrder(employee);

        System.out.println("\n=== INTERACTIVE MODE ===");
        System.out.println("Commands:");
        System.out.println("add <id> <name> <price> <qty>");
        System.out.println("remove <id>");
        System.out.println("items");
        System.out.println("total");
        System.out.println("confirm");
        System.out.println("status");
        System.out.println("exit");

        // Читаем команды до ввода exit
        while (true) {

            System.out.print("\n> ");

            // Завершаем работу, если ввод закрыт
            if (!scanner.hasNextLine()) {
                break;
            }

            String command = scanner.nextLine().trim();

            if (command.isEmpty()) {
                continue;
            }

            String[] parts = command.split("\\s+");

            try {

                // Добавление товара
                if (parts[0].equalsIgnoreCase("add")) {

                    if (parts.length != 5) {
                        System.out.println(
                                "Usage: add <id> <name> <price> <qty>");
                        continue;
                    }

                    String id = parts[1];
                    String name = parts[2];

                    double price = Double.parseDouble(parts[3]);
                    int quantity = Integer.parseInt(parts[4]);

                    Product product = new Product(id, name, price);

                    order.addProduct(product, quantity);

                    System.out.println("Product added");

                // Удаление товара
                } else if (parts[0].equalsIgnoreCase("remove")) {

                    if (parts.length != 2) {
                        System.out.println("Usage: remove <id>");
                        continue;
                    }

                    order.removeProduct(parts[1]);
                    System.out.println("Remove command completed");

                // Просмотр позиций
                } else if (parts[0].equalsIgnoreCase("items")) {

                    if (order.getItems().isEmpty()) {
                        System.out.println("Order is empty");
                    } else {

                        for (OrderItem item : order.getItems()) {
                            System.out.println(
                                    item.getProduct().getId()
                                    + " x " + item.getQuantity()
                                    + " = " + item.getLineTotal());
                        }
                    }

                // Общая стоимость
                } else if (parts[0].equalsIgnoreCase("total")) {

                    System.out.println("Total: " + order.total());

                // Подтверждение
                } else if (parts[0].equalsIgnoreCase("confirm")) {

                    order.confirm();
                    System.out.println("Order confirmed");

                // Статус заказа
                } else if (parts[0].equalsIgnoreCase("status")) {

                    System.out.println("Status: " + order.getStatus());

                // Завершение работы
                } else if (parts[0].equalsIgnoreCase("exit")) {

                    System.out.println("Goodbye!");
                    break;

                } else {
                    System.out.println("Unknown command");
                }

            } catch (IllegalArgumentException e) {

                System.out.println("Input error: " + e.getMessage());

            } catch (IllegalStateException e) {

                System.out.println("Order error: " + e.getMessage());
            }
        }
    }
}
