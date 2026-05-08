package utils;

import models.Order;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static final Random random = new Random();

    public static String getRandomLogin() {
        return "login_" + System.currentTimeMillis() + "_" + random.nextInt(9999);
    }

    public static String getRandomPassword() {
        return "pass" + random.nextInt(9999);
    }

    public static String getRandomFirstName() {
        return "Имя" + random.nextInt(9999);
    }

    public static Order getOrderWithColor(List<String> color) {
        return new Order(
                "Алексей",
                "Иванов",
                "ул. Ленина, 1",
                "Сокольники",
                "+79991234567",
                5,
                "2025-06-01",
                "Позвоните за час",
                color
        );
    }
}
