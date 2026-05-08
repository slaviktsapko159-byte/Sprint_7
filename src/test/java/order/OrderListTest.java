package order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверяем, что GET /api/v1/orders возвращает 200 и поле orders не null")
    public void getOrdersListTest() {
        given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .get("/api/v1/orders")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("orders", notNullValue());
    }
}