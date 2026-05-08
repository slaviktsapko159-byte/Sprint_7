package order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private static final String ORDERS_PATH = "/api/v1/orders";
    private static final String CANCEL_PATH = "/api/v1/orders/cancel";

    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(order)
                .post(ORDERS_PATH);
    }

    @Step("Отмена заказа по track")
    public Response cancelOrder(int track) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body("{\"track\": " + track + "}")
                .post(CANCEL_PATH);
    }
}
