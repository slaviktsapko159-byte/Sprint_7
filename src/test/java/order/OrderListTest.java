package order;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    @Test
    @DisplayName("Получение списка заказов - возвращается непустой список")
    public void getOrdersList() {
        given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
