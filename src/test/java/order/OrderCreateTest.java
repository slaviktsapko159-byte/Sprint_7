package order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.Order;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utils.DataGenerator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private final List<String> color;
    private int trackNumber;
    private final OrderClient orderClient = new OrderClient();

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Parameters(name = "Цвета: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()} // без цвета
        });
    }

    @After
    public void tearDown() {
        if (trackNumber > 0) {
            orderClient.cancelOrder(trackNumber);
        }
    }

    @Test
    @DisplayName("Создание заказа с различными цветами")
    @Description("Проверяем, что заказ с указанными цветами успешно создаётся и возвращает track")
    public void createOrderWithColorsTest() {
        Order order = DataGenerator.getOrderWithColor(color);
        var response = orderClient.createOrder(order);
        response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("track", notNullValue());
        trackNumber = response.then().extract().path("track");
    }
}
